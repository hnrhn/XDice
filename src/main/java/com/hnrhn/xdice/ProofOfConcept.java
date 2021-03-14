package com.hnrhn.xdice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnrhn.xdice.enums.*;
import com.hnrhn.xdice.models.Config;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProofOfConcept {
    private static final String xDiceVersion = "2.0.0-jAlpha1";
    private static final String configCommand = "/xdice config";
    private static final String helpCommand = "/xdice help";
    private static final String configChannelName = "xdice-config";
    private static final HashMap<String, Config> configs = new HashMap<>();

    public static void joinGuild(Server server) {
        if (Arrays.stream(new File("configs").list()).collect(Collectors.toList()).contains(server.getIdAsString() + ".json")) {
            return;
        }

        var configChannel = server.createTextChannelBuilder().setName(configChannelName).create().join();
        var newConfig = new Config(
                server.getId(),
                false,
                ConfigStep.BEGIN,
                20,
                false,
                new ArrayList<>(),
                false,
                PlusBehaviour.IGNORE,
                ExplodeBehaviour.NONE,
                new ArrayList<>(),
                CritFailBehaviour.NONE
        );
        configs.put(server.getIdAsString(), newConfig);
        newConfig.save();

        var welcomeMessage = "Thank you for installing XDice.\n\n"
                + "The " + configChannelName + " channel has been created to keep your regular chat channels clean. However, all XDice commands will work from any channel, and XDice will respond in the same channel.\n\n"

                + "Right now, XDice is set up in its most basic form. Additional features can be enabled by using Configuration Mode, which can be activated by sending the following message in any channel on this server:\n"
                + "`/xdice config`\n\n"

                + "To learn how to use the features which are enabled on this server, send this message on any channel:\n"
                + "`/xdice help`\n\n"

                + "To delete the " + configChannelName + " channel, send this message:\n"
                + "`/xdice config delete`";

        configChannel.sendMessage(welcomeMessage);
    }

    public static void loggedIn(String botUsername, Collection<Server> servers) {
        System.out.println("Logged in as " + botUsername);

        var knownConfigs = Arrays
                .stream(new File("configs").list())
                .map(path -> path.replaceFirst("\\.json", ""))
                .collect(Collectors.toSet());

        for (var serverId: knownConfigs) {
            Config loadedConfig;
            try {
                loadedConfig = loadConfig(serverId);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            configs.put(serverId, loadedConfig);
            System.out.println("Loaded config for " + serverId);
        }

        var connectedServers = new HashSet<>(servers);
        var serverIds = connectedServers.stream().map(DiscordEntity::getIdAsString).collect(Collectors.toSet());
        serverIds.removeAll(knownConfigs);

        for (var serverId: serverIds) {
            var matchingServer = connectedServers.stream().filter(server -> server.getIdAsString().equals(serverId)).findFirst().orElseThrow();
            joinGuild(matchingServer);
        }

        System.out.println("All Configs loaded");
    }

    public static void onMessage(Message message) {
        var messageContent = message.getContent().toLowerCase();
        var config = configs.get(message.getServer().orElseThrow().getIdAsString());

        if (messageContent.startsWith(configCommand + " ")) {
            var channel = message.getChannel();
            for (var messagePart: configure(message).split("SPLIT")) {
                channel.sendMessage(messagePart);
            }
            throw new UnsupportedOperationException("Not Implemented");
        } else if (messageContent.equals(configCommand)) {
            if (!message.getAuthor().canManageServer()) {
                message.getChannel().sendMessage("Only server admins can configure XDice");
                return;
            }
            config.configMode = true;

            var server = message.getServer();
            if (server.isPresent()) {
                var configChannelSearch = server.get().getTextChannelsByName(configChannelName).stream().findFirst();
                ServerTextChannel configChannel;
                if (configChannelSearch.isEmpty()) {
                    configChannel = server.get().createTextChannelBuilder().setName(configChannelName).create().join();
                } else {
                    configChannel = configChannelSearch.get();
                }
                configChannel.sendMessage("XDice is now in configuration mode.\nTo continue, send a message with the contents `/xdice config begin`\nTo exit configuration mode **without saving any changes**, send `/xdice config cancel`");
            }
        } else if (messageContent.equals(helpCommand)) {
            for (String messagePart: getHelp(config).split("SPLIT")) {
                message.getChannel().sendMessage(messagePart);
            }
        } else {
            var result = rollDice(messageContent, message);
            if (result != null) {
                message.getChannel().sendMessage(message.getUserAuthor().orElseThrow().getMentionTag() + ": " + result);
            }
        }
    }

    private static String getHelp(Config config){
        var help = new ArrayList<String>();
        help.add("**= XDice v" + xDiceVersion + "=**");
        help.add("------------------------------------------------------------------------------------------------------------------------\n");
        help.add("**BASIC USAGE**");
        help.add("XDice listens for messages beginning with one of the activation phrases");
        help.add("`/roll` or `!roll`");
        help.add("then a \"dice request\" in this format:");
        help.add("`XdY + Z`");
        help.add("Therefore a full \"roll command\" looks something like this:");
        help.add("`/roll 3d12 + 5`");
        help.add("or");
        help.add("`!roll 3d12 + 5`\n");
        help.add("There is also a special roll command for randomly generating Rock/Paper/Scissors results:");
        help.add("`/rps` or `!rps`");
        help.add("SPLIT");
        help.add("------------------------------------------------------------------------------------------------------------------------\n");
        help.add("**SHORTCUTS**");
        help.add("Not every part of the roll command is required. XDice on your server has been configured with default values for X, Y, and Z to help you type less:");
        help.add("```");
        help.add("X = 1");
        help.add("Y = " + config.defaultDice);
        help.add(config.plusBehaviour == PlusBehaviour.IGNORE ? "N/A -- (Z is ignored on your server)" : "0");
        help.add("```\n");
        help.add("Spaces between the X, Y, and Z elements are optional, as are the letters **o-l-l** in the word \"roll\".");
        help.add("i.e. `!r` and `/r` work exactly the same as `!roll` and `/roll`\n");
        help.add("So for example, in this server `/r5` will roll 5d" + config.defaultDice + "s, `!r d8` will roll 1d8.");
        help.add("The smallest possible roll command is just two characters long:");
        help.add("`/r` or `!r` -> This will roll 1d" + config.defaultDice + ".");

        if (config.plusBehaviour != PlusBehaviour.IGNORE) {
            var behaviour = config.plusBehaviour == PlusBehaviour.AUTO_SUCCESS ? "add an additional success to each roll result" : "add the value of Z to the total of your rolled dice";
            help.add("\nFor your server, the number specified in the `Z` position will " + behaviour);
        }

        var extraSettingsPosition = help.size();
        help.add("IF YOU ARE SEEING THIS LINE, PETER MESSED UP");

        var extraSettings = false;

        if (config.countSuccesses) {
            extraSettings = true;
            var shuffled = new ArrayList<>(config.successOn);
            Collections.shuffle(shuffled);

            help.add("- Automatically count successes when any of the following numbers are rolled: " + config.successOn.toString());
            help.add("    **" + shuffled.toString() + " = " + config.successOn.size() + " Successes**");
        } else if (config.addTotal) {
            extraSettings = true;
            var sample = new ArrayList<Integer>();
            for (int i = config.defaultDice; i >= config.defaultDice - 3; i--) {
                sample.add(i);
            }
            help.add("\n- Automatically calculate the total of all of your rolls:");
            help.add("    **" + sample.toString() + " = " + sample.stream().mapToInt(Integer::intValue).sum() + "**");
        }

        if (config.explodeBehaviour != ExplodeBehaviour.NONE) {
            extraSettings = true;
            help.add("\n- \"Explode\" dice on rolls of the following numbers: " + config.explodeOn.toString());
            switch (config.explodeBehaviour) {
                case DOUBLE -> help.add("    - Each exploded die will add an extra success to the Successes counter.");
                case EXTRA -> help.add("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice do not explode.");
                case EXTRA_CHAIN -> help.add("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice can also explode.");
            }
        }

        if (config.critFailBehaviour == CritFailBehaviour.ONE_NO_SUCCESSES) {
            extraSettings = true;
            help.add("\n- Add a Critical Fail warning when you roll at least one 1 and no Successes:");
            help.add("    **[1, 1, 1] = 3 Critical Fails**");
        }

        if (extraSettings) {
            help.set(extraSettingsPosition, "SPLIT\n------------------------------------------------------------------------------------------------------------------------\n\n**SPECIAL SETTINGS**\nFor your server, XDice has also been configured to do the following when you roll d" + config.defaultDice + "s");
        } else {
            help.remove(extraSettingsPosition);
        }

        return String.join("\n", help);
    }

    private static String rollDice(String rollString, Message message) {
        var rng = new Random();

        if (rollString.strip().equals("/rps") || rollString.strip().equals("!rps")) {
            return rockPaperScissors(rng);
        }

        if (rollString.strip().equals("/coin") || rollString.strip().equals("!coin")) {
            return flipCoin(rng);
        }

        var config = configs.get(message.getServer().orElseThrow().getIdAsString());

        var pattern = Pattern.compile("[!/]r(?:oll)*\\s*(\\d*)\\s*d*\\s*(\\d*)(?:\\s*\\++\\s*(\\d*))?");
        var matcher = pattern.matcher(rollString);
        if (!matcher.matches()) {
            return null;
        }

        int diceToRoll;
        if (matcher.group(1) == null || matcher.group(1).isEmpty()) {
            diceToRoll = 1;
        } else {
            diceToRoll = Integer.parseInt(matcher.group(1));
        }

        int diceType;
        if (matcher.group(2) == null || matcher.group(2).isEmpty()) {
            diceType = config.defaultDice;
        } else {
            diceType = Integer.parseInt(matcher.group(2));
        }

        var rolledDice = new ArrayList<Integer>();

        for (int i = 0; i < diceToRoll; i++) {
            rolledDice.add(roll(rng, diceType));
        }

        int successes = 0;
        if (!(matcher.group(3) == null || matcher.group(3).isEmpty()) && config.plusBehaviour == PlusBehaviour.AUTO_SUCCESS) {
            successes = Integer.parseInt(matcher.group(3));
        }

        if (config.explodeBehaviour == ExplodeBehaviour.EXTRA) {
            for (var rolledDie: rolledDice) {
                if (config.explodeOn.contains(rolledDie)) {
                    rolledDice.add(roll(rng, diceType));
                }
            }
        } else if (config.explodeBehaviour == ExplodeBehaviour.EXTRA_CHAIN) {
            var explodingDiceCount = rolledDice.stream().filter(die -> config.explodeOn.contains(die)).count();
            while (explodingDiceCount > 0) {
                var newDice = new ArrayList<Integer>();
                for (int i = 0; i < explodingDiceCount; i++){
                    newDice.add(roll(rng, diceType));
                }
                explodingDiceCount = newDice.stream().filter(die -> config.explodeOn.contains(die)).count();
            }
        } else if (config.explodeBehaviour == ExplodeBehaviour.DOUBLE) {
            successes += rolledDice.stream().filter(die -> config.explodeOn.contains(die)).count();
        }

        var result = rolledDice.toString();

        if (config.addTotal) {
            return result + " = " + rolledDice.stream().mapToInt(roll -> roll).sum();
        }

        if (!config.countSuccesses || diceType != config.defaultDice) {
            return result;
        }

        var critFails = 0;
        for (var die: rolledDice) {
            if (config.successOn.contains(die)) {
                successes++;
            } else if (die == 1) {
                critFails++;
            }
        }

        if (successes == 0 && critFails > 0 && config.critFailBehaviour == CritFailBehaviour.ONE_NO_SUCCESSES) {
            return result + " = " + critFails + " Critical Fail" + (critFails == 1 ? "" : "s");
        }

        return result + " = " + successes + " Success" + (successes == 1 ? "" : "es");
    }

    private static String rockPaperScissors(Random rng) {
        var result = rng.nextInt(3);
        return switch (result) {
            case 0 -> "Rock";
            case 1 -> "Paper";
            case 2 -> "Scissors";
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    private static String flipCoin(Random rng) {
        var result = rng.nextInt(2);
        return switch (result) {
            case 0 -> "Heads";
            case 1 -> "Tails";
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    private static Integer roll(Random rng, Integer diceType) {
        return (int) Math.floor((rng.nextInt(diceType * 10 + 9) + 12) / 10.0);
    }

    private static Config loadConfig(String serverId) throws IOException {
        var configString = Files.readString(Paths.get("configs/" + serverId + ".json"));
        var mapper = new ObjectMapper();
        return mapper.readValue(configString, Config.class);
    }

    private static String configure(Message message) {
        if (!message.getAuthor().canManageServer() || !message.getContent().toLowerCase().startsWith(configCommand)) {
            return message.getUserAuthor().orElseThrow().getMentionTag() + ": XDice is being configured by your server's admin(s), and is currently unavailable.";
        }

        var serverId = message.getServer().orElseThrow().getIdAsString();
        var config = configs.get(serverId);
        var command = message.getContent().toLowerCase().replaceFirst("/xdice config ", "");

        if (command.equals("delete")) {
            var configChannel = message.getServer().orElseThrow().getTextChannelsByName(configChannelName).stream().findFirst().orElseThrow();
            configChannel.delete();
            return "";
        }

        if (!config.configMode) {
            return "XDice is not in configuration mode.";
        }

        if (command.equals("cancel")) {
            try {
                configs.put(serverId, loadConfig(serverId));
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to reload previous config. Config is now in an unpredictable state.";
            }
            return "Discarding all changes and exiting configuration mode.\n"
                    + "Send the command `/xdice delete` to remove the " + configChannelName + " channel.";
        }

        try
        {
            var split = command.split(" ");
            var currentStep = split[0];
            var commandParam = split.length == 2 ? split[1] : "";

            if (!currentStep.equals(config.currentConfigStep.toString().toLowerCase()))
            {
                throw new Exception();
            }

            if (currentStep.equals(ConfigStep.BEGIN.toString()))
            {
                config.currentConfigStep = ConfigStep.DEFAULT_DICE;
                return MessageFormat.format(ProofOfConceptConstants.configHelperBegin, new Random().nextInt(10001) + 1000, ConfigStep.DEFAULT_DICE.toString());
            }

            if (currentStep.equals(ConfigStep.DEFAULT_DICE.toString()))
            {
                var selection = commandParam.replace("d", ""); // In case anyone types "d20" instead of "20".
                config.defaultDice = Integer.parseInt(selection);
                config.currentConfigStep = ConfigStep.COUNT_SUCCESSES;
                return MessageFormat.format(ProofOfConceptConstants.configHelperCountSuccesses, selection);
            }

            if (currentStep.equals(ConfigStep.COUNT_SUCCESSES.toString()))
            {
                switch (commandParam) {
                    case "yes" -> {
                        config.countSuccesses = true;
                        config.currentConfigStep = ConfigStep.SUCCESS_ON;
                        return MessageFormat.format(ProofOfConceptConstants.configHelperSuccessOn, ConfigStep.SUCCESS_ON.toString());
                    }
                    case "no" -> {
                        config.countSuccesses = false;
                        config.currentConfigStep = ConfigStep.ADD_TOTAL;
                        return MessageFormat.format(ProofOfConceptConstants.configHelperAddTotal, ConfigStep.ADD_TOTAL.toString());
                    }
                }
            }

            if (currentStep.equals(ConfigStep.SUCCESS_ON.toString()))
            {
                config.successOn = Arrays.stream(split).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                config.currentConfigStep = ConfigStep.PLUS_BEHAVIOUR;

                return MessageFormat.format("Nice!\n\n" + ProofOfConceptConstants.configHelperPlusBehaviour, ConfigStep.PLUS_BEHAVIOUR.toString(), "~~", "");
            }

            if (currentStep.equals(ConfigStep.ADD_TOTAL.toString()))
            {
                switch (commandParam) {
                    case "yes" -> {
                        config.addTotal = true;
                        config.currentConfigStep = ConfigStep.PLUS_BEHAVIOUR;
                        return "Nice! A big ol' YES on adding up your totals. You got it!\n\n" + MessageFormat.format(ProofOfConceptConstants.configHelperPlusBehaviour, ConfigStep.PLUS_BEHAVIOUR.toString(), "", "~~");
                    }
                    case "no" -> {
                        config.addTotal = false;
                        config.currentConfigStep = ConfigStep.EXPLODE_BEHAVIOUR;
                        return "No assistance needed on the ol' numbers, you've got it!\n\n" + MessageFormat.format(ProofOfConceptConstants.configHelperExplodeBehaviour, ConfigStep.EXPLODE_BEHAVIOUR.toString(), config.plusBehaviour == PlusBehaviour.ADD ? "~~" : "");
                    }
                }
            }

            if (currentStep.equals(ConfigStep.PLUS_BEHAVIOUR.toString()))
            {
                var chosenNumber = Integer.parseInt(commandParam);
                if (PlusBehaviourHelper.validIntegers.contains(chosenNumber))
                {
                    config.plusBehaviour = PlusBehaviourHelper.toEnum(chosenNumber);
                    config.currentConfigStep = ConfigStep.EXPLODE_BEHAVIOUR;
                }
                else
                {
                    throw new Exception();
                }

                return "Done and done!\n\n" + MessageFormat.format(ProofOfConceptConstants.configHelperExplodeBehaviour, ConfigStep.EXPLODE_BEHAVIOUR.toString(), config.plusBehaviour == PlusBehaviour.ADD ? "~~" : "");
            }

            if (currentStep.equals(ConfigStep.EXPLODE_BEHAVIOUR.toString()))
            {
                var chosenNumber = Integer.parseInt(commandParam);
                var chosenEnum = ExplodeBehaviourHelper.toEnum(chosenNumber);
                if (ExplodeBehaviourHelper.validIntegers.contains(chosenNumber))
                {
                    config.explodeBehaviour = chosenEnum;
                }
                else
                {
                    throw new Exception();
                }

                if (chosenEnum != ExplodeBehaviour.NONE)
                {
                    config.currentConfigStep = ConfigStep.EXPLODE_ON;
                    return MessageFormat.format(ProofOfConceptConstants.configHelperExplodeOn, ConfigStep.EXPLODE_ON.toString());
                }

                if (config.countSuccesses)
                {
                    config.currentConfigStep = ConfigStep.CRIT_FAIL_BEHAVIOUR;
                    return MessageFormat.format(ProofOfConceptConstants.configHelperCritFail, ConfigStep.CRIT_FAIL_BEHAVIOUR.toString());
                }

                config.currentConfigStep = ConfigStep.CONFIRM;
                return MessageFormat.format(ProofOfConceptConstants.configHelperConfirm, ConfigStep.CONFIRM.toString(), getHelp(config));
            }

            if (currentStep.equals(ConfigStep.EXPLODE_ON.toString()))
            {
                config.explodeOn = Arrays.stream(split).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                config.currentConfigStep = ConfigStep.CRIT_FAIL_BEHAVIOUR;
                return MessageFormat.format(ProofOfConceptConstants.configHelperCritFail, ConfigStep.CRIT_FAIL_BEHAVIOUR.toString());
            }

            if (currentStep.equals(ConfigStep.CRIT_FAIL_BEHAVIOUR.toString()))
            {
                switch (commandParam) {
                    case "yes" -> config.critFailBehaviour = CritFailBehaviour.ONE_NO_SUCCESSES;
                    case "no" -> config.critFailBehaviour = CritFailBehaviour.NONE;
                    default -> throw new IllegalStateException("Invalid command parameter: " + commandParam);
                }

                config.currentConfigStep = ConfigStep.CONFIRM;
                return MessageFormat.format(ProofOfConceptConstants.configHelperConfirm, ConfigStep.CONFIRM.toString(), getHelp(config));
            }

            if (currentStep.equals(ConfigStep.CONFIRM.toString()))
            {
                config.currentConfigStep = ConfigStep.BEGIN;
                config.save();
                config.configMode = false;
                return "";
            }

            throw new Exception();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Uh oh, that didn't work. Check your spelling and try again!";
        }
    }
}

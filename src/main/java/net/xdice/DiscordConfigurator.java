package net.xdice;

import net.xdice.enums.*;
import net.xdice.interfaces.HelpGenerator;
import net.xdice.interfaces.XDiceRepository;
import net.xdice.models.XDiceConfig;
import net.xdice.utilities.ConfigStepConverter;
import net.xdice.utilities.ExplodeBehaviourConverter;
import net.xdice.utilities.PlusBehaviourConverter;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DiscordConfigurator {
    private final XDiceRepository configRepository;
    private final HelpGenerator helpGenerator;

    public DiscordConfigurator(XDiceRepository repository, HelpGenerator help) {
        configRepository = repository;
        helpGenerator = help;
    }

    public String configure(Message message) {  // TODO: Decouple this from Discord features.
        if (!message.getAuthor().canManageServer()) {
            return message.getUserAuthor().orElseThrow().getMentionTag() + ": You do not have permission to manage XDice on this server.";
        }

        long serverId = message.getServer().orElseThrow().getId();

        XDiceConfig config;
        try {
            config = configRepository.getConfig(serverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR: Could not load an existing configuration for your server.";
        }
        String command = message.getContent().toLowerCase().replaceFirst("/xdice config ", "");

        if (command.equals("delete")) {
            ServerTextChannel configChannel = message.getServer().orElseThrow().getTextChannelsByName(Constants.configChannelName).stream().findFirst().orElseThrow();
            configChannel.delete();
            return "";
        }

        //TODO: Write a force-reload function

        try
        {
            String[] split = command.split(" ");
            ConfigStep currentStep = ConfigStepConverter.stringToEnum(split[0]);
            String commandParam = split.length == 2 ? split[1] : "";

            if (!currentStep.equals(config.getCurrentConfigStep()))
            {
                throw new Exception();
            }

            return switch (currentStep) {
                case BEGIN -> {
                    config.setCurrentConfigStep(ConfigStep.DEFAULT_DICE);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format(ConfigInstructions.begin, new Random().nextInt(9000) + 1000, ConfigStep.DEFAULT_DICE.toString().toLowerCase());
                }
                case DEFAULT_DICE -> {
                    String selection = commandParam.replace("d", ""); // In case anyone types "d20" instead of "20".
                    config.setDefaultDice(Integer.parseInt(selection));
                    config.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format(ConfigInstructions.countSuccesses, selection);
                }
                case COUNT_SUCCESSES -> switch (commandParam) {
                    case "yes" -> {
                        config.setCountSuccesses(true);
                        config.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
                        configRepository.saveConfig(config);
                        yield MessageFormat.format(ConfigInstructions.successOn, ConfigStep.SUCCESS_ON.toString().toLowerCase());
                    }
                    case "no" -> {
                        config.setCountSuccesses(false);
                        config.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
                        configRepository.saveConfig(config);
                        yield MessageFormat.format(ConfigInstructions.addTotal, ConfigStep.ADD_TOTAL.toString().toLowerCase());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + commandParam);
                };
                case SUCCESS_ON -> {
                    config.setSuccessOn(Arrays.stream(split).skip(1).map(Integer::parseInt).collect(Collectors.toList()));
                    config.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format("Nice!\n\n" + ConfigInstructions.plusBehaviour, ConfigStep.PLUS_BEHAVIOUR.toString().toLowerCase(), "~~", "");
                }
                case ADD_TOTAL -> switch (commandParam) {
                    case "yes" -> {
                        config.setAddTotal(true);
                        config.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
                        configRepository.saveConfig(config);
                        yield "Nice! A big ol' YES on adding up your totals. You got it!\n\n" + MessageFormat.format(ConfigInstructions.plusBehaviour, ConfigStep.PLUS_BEHAVIOUR.toString().toLowerCase(), "", "~~");
                    }
                    case "no" -> {
                        config.setAddTotal(false);
                        config.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
                        configRepository.saveConfig(config);
                        yield "No assistance needed on the ol' numbers, you've got it!\n\n" + MessageFormat.format(ConfigInstructions.explodeBehaviour, ConfigStep.EXPLODE_BEHAVIOUR.toString().toLowerCase(), config.isCountSuccesses() ? "" : "~~");
                    }
                    default -> throw new IllegalStateException();
                };
                case PLUS_BEHAVIOUR -> {
                    int chosenNumber = Integer.parseInt(commandParam);
                    config.setPlusBehaviour(PlusBehaviourConverter.intToEnum(chosenNumber));
                    config.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
                    configRepository.saveConfig(config);
                    yield "Done and done!\n\n" + MessageFormat.format(ConfigInstructions.explodeBehaviour, ConfigStep.EXPLODE_BEHAVIOUR.toString().toLowerCase(), config.isCountSuccesses() ? "" : "~~");
                }
                case EXPLODE_BEHAVIOUR -> {
                    int chosenNumber = Integer.parseInt(commandParam);
                    ExplodeBehaviour chosenEnum = ExplodeBehaviourConverter.intToEnum(chosenNumber);
                    config.setExplodeBehaviour(chosenEnum);

                    if (chosenEnum != ExplodeBehaviour.NONE)
                    {
                        config.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
                        configRepository.saveConfig(config);
                        yield MessageFormat.format(ConfigInstructions.explodeOn, ConfigStep.EXPLODE_ON.toString().toLowerCase());
                    }

                    if (config.isCountSuccesses())
                    {
                        config.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
                        configRepository.saveConfig(config);
                        yield MessageFormat.format(ConfigInstructions.critFail, ConfigStep.CRIT_FAIL_BEHAVIOUR.toString().toLowerCase());
                    }

                    config.setCurrentConfigStep(ConfigStep.CONFIRM);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format(ConfigInstructions.finalConfirmation, ConfigStep.CONFIRM.toString().toLowerCase(), helpGenerator.getHelp(config));
                }
                case EXPLODE_ON -> {
                    List<Integer> args = Arrays.stream(split).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                    if (args.isEmpty()) {
                        throw new Exception("No numbers chosen");
                    }
                    config.setExplodeOn(args);
                    config.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format(ConfigInstructions.critFail, ConfigStep.CRIT_FAIL_BEHAVIOUR.toString().toLowerCase());
                }
                case CRIT_FAIL_BEHAVIOUR -> {
                    switch (commandParam) {
                        case "yes" -> config.setCritFailBehaviour(CritFailBehaviour.ONE_NO_SUCCESSES);
                        case "no" -> config.setCritFailBehaviour(CritFailBehaviour.NONE);
                        default -> throw new IllegalStateException("Invalid command parameter: " + commandParam);
                    }

                    config.setCurrentConfigStep(ConfigStep.CONFIRM);
                    configRepository.saveConfig(config);
                    yield MessageFormat.format(ConfigInstructions.finalConfirmation, ConfigStep.CONFIRM.toString().toLowerCase(), helpGenerator.getHelp(config));
                }
                case CONFIRM -> {
                    config.setCurrentConfigStep(ConfigStep.BEGIN);
                    config.setConfigMode(false);
                    configRepository.saveConfig(config);
                    yield ConfigInstructions.signOff;
                }
            };
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ConfigInstructions.inputError;
        }
    }
}

package net.xdice;

import net.xdice.enums.PlusBehaviour;
import net.xdice.enums.CommandType;
import net.xdice.interfaces.XDiceParser;
import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardParser implements XDiceParser {
    private static final Pattern rollPattern = Pattern.compile("^[!/]r(?:oll)*\\s*(\\d*)\\s*d*\\s*(\\d*)(?:\\s*\\++\\s*(\\d*))? *$");
    private static final Pattern wtfPattern = Pattern.compile("(wtf|dicebot|xdice|what the fuck|what are you doing),? (?!\1)(wtf|dicebot|xdice|what the fuck|what are you doing)");
    private static final Pattern thanksPattern = Pattern.compile("(?:thank you|thanks),? (?:xdice|dicebot)");
    private static final Pattern insultPattern = Pattern.compile("(?:fuck you|screw you|i hate you|fuck off|go away),? (?:xdice|dicebot)");
    private static final Pattern lovePattern = Pattern.compile("(i love you|ily|i would die for you|dicebot|xdice|<3),? (?!\1)(i love you|ily|i would die for you|dicebot|xdice|<3)");

    public XDiceCommand parseCommandString(String commandString, XDiceConfig config) {
        String lowerCaseCommand = commandString.toLowerCase().strip();
        String commandWithPrefixRemoved = lowerCaseCommand.substring(1);

        XDiceCommand command = new XDiceCommand();

        Matcher rollMatcher = rollPattern.matcher(lowerCaseCommand);
        if (rollMatcher.find()) {
            command.setCommandType(CommandType.DICE);

            command.setNumberOfDice(
                    rollMatcher.group(1) == null || rollMatcher.group(1).isEmpty()
                            ? 1
                            : Integer.parseInt(rollMatcher.group(1))
            );

            command.setTypeOfDice(
                    rollMatcher.group(2) == null || rollMatcher.group(2).isEmpty()
                            ? config.getDefaultDice()
                            : Integer.parseInt(rollMatcher.group(2))
            );

            command.setModifier(
                    (rollMatcher.group(3) == null || rollMatcher.group(3).isEmpty()) || config.getPlusBehaviour() == PlusBehaviour.IGNORE    // TODO: Remove this so config is not needed for parse.
                            ? 0
                            : Integer.parseInt(rollMatcher.group(3))
            );

            return command;
        }

        if (commandWithPrefixRemoved.equals("rps")) {
            command.setCommandType(CommandType.ROCK_PAPER_SCISSORS);
            return command;
        }

        if (commandWithPrefixRemoved.equals("coin")) {
            command.setCommandType(CommandType.COIN);
            return command;
        }

        if (commandWithPrefixRemoved.startsWith("xdice help")) {
            command.setCommandType(CommandType.HELP);;
            return command;
        }

        if (commandWithPrefixRemoved.startsWith("xdice config")) {
            command.setCommandType(CommandType.CONFIG);
            return command;
        }

        Matcher wtfMatcher = wtfPattern.matcher(lowerCaseCommand);
        if (wtfMatcher.find()) {
            command.setCommandType(CommandType.WTF);
            return command;
        }

        Matcher thanksMatcher = thanksPattern.matcher(lowerCaseCommand);
        if (thanksMatcher.find()) {
            command.setCommandType(CommandType.THANKS);
            return command;
        }

        Matcher insultMatcher = insultPattern.matcher(lowerCaseCommand);
        if (insultMatcher.find()) {
            command.setCommandType(CommandType.INSULT);
            return command;
        }

        Matcher loveMatcher = lovePattern.matcher(lowerCaseCommand);
        if (loveMatcher.find()) {
            command.setCommandType(CommandType.LOVE);
            return command;
        }

        command.setCommandType(CommandType.INVALID);
        return command;
    }
}

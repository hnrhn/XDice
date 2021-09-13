package net.xdice.forbiddenlands;

import net.xdice.StandardParser;
import net.xdice.enums.CommandType;
import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FLParser extends StandardParser {
    private static final Pattern flPattern = Pattern.compile("^/r *(\\d+) +(\\d+) +(\\d+) *(m|e|l|d?8|d?10|d?12)?");

    @Override
    public XDiceCommand parseCommandString(String commandString, XDiceConfig config) {
        String lowerCaseCommand = commandString.toLowerCase().strip();

        XDiceCommand command = new XDiceCommand();

        if (commandString.startsWith("/push")) {
            command.setCommandType(CommandType.FL_PUSH);
            return command;
        }

        if (commandString.startsWith("/pride")) {
            command.setCommandType(CommandType.FL_PRIDE);
            return command;
        }

        if (commandString.startsWith("/newdeck")) {
            command.setCommandType(CommandType.FL_CREATE_NEW_DECK);
            return command;
        }

        if (commandString.startsWith("/draw")) {
            command.setCommandType(CommandType.FL_DRAW_INITIATIVE);
            return command;
        }

        if (commandString.startsWith("/help")) {
            command.setCommandType(CommandType.HELP);
            return command;
        }

        Matcher flRollMatcher = flPattern.matcher(lowerCaseCommand);
        if (flRollMatcher.find()) {
            int base = Integer.parseInt(flRollMatcher.group(1));
            int skill = Integer.parseInt(flRollMatcher.group(2));
            int gear = Integer.parseInt(flRollMatcher.group(3));

            Integer artifact = null;
            if (flRollMatcher.group(4) != null) {
                switch (flRollMatcher.group(4)) {
                    case "m", "8" -> artifact = 8;
                    case "e", "10" -> artifact = 10;
                    case "l", "12" -> artifact = 12;
                }
            }

            command.setCommandType(CommandType.FL_ROLL);
            command.setFallenKingdomsDice(new FLDice(base, skill, gear, artifact, false));
            return command;
        }

        return super.parseCommandString(commandString, config);
    }
}

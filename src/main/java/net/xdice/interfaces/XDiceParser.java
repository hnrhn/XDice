package net.xdice.interfaces;

import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;

public interface XDiceParser {
    XDiceCommand parseCommandString(String commandString, XDiceConfig config);
}

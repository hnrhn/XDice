package net.xdice.interfaces;

import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;

public interface XDiceRoller {
    String rollDice(XDiceCommand command, XDiceConfig config);
    String rockPaperScissors();
    String flipCoin();
}

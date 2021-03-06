using System.Collections.Generic;

namespace XDice.Interfaces
{
    public interface IDiceRoller
    {
        List<int> RollDice(int numberOfDice, int numberOfSides, bool luckyModeActive = true);

        string RockPaperScissors();

        string CoinFlip();
    }
}
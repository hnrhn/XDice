using System;
using System.Collections.Generic;
using XDice.Interfaces;

namespace XDice.Implementations
{
    public class GenericDiceRoller : IDiceRoller
    {
        private readonly Random _randomNumberGenerator;

        public GenericDiceRoller()
        {
            _randomNumberGenerator = new Random();
        }
        
        public List<int> RollDice(int numberOfDice, int numberOfSides, bool luckyModeActive = true)
        {
            var resultArray = new List<int>();

            if (luckyModeActive)
            {
                for (var i = 0; i < numberOfDice; i++)
                {
                    // TODO: Explain "Lucky Mode"
                    resultArray.Add(_randomNumberGenerator.Next(12, numberOfSides * 10 + 10) / 10);
                }
            }
            else
            {
                for (var i = 0; i < numberOfDice; i++)
                {
                    resultArray.Add(_randomNumberGenerator.Next(1, numberOfSides + 1));
                }
            }

            return resultArray;
        }
        
        public string RockPaperScissors()
        {
            return _randomNumberGenerator.Next(3) switch
            {
                0 => "Rock",
                1 => "Paper",
                2 => "Scissors",
                _ => throw new ArgumentOutOfRangeException()
            };
        }

        public string CoinFlip()
        {
            return _randomNumberGenerator.Next(3) switch
            {
                0 => "Heads",
                1 => "Tails",
                _ => throw new ArgumentOutOfRangeException()
            };
        }
    }
}
using System;
using System.Linq;
using System.Text.RegularExpressions;
using XDice.Enums;
using XDice.Interfaces;

namespace XDice.Implementations
{
    // TODO: This is currently a straight-up rewrite of the Python version, with no splitting SOC.
    public class SimpleCommandParser : ICommandParser
    {
        private static readonly Regex RollCommandRegex = new Regex(@"[!/]r(?:oll)*\s*(\d*)\s*d*\s*(\d*)(?:\s*\++\s*(\d*))?");
        
        public string? GetResult(string command, string serverId)
        {
            var diceRoller = new GenericDiceRoller();

            var config = ConfigLoader.Load(serverId);
            if (command.StartsWith("/rps") || command.StartsWith("!rps"))
            {
                return diceRoller.RockPaperScissors();
            }

            var match = RollCommandRegex.Match(command);
            if (!match.Success)
            {
                return null;
            }

            var diceToRoll = string.IsNullOrEmpty(match.Groups[1].Value)
                ? 1 
                : int.Parse(match.Groups[1].Value);

            var diceType = string.IsNullOrEmpty(match.Groups[2].Value)
                ? config.DefaultDice
                : int.Parse(match.Groups[2].Value);

            var rolledDice = diceRoller.RollDice(diceToRoll, diceType);
            int successes;
            if (!string.IsNullOrEmpty(match.Groups[3].Value) && config.PlusBehaviour == PlusBehaviour.AutoSuccess)
            {
                successes = int.Parse(match.Groups[3].Value);
            }
            else
            {
                successes = 0;
            }

            switch (config.ExplodeBehaviour)
            {
                case ExplodeBehaviour.Extra:
                {
                    var explodingDiceCount = rolledDice.Count(rolledDie => config.ExplodeOn.Contains(rolledDie));
                    rolledDice.AddRange(diceRoller.RollDice(explodingDiceCount, diceType));
                    break;
                }
                case ExplodeBehaviour.ExtraWithChaining:
                {
                    var explodingDiceCount = rolledDice.Count(rolledDie => config.ExplodeOn.Contains(rolledDie));
                    while (explodingDiceCount > 0)
                    {
                        var newDice = diceRoller.RollDice(explodingDiceCount, diceType);
                        rolledDice.AddRange(newDice);
                        explodingDiceCount = newDice.Count(rolledDie => config.ExplodeOn.Contains(rolledDie));
                    }
                    break;   
                }
                case ExplodeBehaviour.Double:
                {
                    successes += rolledDice.Count(rolledDie => config.ExplodeOn.Contains(rolledDie));
                    break;
                }
                case ExplodeBehaviour.None:
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }

            var resultString = rolledDice.ToString();

            if (config.AddTotalModeActive)
            {
                return $"{resultString} = {rolledDice.Sum()}";
            }

            if (!config.CountSuccessesModeActive || diceType != config.DefaultDice)
            {
                return resultString;
            }

            successes += rolledDice.Count(rolledDie => config.SuccessOn.Contains(rolledDie));
            var critFails = rolledDice.Count(rolledDie => rolledDie == 1);

            if (successes == 0 && critFails > 0 && config.CritFailBehaviour == CritFailBehaviour.OnesWithNoSuccesses)
            {
                return $"{resultString} = {critFails} Critical Fail{(critFails == 1 ? "" : "s")}";
            }

            return $"{resultString} = {successes} Success{(successes == 1 ? "" : "es")}";
        }
    }
}
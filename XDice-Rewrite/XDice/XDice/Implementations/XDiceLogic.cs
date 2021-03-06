using System;
using XDice.Enums;
using XDice.Interfaces;

namespace XDice.Implementations
{
    public class XDiceLogic : IXDiceLogic
    {
        private readonly ICommandParser _commandParser;
        private readonly IDiceRoller _diceRoller;

        public XDiceLogic(ICommandParser commandParser, IDiceRoller diceRoller)
        {
            _commandParser = commandParser;
            _diceRoller = diceRoller;
        }

        public string? GetResult(string rawCommand)
        {
            var command = _commandParser.Parse(rawCommand);
            
            var rolledDice = _diceRoller.RollDice(command.NumberOfDice, command.SidesOnDice);
            int successes;
            if (!string.IsNullOrEmpty(match.Groups[3].Value) && _config.PlusBehaviour == PlusBehaviour.AutoSuccess)
            {
                successes = int.Parse(match.Groups[3].Value);
            }
            else
            {
                successes = 0;
            }

            switch (_config.ExplodeBehaviour)
            {
                case ExplodeBehaviour.Extra:
                {
                    var explodingDiceCount = rolledDice.Count(rolledDie => _config.ExplodeOn.Contains(rolledDie));
                    rolledDice.AddRange(_diceRoller.RollDice(explodingDiceCount, diceType));
                    break;
                }
                case ExplodeBehaviour.ExtraWithChaining:
                {
                    var explodingDiceCount = rolledDice.Count(rolledDie => _config.ExplodeOn.Contains(rolledDie));
                    while (explodingDiceCount > 0)
                    {
                        var newDice = _diceRoller.RollDice(explodingDiceCount, diceType);
                        rolledDice.AddRange(newDice);
                        explodingDiceCount = newDice.Count(rolledDie => _config.ExplodeOn.Contains(rolledDie));
                    }
                    break;   
                }
                case ExplodeBehaviour.Double:
                {
                    successes += rolledDice.Count(rolledDie => _config.ExplodeOn.Contains(rolledDie));
                    break;
                }
                case ExplodeBehaviour.None:
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }

            var resultString = rolledDice.ToString();

            if (_config.AddTotalModeActive)
            {
                return $"{resultString} = {rolledDice.Sum()}";
            }

            if (!_config.CountSuccessesModeActive || diceType != _config.DefaultDice)
            {
                return resultString;
            }

            successes += rolledDice.Count(rolledDie => _config.SuccessOn.Contains(rolledDie));
            var critFails = rolledDice.Count(rolledDie => rolledDie == 1);

            if (successes == 0 && critFails > 0 && _config.CritFailBehaviour == CritFailBehaviour.OnesWithNoSuccesses)
            {
                return $"{resultString} = {critFails} Critical Fail{(critFails == 1 ? "" : "s")}";
            }

            return $"{resultString} = {successes} Success{(successes == 1 ? "" : "es")}";
        }
    }
}
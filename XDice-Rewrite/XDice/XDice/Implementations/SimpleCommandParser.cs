using System;
using System.Linq;
using System.Text.RegularExpressions;
using XDice.Enums;
using XDice.Interfaces;
using XDice.Models;

namespace XDice.Implementations
{
    // TODO: This is currently a straight-up rewrite of the Python version, with no splitting SOC.
    public class SimpleCommandParser : ICommandParser
    {
        private static readonly Regex RollCommandRegex = new(@"r(?:oll)*\s*(\d*)\s*d*\s*(\d*)(?:\s*\++\s*(\d*))?", RegexOptions.Compiled);  // TODO: Look into making this an Assembly;
        private readonly IDiceRoller _diceRoller;
        private readonly IConfig _config;

        public SimpleCommandParser(IDiceRoller diceRoller, IConfig config)
        {
            _diceRoller = diceRoller;
            _config = config;
        }

        public ParsedRollCommand Parse(string rawCommand)
        {
            if (!rawCommand.StartsWith("/") && !rawCommand.StartsWith("!"))
            {
                return new ParsedRollCommand
                {
                    IsValidCommand = false
                };
            }
            
            var noPrefixCommand = rawCommand.ToLower().TrimStart('/', '!');
            if (noPrefixCommand.StartsWith("rps"))
            {
                return new ParsedRollCommand
                {
                    SpecialRoll = SpecialRoll.RockPaperScissors
                };
            }

            if (noPrefixCommand.StartsWith("coin"))
            {
                return new ParsedRollCommand
                {
                    SpecialRoll = SpecialRoll.CoinFlip
                };
            }

            var match = RollCommandRegex.Match(noPrefixCommand);
            if (!match.Success)
            {
                return new ParsedRollCommand
                {
                    IsValidCommand = false
                };
            }

            return new ParsedRollCommand
            {
                NumberOfDice = string.IsNullOrEmpty(match.Groups[1].Value)
                    ? 1 
                    : int.Parse(match.Groups[1].Value),
                SidesOnDice = string.IsNullOrEmpty(match.Groups[2].Value)
                    ? _config.DefaultDice
                    : int.Parse(match.Groups[2].Value),
                PlusValue = string.IsNullOrEmpty(match.Groups[3].Value)
                    ? 0
                    : int.Parse(match.Groups[3].Value)
            };
        }
    }
}
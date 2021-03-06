using XDice.Models;

namespace XDice.Interfaces
{
    public interface ICommandParser
    {
        public ParsedRollCommand Parse(string rawCommand);
    }
}
using XDice.Enums;

namespace XDice.Models
{
    public class ParsedRollCommand
    {
        public int NumberOfDice { get; init; }
        
        public int SidesOnDice { get; init; }
        
        public int PlusValue { get; init; }

        public SpecialRoll SpecialRoll { get; init; } = SpecialRoll.None;

        public bool IsValidCommand { get; init; }
    }
}
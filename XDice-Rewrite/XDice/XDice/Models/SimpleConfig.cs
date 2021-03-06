using System.Collections.Generic;
using XDice.Enums;
using XDice.Interfaces;

namespace XDice.Models
{
    public class SimpleConfig : IConfig
    {
        public int GuildId { get; set; }
        
        public bool IsConfigModeActive { get; set; }
        
        public ConfigStep CurrentConfigStep { get; set; }
        
        public int DefaultDice { get; set; }
        
        public PlusBehaviour PlusBehaviour { get; set; }
        
        public ExplodeBehaviour ExplodeBehaviour { get; set; }

        public IList<int> ExplodeOn { get; set; } = new List<int>();
        
        public bool AddTotalModeActive { get; set; }
        
        public bool CountSuccessesModeActive { get; set; }

        public IList<int> SuccessOn { get; set; } = new List<int>();
        
        public CritFailBehaviour CritFailBehaviour { get; set; }
    }
}
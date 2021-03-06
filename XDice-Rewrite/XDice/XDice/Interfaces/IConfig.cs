using System.Collections.Generic;
using XDice.Enums;

namespace XDice.Interfaces
{
    public interface IConfig
    {
       int GuildId { get; set; }
       
       bool IsConfigModeActive { get; set; }
       
       ConfigStep CurrentConfigStep { get; set; }
       
       int DefaultDice { get; set; }
       
       PlusBehaviour PlusBehaviour { get; set; }
       
       ExplodeBehaviour ExplodeBehaviour { get; set; }

       IList<int> ExplodeOn { get; set; }
       
       bool AddTotalModeActive { get; set; }
       
       bool CountSuccessesModeActive { get; set; }

       IList<int> SuccessOn { get; set; }
       
       CritFailBehaviour CritFailBehaviour { get; set; }
    }
}
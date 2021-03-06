using XDice.Interfaces;
using XDice.Models;

namespace XDice.Implementations
{
    public class ConfigLoader : IConfigLoader
    {
        public IConfig Load(string serverId)
        {
            return new SimpleConfig
            {
                
            };
        }
    }
}
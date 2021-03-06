namespace XDice.Interfaces
{
    public interface IConfigLoader
    {
        IConfig Load(string serverId);
    }
}
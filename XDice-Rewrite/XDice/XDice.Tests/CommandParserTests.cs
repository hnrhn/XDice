using Moq;
using XDice.Interfaces;
using Xunit;

namespace XDice.Tests
{
    public class CommandParserTests
    {
        [Theory]
        [InlineData("/rps")]
        [InlineData("!rps")]
        public void RPS_Commands_Call_Rock_Paper_Scissors(string rpsCommand)
        {
            var roller = new Mock<IDiceRoller>();
            roller.Verify(rps => rps.RockPaperScissors(), Times.Once);
        }
    }
}
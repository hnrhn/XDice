import net.xdice.behaviour.standard.StandardParser;
import net.xdice.enums.*;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardParserTests {
    private final XDiceConfig baseConfig = XDiceConfig.getDefaultConfig("1");
    private final StandardParser parser = new StandardParser();

    @ParameterizedTest
    @ValueSource(strings = {
        "/r",
        "/r 1",
        "/r d10",
        "/r 2d10",
        "/r 1d10+1",
        "/roll",
        "/roll 1",
        "/roll d10",
        "/roll 2d10",
        "/roll 1d10+1",
        "!r",
        "!r 1",
        "!r d10",
        "!r 2d10",
        "!r 1d10+1",
        "!roll",
        "!roll 1",
        "!roll d10",
        "!roll 2d10",
        "!roll 1d10+1",
        "/r1",
        "/rd10",
        "/r2d10",
        "/r1d10+1",
        "/roll1",
        "/rolld10",
        "/roll2d10",
        "/roll1d10+1",
        "!r1",
        "!rd10",
        "!r2d10",
        "!r1d10+1",
        "!roll1",
        "!rolld10",
        "!roll2d10",
        "!roll1d10+1"
    })
    void validDiceRollStringReturnsXDiceCommandOfTypeDICE(String validDiceRoll) {
        assertEquals(CommandType.DICE, parser.parseCommandString(validDiceRoll, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @CsvSource({
        "/r,1",
        "/r 5,5",
        "/r d10,1",
        "/r 2d10,2",
        "/r 3d10+1,3"
    })
    void diceRollStringReturnsCorrectNumberOfDice(String diceRoll, Integer expectedOutcome) {
        assertEquals(expectedOutcome, parser.parseCommandString(diceRoll, baseConfig).getNumberOfDice());
    }

    @ParameterizedTest
    @CsvSource({
        "/r,-1",
        "/r 5,-1",
        "/r d10,10",
        "/r 2d10,10",
        "/r 3d20+1,20"
    })
    void diceRollStringReturnsCorrectTypeOfDice(String diceRoll, Integer suppliedOutcome) {
        var expectedOutcome = suppliedOutcome == -1 ? baseConfig.getDefaultDice() : suppliedOutcome;
        assertEquals(expectedOutcome, parser.parseCommandString(diceRoll, baseConfig).getTypeOfDice());
    }

    @ParameterizedTest
    @CsvSource({
        "/r 1d20+1,1",
        "/r d20+2,2",
        "/r 1+2,2",
        "/r,0"
    })
    void diceRollStringReturnsCorrectBonusValueWhenInAutoSuccessesMode(String diceRoll, Long expectedOutcome) {
        baseConfig.setCountSuccesses(true);
        baseConfig.setPlusBehaviour(PlusBehaviour.AUTO_SUCCESS);
        assertEquals(expectedOutcome, parser.parseCommandString(diceRoll, baseConfig).getModifier());
    }

    @Test
    void rpsReturnsXDiceCommandOfTypeROCK_PAPER_SCISSORS() {
        assertEquals(CommandType.ROCK_PAPER_SCISSORS, parser.parseCommandString("/rps", baseConfig).getCommandType());
    }

    @Test
    void coinReturnsXDiceCommandOfTypeCOIN() {
        assertEquals(CommandType.COIN, parser.parseCommandString("/coin", baseConfig).getCommandType());
    }

    @Test
    void xdiceConfigReturnsXDiceCommandOfTypeCONFIG() {
        assertEquals(CommandType.CONFIG, parser.parseCommandString("/xdice config", baseConfig).getCommandType());
    }

    @Test
    void xdiceHelpReturnsXDiceCommandOfTypeCONFIG() {
        assertEquals(CommandType.HELP, parser.parseCommandString("/xdice help", baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "wtf dicebot",
        "wtf xdice",
        "dicebot wtf",
        "dicebot what the fuck",
        "dicebot what are you doing",
        "xdice wtf",
        "xdice what the fuck",
        "xdice what are you doing",
        "what the fuck xdice",
        "what the fuck dicebot",
        "what are you doing xdice",
        "what are you doing dicebot",
        "wtf, dicebot",
        "wtf, xdice",
        "dicebot, wtf",
        "dicebot, what the fuck",
        "dicebot, what are you doing",
        "xdice, wtf",
        "xdice, what the fuck",
        "xdice, what are you doing",
        "what the fuck, xdice",
        "what the fuck, dicebot",
        "what are you doing, xdice",
        "what are you doing, dicebot",
    })
    void validWtfStringsReturnXDiceCommandOfTypeWTF(String wtfString) {
        assertEquals(CommandType.WTF, parser.parseCommandString(wtfString, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "thank you xdice",
        "thank you dicebot",
        "thanks xdice",
        "thanks dicebot",
        "thank you, xdice",
        "thank you, dicebot",
        "thanks, xdice",
        "thanks, dicebot"
    })
    void validThanksStringsReturnXDiceCommandOfTypeTHANKS(String thanksString) {
        assertEquals(CommandType.THANKS, parser.parseCommandString(thanksString, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "fuck you xdice",
        "screw you xdice",
        "i hate you xdice",
        "fuck off xdice",
        "go away xdice",
        "fuck you dicebot",
        "screw you dicebot",
        "i hate you dicebot",
        "fuck off dicebot",
        "go away dicebot",
        "fuck you, xdice",
        "screw you, xdice",
        "i hate you, xdice",
        "fuck off, xdice",
        "go away, xdice",
        "fuck you, dicebot",
        "screw you, dicebot",
        "i hate you, dicebot",
        "fuck off, dicebot",
        "go away, dicebot",
    })
    void validInsultStringsReturnXDiceCommandOfTypeINSULT(String insultString) {
        assertEquals(CommandType.INSULT, parser.parseCommandString(insultString, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "i love you dicebot",
        "ily dicebot",
        "i would die for you dicebot",
        "i love you xdice",
        "ily xdice",
        "i would die for you xdice",
        "dicebot i love you",
        "dicebot ily",
        "dicebot i would die for you",
        "xdice i love you",
        "xdice ily",
        "xdice i would die for you",
        "i love you, dicebot",
        "ily, dicebot",
        "i would die for you, dicebot",
        "i love you, xdice",
        "ily, xdice",
        "i would die for you, xdice",
        "dicebot, i love you",
        "dicebot, ily",
        "dicebot, i would die for you",
        "xdice, i love you",
        "xdice, ily",
        "xdice, i would die for you"
    })
    void validLoveStringsReturnXDiceCommandOfTypeLOVE(String loveString) {
        assertEquals(CommandType.LOVE, parser.parseCommandString(loveString, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Hello there",
        "/rollllll",
        "12"
    })
    void invalidStringsReturnXDiceCommandOfTypeINVALID(String invalidString) {
        assertEquals(CommandType.INVALID, parser.parseCommandString(invalidString, baseConfig).getCommandType());
    }
}

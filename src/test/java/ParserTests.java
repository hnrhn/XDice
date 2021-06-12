import net.xdice.enums.*;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static net.xdice.Parser.*;

public class ParserTests {
    private final XDiceConfig baseConfig = new XDiceConfig(0, true, ConfigStep.BEGIN, 10, false, Collections.emptyList(), false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, Collections.emptyList(), CritFailBehaviour.NONE);

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
        assertEquals(CommandType.DICE, parseCommandString(validDiceRoll, baseConfig).getCommandType());
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
        assertEquals(expectedOutcome, parseCommandString(diceRoll, baseConfig).getNumberOfDice());
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
        assertEquals(expectedOutcome, parseCommandString(diceRoll, baseConfig).getTypeOfDice());
    }

    @ParameterizedTest
    @CsvSource({
        "/r 1d20+1,1",
        "/r d20+2,2",
        "/r 1+2,2",
        "/r,0"
    })
    void diceRollStringReturnsCorrectBonusValue(String diceRoll, Long expectedOutcome) {
        assertEquals(expectedOutcome, parseCommandString(diceRoll, baseConfig).getModifier());
    }

    @Test
    void rpsReturnsXDiceCommandOfTypeROCK_PAPER_SCISSORS() {
        assertEquals(CommandType.ROCK_PAPER_SCISSORS, parseCommandString("/rps", baseConfig).getCommandType());
    }

    @Test
    void coinReturnsXDiceCommandOfTypeCOIN() {
        assertEquals(CommandType.COIN, parseCommandString("/coin", baseConfig).getCommandType());
    }

    @Test
    void xdiceConfigReturnsXDiceCommandOfTypeCONFIG() {
        assertEquals(CommandType.CONFIG, parseCommandString("/xdice config", baseConfig).getCommandType());
    }

    @Test
    void xdiceHelpReturnsXDiceCommandOfTypeCONFIG() {
        assertEquals(CommandType.HELP, parseCommandString("/xdice help", baseConfig).getCommandType());
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
        assertEquals(CommandType.WTF, parseCommandString(wtfString, baseConfig).getCommandType());
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
        assertEquals(CommandType.THANKS, parseCommandString(thanksString, baseConfig).getCommandType());
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
        assertEquals(CommandType.INSULT, parseCommandString(insultString, baseConfig).getCommandType());
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
        assertEquals(CommandType.LOVE, parseCommandString(loveString, baseConfig).getCommandType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Hello there",
        "/rollllll",
        "12"
    })
    void invalidStringsReturnXDiceCommandOfTypeINVALID(String invalidString) {
        assertEquals(CommandType.INVALID, parseCommandString(invalidString, baseConfig).getCommandType());
    }
}

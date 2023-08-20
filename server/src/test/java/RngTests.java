import net.xdice.behaviour.standard.StandardRoller;
import net.xdice.enums.*;
import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class RngTests {
    private final XDiceConfig baseConfig = XDiceConfig.getDefaultConfig("1");
    private final StandardRoller diceRoller = new StandardRoller();

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 100, 1000})
    void rollerRollsCorrectNumberOfDice(int numberOfDice) {
        XDiceCommand diceRollCommand = new XDiceCommand(CommandType.DICE, numberOfDice, 1, 0);

        // Roller writes its results to a string, so to test the length of the array, we make an array of the expected
        // length, convert it to a string, and measure its length.
        // TODO: separate the array generation and the string generation into two testable classes.
        var expectedStringLength = Arrays.toString(new int[numberOfDice]).length();
        var rolledDiceStringLength = diceRoller.rollDice(diceRollCommand, baseConfig).length();

        assertEquals(rolledDiceStringLength, expectedStringLength);
    }

    @Test
    void explodeBehaviourExtraGeneratesAdditionalRolls(){
        XDiceCommand diceRollCommand = new XDiceCommand(CommandType.DICE, 1, 1, 0);
        XDiceConfig diceRollConfig = baseConfig;
        diceRollConfig.setExplodeBehaviour(ExplodeBehaviour.EXTRA);
        diceRollConfig.setExplodeOn(List.of(1));

        var expectedStringLength = Arrays.toString(new int[2]).length();
        var rolledDiceStringLength = diceRoller.rollDice(diceRollCommand, diceRollConfig).length();

        assertEquals(rolledDiceStringLength, expectedStringLength);
    }

    @RepeatedTest(100)
    void extraChainExplodesExplodedDice(){
        XDiceCommand diceRollCommand = new XDiceCommand(CommandType.DICE, 1, 2, 0);
        XDiceConfig diceRollConfig = baseConfig;
        diceRollConfig.setExplodeBehaviour(ExplodeBehaviour.EXTRA_CHAIN);
        diceRollConfig.setExplodeOn(List.of(2));

        var rolledDiceString = diceRoller.rollDice(diceRollCommand, diceRollConfig);
        var rolledDice = Arrays.stream(new StringBuilder(rolledDiceString)
                .deleteCharAt(rolledDiceString.length() - 1)
                .deleteCharAt(0)
                .toString()
                .split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();

        if (rolledDice[0] != 2 || rolledDice[1] != 2) {
            return;
        }

        assertTrue(rolledDice.length > 2);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void explodeOnDoubleModeAddsExtraSuccessForEachExplode(Integer numDice) {
        XDiceConfig doubleConfig = new XDiceConfig("0", true, ConfigStep.BEGIN, 1, true, List.of(1), false, PlusBehaviour.IGNORE, ExplodeBehaviour.DOUBLE, List.of(1), CritFailBehaviour.NONE, RollerSelection.STANDARD);
        XDiceCommand doubleCommand = new XDiceCommand(CommandType.DICE, numDice, 1, 0);
        String[] rolledDiceResult = diceRoller.rollDice(doubleCommand, doubleConfig).split(" ");
        int numberOfSuccesses = Integer.parseInt(rolledDiceResult[rolledDiceResult.length - 2]);
        assertEquals(numDice*2, numberOfSuccesses);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void addTotalReturnsCorrectTotal (Integer numDice) {
        XDiceConfig addTotalConfig = new XDiceConfig("0", true, ConfigStep.BEGIN, 1, false, Collections.emptyList(), true, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, Collections.emptyList(), CritFailBehaviour.NONE, RollerSelection.STANDARD);
        XDiceCommand addTotalCommand = new XDiceCommand(CommandType.DICE, numDice, 1, 0);
        String[] rolledDiceResult = diceRoller.rollDice(addTotalCommand, addTotalConfig).split(" ");
        int total = Integer.parseInt(rolledDiceResult[rolledDiceResult.length - 1]);
        assertEquals(numDice, total);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void plusBehaviourADDReturnsTotalPlusModifier (Integer modifier) {
        XDiceConfig addTotalConfig = new XDiceConfig("0", true, ConfigStep.BEGIN, 1, false, Collections.emptyList(), true, PlusBehaviour.ADD, ExplodeBehaviour.NONE, Collections.emptyList(), CritFailBehaviour.NONE, RollerSelection.STANDARD);
        XDiceCommand addTotalCommand = new XDiceCommand(CommandType.DICE, 0, 1, modifier);
        String[] rolledDiceResult = diceRoller.rollDice(addTotalCommand, addTotalConfig).split(" ");
        int total = Integer.parseInt(rolledDiceResult[rolledDiceResult.length - 1]);
        assertEquals(modifier, total);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void plusBehaviourAUTO_SUCCESSReturnsSuccessesPlusModifier(Integer modifier) {
        XDiceConfig doubleConfig = new XDiceConfig("0", true, ConfigStep.BEGIN, 1, true, List.of(1), false, PlusBehaviour.AUTO_SUCCESS, ExplodeBehaviour.NONE, Collections.emptyList(), CritFailBehaviour.NONE, RollerSelection.STANDARD);
        XDiceCommand doubleCommand = new XDiceCommand(CommandType.DICE, 0, 1, modifier);
        String[] rolledDiceResult = diceRoller.rollDice(doubleCommand, doubleConfig).split(" ");
        int numberOfSuccesses = Integer.parseInt(rolledDiceResult[rolledDiceResult.length - 2]);
        assertEquals(modifier, numberOfSuccesses);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void critFailReturnsCorrectNumberOfFails (Integer numDice) {
        XDiceConfig addTotalConfig = new XDiceConfig("0", true, ConfigStep.BEGIN, 1, true, List.of(2), false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, Collections.emptyList(), CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD);
        XDiceCommand addTotalCommand = new XDiceCommand(CommandType.DICE, numDice, 1, 0);
        String[] rolledDiceResult = diceRoller.rollDice(addTotalCommand, addTotalConfig).split(" ");
        int total = Integer.parseInt(rolledDiceResult[rolledDiceResult.length - 3]);
        assertEquals(numDice, total);
    }

    @RepeatedTest(100)
    void rockPaperScissorsReturnsValidResult() {
        var command = new XDiceCommand(CommandType.ROCK_PAPER_SCISSORS);

        var validValues = new HashSet<String>();
        Collections.addAll(validValues, "Rock", "Paper", "Scissors");

        var receivedValue = diceRoller.rockPaperScissors();
        assertTrue(validValues.contains(receivedValue));
    }

    @RepeatedTest(100)
    void coinFlipReturnsValidResult() {
        var validValues = new HashSet<String>();
        Collections.addAll(validValues, "Heads", "Tails");

        var receivedValue = diceRoller.flipCoin();
        assertTrue(validValues.contains(receivedValue));
    }
}

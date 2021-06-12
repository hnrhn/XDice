import net.xdice.enums.ConfigStep;
import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.utilities.ConfigStepConverter;
import net.xdice.utilities.CritFailBehaviourConverter;
import net.xdice.utilities.ExplodeBehaviourConverter;
import net.xdice.utilities.PlusBehaviourConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTests {
    private static final Map<Integer, ConfigStep> configStepIntToEnumValidResults = Map.of(
    1, ConfigStep.BEGIN,
    2, ConfigStep.DEFAULT_DICE,
    3, ConfigStep.COUNT_SUCCESSES,
    4, ConfigStep.SUCCESS_ON,
    5, ConfigStep.ADD_TOTAL,
    6, ConfigStep.PLUS_BEHAVIOUR,
    7, ConfigStep.EXPLODE_BEHAVIOUR,
    8, ConfigStep.EXPLODE_ON,
    9, ConfigStep.CRIT_FAIL_BEHAVIOUR,
    10, ConfigStep.CONFIRM
    );
    private static final Map<ConfigStep, Integer> configStepEnumToIntValidResults = Map.of(
        ConfigStep.BEGIN, 1,
        ConfigStep.DEFAULT_DICE, 2,
        ConfigStep.COUNT_SUCCESSES, 3,
        ConfigStep.SUCCESS_ON, 4,
        ConfigStep.ADD_TOTAL, 5,
        ConfigStep.PLUS_BEHAVIOUR, 6,
        ConfigStep.EXPLODE_BEHAVIOUR, 7,
        ConfigStep.EXPLODE_ON, 8,
        ConfigStep.CRIT_FAIL_BEHAVIOUR, 9,
        ConfigStep.CONFIRM, 10
    );
    private static final Map<String, ConfigStep> configStepStringToEnumValidResults = Map.ofEntries(
        Map.entry("begin", ConfigStep.BEGIN),
        Map.entry("default_dice", ConfigStep.DEFAULT_DICE),
        Map.entry("count_successes", ConfigStep.COUNT_SUCCESSES),
        Map.entry("success_on", ConfigStep.SUCCESS_ON),
        Map.entry("add_total", ConfigStep.ADD_TOTAL),
        Map.entry("plus_behaviour", ConfigStep.PLUS_BEHAVIOUR),
        Map.entry("plus_behavior", ConfigStep.PLUS_BEHAVIOUR),
        Map.entry("explode_behaviour", ConfigStep.EXPLODE_BEHAVIOUR),
        Map.entry("explode_behavior", ConfigStep.EXPLODE_BEHAVIOUR),
        Map.entry("explode_on", ConfigStep.EXPLODE_ON),
        Map.entry("crit_fail_behaviour", ConfigStep.CRIT_FAIL_BEHAVIOUR),
        Map.entry("crit_fail_behavior", ConfigStep.CRIT_FAIL_BEHAVIOUR),
        Map.entry("confirm", ConfigStep.CONFIRM)
    );

    private static final Map<Integer, CritFailBehaviour> critFailIntToEnumValidResults = Map.of(
        1, CritFailBehaviour.NONE,
        2, CritFailBehaviour.ONE_NO_SUCCESSES
    );
    private static final Map<CritFailBehaviour, Integer> critFailEnumToIntValidResults = Map.of(
        CritFailBehaviour.NONE, 1,
        CritFailBehaviour.ONE_NO_SUCCESSES, 2
    );

    private static final Map<Integer, ExplodeBehaviour> explodeBehaviourIntToEnumValidResults = Map.of(
        1, ExplodeBehaviour.NONE,
        2, ExplodeBehaviour.DOUBLE,
        3, ExplodeBehaviour.EXTRA,
        4, ExplodeBehaviour.EXTRA_CHAIN
    );
    private static final Map<ExplodeBehaviour, Integer> explodeBehaviourEnumToIntValidResults = Map.of(
        ExplodeBehaviour.NONE, 1,
        ExplodeBehaviour.DOUBLE, 2,
        ExplodeBehaviour.EXTRA, 3,
        ExplodeBehaviour.EXTRA_CHAIN, 4
    );

    private static final Map<Integer, PlusBehaviour> plusBehaviourIntToEnumValidResults = Map.of(
        1, PlusBehaviour.IGNORE,
        2, PlusBehaviour.ADD,
        3, PlusBehaviour.AUTO_SUCCESS
    );
    private static final Map<PlusBehaviour, Integer> plusBehaviourEnumToIntValidResults = Map.of(
        PlusBehaviour.IGNORE, 1,
        PlusBehaviour.ADD, 2,
        PlusBehaviour.AUTO_SUCCESS, 3
    );

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    void configStepConverterConvertsValidIntToCorrectConfigStep(int testInt) {
        assertEquals(configStepIntToEnumValidResults.get(testInt), ConfigStepConverter.intToEnum(testInt));
    }

    @ParameterizedTest
    @ValueSource(strings = {"begin", "default_dice", "count_successes", "success_on", "add_total", "plus_behaviour", "plus_behavior", "explode_behaviour", "explode_behavior", "explode_on", "crit_fail_behaviour", "crit_fail_behavior", "confirm"})
    void configStepConverterConvertsValidStringToCorrectEnum(String testString) {
        assertEquals(configStepStringToEnumValidResults.get(testString), ConfigStepConverter.stringToEnum(testString));
    }

    @ParameterizedTest
    @EnumSource(ConfigStep.class)
    void configStepConverterConvertsConfigStepToCorrectInt(ConfigStep testEnum) {
        assertEquals(configStepEnumToIntValidResults.get(testEnum), ConfigStepConverter.enumToInt(testEnum));
    }

    @Test
    void configStepConverterThrowsInvalidStateWhenInvalidIntProvided() {
        assertThrows(IllegalStateException.class, () -> ConfigStepConverter.intToEnum(11));
    }

    @Test
    void configStepConverterThrowsInvalidStateWhenInvalidStringProvided() {
        assertThrows(IllegalStateException.class, () -> ConfigStepConverter.stringToEnum("invalid"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2})
    void critFailConverterCorrectlyConvertsValidIntToCritFail(int testInt) {
        assertEquals(critFailIntToEnumValidResults.get(testInt), CritFailBehaviourConverter.intToEnum(testInt));
    }

    @Test
    void critFailConverterThrowsInvalidStateWhenInvalidIntProvided() {
        assertThrows(IllegalStateException.class, () -> CritFailBehaviourConverter.intToEnum(3));
    }

    @ParameterizedTest
    @EnumSource(CritFailBehaviour.class)
    void critFailConverterConvertsCritFailToCorrectInt(CritFailBehaviour testEnum) {
        assertEquals(critFailEnumToIntValidResults.get(testEnum), CritFailBehaviourConverter.enumToInt(testEnum));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4})
    void explodeBehaviourConverterCorrectlyConvertsValidIntToExplode(int testInt) {
        assertEquals(explodeBehaviourIntToEnumValidResults.get(testInt), ExplodeBehaviourConverter.intToEnum(testInt));
    }

    @Test
    void explodeBehaviourConverterThrowsInvalidStateWhenInvalidIntProvided() {
        assertThrows(IllegalStateException.class, () -> ExplodeBehaviourConverter.intToEnum(5));
    }

    @ParameterizedTest
    @EnumSource(ExplodeBehaviour.class)
    void explodeBehaviourConverterConvertsExplodeToCorrectInt(ExplodeBehaviour testEnum) {
        assertEquals(explodeBehaviourEnumToIntValidResults.get(testEnum), ExplodeBehaviourConverter.enumToInt(testEnum));
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    void plusBehaviourConverterCorrectlyConvertsValidIntToPlus(int testInt) {
        assertEquals(plusBehaviourIntToEnumValidResults.get(testInt), PlusBehaviourConverter.intToEnum(testInt));
    }

    @Test
    void plusBehaviourConverterThrowsInvalidStateWhenInvalidIntProvided() {
        assertThrows(IllegalStateException.class, () -> PlusBehaviourConverter.intToEnum(4));
    }

    @ParameterizedTest
    @EnumSource(PlusBehaviour.class)
    void plusBehaviourConverterConvertsPlusEnumToCorrectInt(PlusBehaviour testEnum) {
        assertEquals(plusBehaviourEnumToIntValidResults.get(testEnum), PlusBehaviourConverter.enumToInt(testEnum));
    }
}

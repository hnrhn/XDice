package net.xdice.utilities;

import net.xdice.enums.ConfigStep;

import java.util.Locale;

public class ConfigStepConverter {
    public static Integer enumToInt(ConfigStep cs) {
        return switch (cs) {
            case BEGIN -> 1;
            case DEFAULT_DICE -> 2;
            case COUNT_SUCCESSES -> 3;
            case SUCCESS_ON -> 4;
            case ADD_TOTAL -> 5;
            case PLUS_BEHAVIOUR -> 6;
            case EXPLODE_BEHAVIOUR -> 7;
            case EXPLODE_ON -> 8;
            case CRIT_FAIL_BEHAVIOUR -> 9;
            case CONFIRM -> 10;
        };
    }

    public static ConfigStep intToEnum(Integer integer) {
        return switch (integer) {
            case 1 -> ConfigStep.BEGIN;
            case 2 -> ConfigStep.DEFAULT_DICE;
            case 3 -> ConfigStep.COUNT_SUCCESSES;
            case 4 -> ConfigStep.SUCCESS_ON;
            case 5 -> ConfigStep.ADD_TOTAL;
            case 6 -> ConfigStep.PLUS_BEHAVIOUR;
            case 7 -> ConfigStep.EXPLODE_BEHAVIOUR;
            case 8 -> ConfigStep.EXPLODE_ON;
            case 9 -> ConfigStep.CRIT_FAIL_BEHAVIOUR;
            case 10 -> ConfigStep.CONFIRM;
            default -> throw new IllegalStateException("Unexpected value: " + integer);
        };
    }

    public static ConfigStep stringToEnum(String string) {
        String lowerCase = string.toLowerCase(Locale.ROOT);
        return switch (lowerCase) {
            case "begin" -> ConfigStep.BEGIN;
            case "default_dice" -> ConfigStep.DEFAULT_DICE;
            case "count_successes" -> ConfigStep.COUNT_SUCCESSES;
            case "success_on" -> ConfigStep.SUCCESS_ON;
            case "add_total" -> ConfigStep.ADD_TOTAL;
            case "plus_behaviour", "plus_behavior" -> ConfigStep.PLUS_BEHAVIOUR;
            case "explode_behaviour", "explode_behavior" -> ConfigStep.EXPLODE_BEHAVIOUR;
            case "explode_on" -> ConfigStep.EXPLODE_ON;
            case "crit_fail_behaviour", "crit_fail_behavior" -> ConfigStep.CRIT_FAIL_BEHAVIOUR;
            case "confirm" -> ConfigStep.CONFIRM;
            default -> throw new IllegalStateException("Unexpected value: " + lowerCase);
        };
    }
}

package com.hnrhn.xdice.enums;

public class ConfigStepHelper {
    public static Integer toInt(ConfigStep cs) {
        switch (cs) {
            case BEGIN -> { return 1; }
            case DEFAULT_DICE -> { return 2; }
            case COUNT_SUCCESSES -> { return 3; }
            case SUCCESS_ON -> { return 4; }
            case ADD_TOTAL -> { return 5; }
            case PLUS_BEHAVIOUR -> { return 6;}
            case EXPLODE_BEHAVIOUR -> { return 7; }
            case EXPLODE_ON -> { return 8; }
            case CRIT_FAIL_BEHAVIOUR -> { return 9; }
            case CONFIRM -> { return 10; }
            default -> throw new IllegalStateException("Invalid enum value: " + cs.name());
        }
    }

    public static ConfigStep toEnum(Integer integer) {
        switch (integer) {
            case 1 -> { return ConfigStep.BEGIN; }
            case 2 -> { return ConfigStep.DEFAULT_DICE; }
            case 3 -> { return ConfigStep.COUNT_SUCCESSES; }
            case 4 -> { return ConfigStep.SUCCESS_ON; }
            case 5 -> { return ConfigStep.ADD_TOTAL; }
            case 6 -> { return ConfigStep.PLUS_BEHAVIOUR; }
            case 7 -> { return ConfigStep.EXPLODE_BEHAVIOUR; }
            case 8 -> { return ConfigStep.EXPLODE_ON; }
            case 9 -> { return ConfigStep.CRIT_FAIL_BEHAVIOUR; }
            case 10 -> { return ConfigStep.CONFIRM; }
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        }
    }
}

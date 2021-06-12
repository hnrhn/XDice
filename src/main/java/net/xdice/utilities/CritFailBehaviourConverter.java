package net.xdice.utilities;

import net.xdice.enums.CritFailBehaviour;

public class CritFailBehaviourConverter {
    public static Integer enumToInt(CritFailBehaviour cfb) {
        return switch (cfb) {
            case NONE -> 1;
            case ONE_NO_SUCCESSES -> 2;
        };
    }

    public static CritFailBehaviour intToEnum(Integer integer) {
        return switch (integer) {
            case 1 -> CritFailBehaviour.NONE;
            case 2 -> CritFailBehaviour.ONE_NO_SUCCESSES;
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        };
    }
}

package net.xdice.utilities;

import net.xdice.enums.ExplodeBehaviour;

public class ExplodeBehaviourConverter {
    public static Integer enumToInt(ExplodeBehaviour eb) {
        return switch (eb) {
            case NONE -> 1;
            case DOUBLE -> 2;
            case EXTRA -> 3;
            case EXTRA_CHAIN -> 4;
        };
    }

    public static ExplodeBehaviour intToEnum(Integer integer) throws IllegalStateException {
        return switch (integer) {
            case 1 -> ExplodeBehaviour.NONE;
            case 2 -> ExplodeBehaviour.DOUBLE;
            case 3 -> ExplodeBehaviour.EXTRA;
            case 4 -> ExplodeBehaviour.EXTRA_CHAIN;
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        };
    }
}

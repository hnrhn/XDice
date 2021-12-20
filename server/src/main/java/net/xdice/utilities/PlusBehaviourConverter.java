package net.xdice.utilities;

import net.xdice.enums.PlusBehaviour;

public class PlusBehaviourConverter {
    public static Integer enumToInt(PlusBehaviour pb) {
        return switch (pb) {
            case IGNORE -> 1;
            case ADD -> 2;
            case AUTO_SUCCESS -> 3;
        };
    }

    public static PlusBehaviour intToEnum(Integer integer) {
        return switch (integer) {
            case 1 -> PlusBehaviour.IGNORE;
            case 2 -> PlusBehaviour.ADD;
            case 3 -> PlusBehaviour.AUTO_SUCCESS;
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        };
    }
}

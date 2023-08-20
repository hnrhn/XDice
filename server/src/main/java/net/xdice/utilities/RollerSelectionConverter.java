package net.xdice.utilities;

import net.xdice.enums.RollerSelection;

public class RollerSelectionConverter {
    public static Integer enumToInt(RollerSelection pb) {
        return switch (pb) {
            case STANDARD -> 1;
            case FORBIDDENLANDS -> 2;
        };
    }

    public static RollerSelection intToEnum(Integer integer) {
        return switch (integer) {
            case 1 -> RollerSelection.STANDARD;
            case 2 -> RollerSelection.FORBIDDENLANDS;
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        };
    }
}

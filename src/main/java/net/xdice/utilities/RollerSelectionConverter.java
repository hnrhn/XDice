package net.xdice.utilities;

import net.xdice.enums.RollerSelection;

public class RollerSelectionConverter {
    public static Integer enumToInt(RollerSelection pb) {
        return switch (pb) {
            case STANDARD -> 1;
            case FALLENKINGDOMS -> 2;
        };
    }

    public static RollerSelection intToEnum(Integer integer) {
        return switch (integer) {
            case 1 -> RollerSelection.STANDARD;
            case 2 -> RollerSelection.FALLENKINGDOMS;
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        };
    }
}

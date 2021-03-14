package com.hnrhn.xdice.enums;

import java.util.ArrayList;
import java.util.List;

public class PlusBehaviourHelper {
    public static final List<Integer> validIntegers = new ArrayList<>(List.of(1, 2, 3));

    public static Integer toInt(PlusBehaviour pb) {
        switch (pb) {
            case IGNORE -> { return 1; }
            case ADD -> { return 2; }
            case AUTO_SUCCESS -> { return 3; }
            default -> throw new IllegalStateException("Invalid enum value: " + pb.name());
        }
    }

    public static PlusBehaviour toEnum(Integer integer) {
        switch (integer) {
            case 1 -> { return PlusBehaviour.IGNORE; }
            case 2 -> { return PlusBehaviour.ADD; }
            case 3 -> { return PlusBehaviour.AUTO_SUCCESS; }
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        }
    }
}

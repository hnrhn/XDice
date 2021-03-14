package com.hnrhn.xdice.enums;

import java.util.ArrayList;
import java.util.List;

public class ExplodeBehaviourHelper {
    public static final List<Integer> validIntegers = new ArrayList<>(List.of(1, 2, 3, 4));

    public static Integer toInt(ExplodeBehaviour eb) {
        switch (eb) {
            case NONE -> { return 1; }
            case DOUBLE -> { return 2; }
            case EXTRA -> { return 3; }
            case EXTRA_CHAIN -> { return 4; }
            default -> throw new IllegalStateException("Invalid enum value: " + eb.name());
        }
    }

    public static ExplodeBehaviour toEnum(Integer integer) {
        switch (integer) {
            case 1 -> { return ExplodeBehaviour.NONE; }
            case 2 -> { return ExplodeBehaviour.DOUBLE; }
            case 3 -> { return ExplodeBehaviour.EXTRA; }
            case 4 -> { return ExplodeBehaviour.EXTRA_CHAIN; }
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        }
    }
}

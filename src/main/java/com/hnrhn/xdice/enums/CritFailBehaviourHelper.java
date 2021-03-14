package com.hnrhn.xdice.enums;

public class CritFailBehaviourHelper {
    public static Integer toInt(CritFailBehaviour cfb) {
        switch (cfb) {
            case NONE -> { return 1; }
            case ONE_NO_SUCCESSES -> { return 2; }
            default -> throw new IllegalStateException("Invalid enum value: " + cfb.name());
        }
    }

    public static CritFailBehaviour toEnum(Integer integer) {
        switch (integer) {
            case 1 -> { return CritFailBehaviour.NONE; }
            case 2 -> { return CritFailBehaviour.ONE_NO_SUCCESSES; }
            default -> throw new IllegalStateException("Invalid integer value: " + integer);
        }
    }
}

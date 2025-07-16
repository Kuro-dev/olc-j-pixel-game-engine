package org.kurodev.jpixelgameengine.impl;

import lombok.Getter;

@Getter
public enum PixelgameEngineReturnCode {
    FAIL(0), OK(1), NO_FILE(-1);

    private final int code;

    PixelgameEngineReturnCode(int code) {
        this.code = code;
    }

    public static PixelgameEngineReturnCode fromCode(int code) {
        return switch (code) {
            case 0 -> FAIL;
            case 1 -> OK;
            case -1 -> NO_FILE;
            default -> throw new IllegalArgumentException("Unexpected value: " + code);
        };
    }

}

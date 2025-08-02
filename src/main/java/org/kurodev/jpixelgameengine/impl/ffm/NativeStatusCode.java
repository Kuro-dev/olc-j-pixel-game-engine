package org.kurodev.jpixelgameengine.impl.ffm;

public enum NativeStatusCode {
    SUCCESS(0),
    FAIL(1),
    NO_FILE(-1),
    INSTANCE_ALREADY_EXISTS(2),
    ;

    private final int code;

    NativeStatusCode(int code) {
        this.code = code;
    }

    public static NativeStatusCode ofCode(int code) {
        for (NativeStatusCode statusCode : NativeStatusCode.values()) {
            if (statusCode.code == code) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Invalid code " + code);
    }
}

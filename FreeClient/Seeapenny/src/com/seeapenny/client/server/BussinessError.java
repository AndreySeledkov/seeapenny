package com.seeapenny.client.server;

public enum BussinessError {
    INTERNAL_ERROR(0),
    EMPTY_CLIENT_INFO(1),
    NO_SESSION(2),
    WRONG_PARAMS(3),
    TEMPORARY_UNAVAILABLE(4),


    UNDER_MAINTENANCE(150),

    TIMEOUT_EXCEPTION(5),

    UNKNOWN_ERROR(-1);

    private final int code;

    private BussinessError(int code) {
        this.code = code;
    }

    public static BussinessError fromCode(int code) {
        for (BussinessError error : BussinessError.values()) {
            if (code == error.code) {
                return error;
            }
        }

        return UNKNOWN_ERROR;
    }

    public int getCode() {
        return code;
    }
}

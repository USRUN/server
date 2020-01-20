package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

public enum AuthType {
    facebook(0),
    google(1),
    strava(2),
    local(3);

    private int value;
    private static final HashMap<Integer, AuthType> returnMap = new HashMap<>();

    static {
        for (AuthType authType : AuthType.values()) {
            returnMap.put(authType.value, authType);
        }
    }

    AuthType(int value) {
        this.value = value;
    }

    public static AuthType fromInt(int iValue) {
        return returnMap.get(iValue);
    }

    @JsonValue
    public int toValue() {
        return this.value;
    }
}

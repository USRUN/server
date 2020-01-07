package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthType {
    facebook,
    google,
    strava,
    local;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}

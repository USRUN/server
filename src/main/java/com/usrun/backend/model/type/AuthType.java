package com.usrun.backend.model.type;

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

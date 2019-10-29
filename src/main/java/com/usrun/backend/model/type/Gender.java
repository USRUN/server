package com.usrun.backend.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE,
    FEMALE;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}

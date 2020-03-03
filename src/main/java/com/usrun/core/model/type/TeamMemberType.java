package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

public enum TeamMemberType {
    OWNER(1),
    ADMIN(2),
    MEMBER(3),
    PENDING(4),
    BLOCKED(5);


    private int value;
    private static final HashMap<Integer, TeamMemberType> returnMap = new HashMap<>();

    static {
        for (TeamMemberType role : TeamMemberType.values()) {
            returnMap.put(role.value, role);
        }
    }
    TeamMemberType(int value) {
        this.value = value;
    }

    public static TeamMemberType fromInt(int iValue) {
        return returnMap.get(iValue);
    }

    @JsonValue
    public int toValue() {
        return this.value;
    }
}

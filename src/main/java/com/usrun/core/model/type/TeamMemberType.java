package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;

public enum TeamMemberType {
  OWNER(1),
  ADMIN(2),
  MEMBER(3),
  PENDING(4),
  INVITED(5),
  BLOCKED(6),
  GUEST(7);


  private int value;
  private static final HashMap<Integer, TeamMemberType> returnMapInt = new HashMap<>();
  private static final HashMap<String, TeamMemberType> returnMapString = new HashMap<>();

  static {
    for (TeamMemberType role : TeamMemberType.values()) {
      returnMapInt.put(role.value, role);
      returnMapString.put(role.name(), role);
    }
  }

  TeamMemberType(int value) {
    this.value = value;
  }

  public static TeamMemberType fromInt(int iValue) {
    return returnMapInt.get(iValue);
  }

  public static TeamMemberType fromString(String name) {
    return returnMapString.get(name);
  }

  @JsonValue
  public int toValue() {
    return this.value;
  }
}

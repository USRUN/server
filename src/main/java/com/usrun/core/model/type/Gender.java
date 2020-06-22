package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;

public enum Gender {
  MALE(0),
  FEMALE(1);

  private int value;
  private static final HashMap<Integer, Gender> returnMap = new HashMap<>();

  static {
    for (Gender gender : Gender.values()) {
      returnMap.put(gender.value, gender);
    }
  }

  Gender(int value) {
    this.value = value;
  }

  public static Gender fromInt(int iValue) {
    return returnMap.get(iValue);
  }

  @JsonValue
  public int toValue() {
    return this.value;
  }
}

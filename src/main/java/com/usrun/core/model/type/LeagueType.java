package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LeagueType {
  ENTERPRISE,
  SCHOOL,
  SPORT_CLUB;

  @JsonValue
  public int toValue() {
    return ordinal();
  }
}

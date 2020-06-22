package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuggestTeamRequest {

  String district;
  String province;
  int howMany;
}

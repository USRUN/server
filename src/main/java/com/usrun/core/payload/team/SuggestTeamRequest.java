package com.usrun.core.payload.team;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuggestTeamRequest {

  int province;
  int count;
}

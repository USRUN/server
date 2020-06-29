package com.usrun.core.payload.team;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class SuggestTeamRequest {

  int province;
  int count;
}

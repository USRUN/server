package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindTeamRequest {

  public String teamName;
  public int pageNum;
  public int perPage;
}

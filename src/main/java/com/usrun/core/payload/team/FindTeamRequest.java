package com.usrun.core.payload.team;

import lombok.Data;

@Data
public class FindTeamRequest {

  private String teamName;
  private int pageNum;
  private int perPage;
}

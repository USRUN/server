package com.usrun.core.payload.team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FindTeamRequest {

  private String teamName;
  private int pageNum;
  private int perPage;
}

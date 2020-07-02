package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamRequest {

  private Long teamId;

  private int privacy;

  private String teamName;

  private String thumbnail;

  private String banner;

  private int province;

  private String description;
}

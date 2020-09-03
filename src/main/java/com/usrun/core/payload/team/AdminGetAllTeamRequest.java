package com.usrun.core.payload.team;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class AdminGetAllTeamRequest {

  private int offset;
  private int limit;
  private String teamName;
}

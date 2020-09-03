package com.usrun.core.payload.team;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class InviteTeamRequest {

  private long teamId;
  private String emailOrUserCode;
}

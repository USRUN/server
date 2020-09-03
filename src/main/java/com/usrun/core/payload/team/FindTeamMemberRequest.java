package com.usrun.core.payload.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindTeamMemberRequest {

  private long teamId;
  private int count;
  private int offset;
  private String keyword;
}

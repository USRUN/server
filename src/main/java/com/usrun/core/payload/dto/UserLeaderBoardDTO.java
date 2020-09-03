package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaderBoardDTO {

  private long userId;
  private String displayName;
  private String avatar;
}

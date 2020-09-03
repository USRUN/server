package com.usrun.core.payload.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
public class UserLeaderBoardInfo {

  private long userId;
  private String displayName;
  private String avatar;
  private long totalDistance;

  public UserLeaderBoardInfo(UserLeaderBoardDTO dto, long totalDistance) {
    this.userId = dto.getUserId();
    this.displayName = dto.getDisplayName();
    this.avatar = dto.getAvatar();
    this.totalDistance = totalDistance;
  }
}

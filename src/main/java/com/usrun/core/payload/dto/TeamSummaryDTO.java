package com.usrun.core.payload.dto;

import com.usrun.core.model.Team;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamSummaryDTO {

  public Long teamId;
  public String teamName;
  public int teamMemberCount;
  public String thumbnail;

  public TeamSummaryDTO(Team toGetSummary) {
    this.teamId = toGetSummary.getId();
    this.teamName = toGetSummary.getTeamName();
    this.teamMemberCount = toGetSummary.getTotalMember();
    this.thumbnail = toGetSummary.getThumbnail();
  }
}

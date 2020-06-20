package com.usrun.core.payload.dto;

import com.usrun.core.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TeamSummaryDTO {
    public Long teamId;
    public String teamName;
    public Long teamMemberCount;
    public String thumbnail;

    public TeamSummaryDTO(Team toGetSummary,Long teamMemberCount){
        this.teamId = toGetSummary.getId();
        this.teamName = toGetSummary.getTeamName();
        this.teamMemberCount = teamMemberCount;
        this.thumbnail = toGetSummary.getThumbnail();
    }
}

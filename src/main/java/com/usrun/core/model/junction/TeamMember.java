package com.usrun.core.model.junction;

import com.usrun.core.model.type.TeamMemberType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
public class TeamMember {
    @Id
    private Long teamId;
    @Id
    private Long userId;
    private TeamMemberType teamMemberType;
    private Date addTime;

    public TeamMember(Long teamId, Long userId, TeamMemberType teamMemberType, Date addTime){
        this.teamId = teamId;
        this.userId = userId;
        this.teamMemberType = teamMemberType;
        this.addTime = addTime;
    }
}

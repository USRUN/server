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
}

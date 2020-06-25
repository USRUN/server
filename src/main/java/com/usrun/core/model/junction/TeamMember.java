package com.usrun.core.model.junction;

import com.usrun.core.model.type.TeamMemberType;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class TeamMember {

  @Id
  private Long teamId;
  @Id
  private Long userId;
  private TeamMemberType teamMemberType;
  private Date addTime;

  public TeamMember(Long teamId, Long userId, int teamMemberType, Date addTime) {
    this.teamId = teamId;
    this.userId = userId;
    this.teamMemberType = TeamMemberType.fromInt(teamMemberType);
    this.addTime = addTime;
  }
}

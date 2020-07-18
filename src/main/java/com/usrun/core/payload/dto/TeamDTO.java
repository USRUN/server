package com.usrun.core.payload.dto;

import com.usrun.core.model.Team;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
  private Long id;

  private int privacy;

  private int totalMember;

  private String teamName;

  private String banner;

  private String thumbnail;

  private boolean verified;

  private boolean deleted;

  private Date createTime;

  private Integer province;

  private String description;

  private TeamMemberType teamMemberType;

  public TeamDTO(Team team, TeamMember teamMember) {
    this.id = team.getId();
    this.privacy = team.getPrivacy();
    this.totalMember = team.getTotalMember();
    this.teamName = team.getTeamName();
    this.banner = team.getBanner();
    this.thumbnail = team.getThumbnail();
    this.verified = team.isVerified();
    this.deleted = team.isDeleted();
    this.createTime = team.getCreateTime();
    this.province = team.getProvince();
    this.description = team.getDescription();
    this.teamMemberType = teamMember.getTeamMemberType();
  }
}

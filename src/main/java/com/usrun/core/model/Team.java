package com.usrun.core.model;


import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
public class Team {

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

  // used in TeamService -> createTeam
  public Team(int privacy, String teamName, Integer province, Date createTime,
      String thumbnail, String banner) {
    this.teamName = teamName;
    this.privacy = privacy;
    this.province = province;
    this.createTime = createTime;

    // auto-assigned for a newly created team
    this.totalMember = 1;
    this.verified = false;
    this.deleted = false;
    this.thumbnail = thumbnail;
    this.banner = banner;
  }

  // used to update team's info
  public Team(Long teamId, int privacy, int totalMember, String teamName, String thumbnail,
      String banner, boolean verified, boolean deleted, Date createTime, Integer province,
      String description) {
    this.id = teamId;
    this.teamName = teamName;
    this.thumbnail = thumbnail;
    this.privacy = privacy;
    this.totalMember = totalMember;
    this.banner = banner;
    this.province = province;
    this.verified = verified;
    this.deleted = deleted;
    this.createTime = createTime;
    this.description = description;
  }
}

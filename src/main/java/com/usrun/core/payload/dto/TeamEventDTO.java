package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamEventDTO {

  private long teamId;
  private String name;
  private String thumbnail;
  private int totalMember;
  private int province;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huyna3
 */
@Getter
@Setter
public class TeamLeaderBoardDTO {

  public long teamId;
  public long distance;
  public String name;
  public String avatar;

  public TeamLeaderBoardDTO() {
  }
}

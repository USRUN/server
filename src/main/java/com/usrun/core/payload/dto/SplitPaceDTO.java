/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.dto;

import lombok.Data;

/**
 * @author huyna
 */
@Data
public class SplitPaceDTO {

  private double km;
  private int pace;
  private double elevGain = 0;
  private double boxWidth = 0;

  public SplitPaceDTO(double km, int pace) {
    this.km = km;
    this.pace = pace;
  }
}

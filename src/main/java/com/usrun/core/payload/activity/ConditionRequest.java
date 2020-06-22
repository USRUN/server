package com.usrun.core.payload.activity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionRequest {

  private Date fromTime;
  private Date toTime;
  private Long distance;
  private Double pace;
  private Double elevation;
}

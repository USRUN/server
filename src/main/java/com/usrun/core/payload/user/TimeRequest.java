package com.usrun.core.payload.user;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimeRequest {

  private Date fromTime;
  private Date toTime;
  private int offset;
  private int limit;
}

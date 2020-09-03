package com.usrun.core.payload.activity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserStatRequest {

  private long userId;
  private Date fromTime;
  private Date toTime;
}

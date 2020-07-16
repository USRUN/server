package com.usrun.core.payload.activity;

import com.usrun.core.payload.user.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserStatRequest {

  private Date fromTime;
  private Date toTime;
}

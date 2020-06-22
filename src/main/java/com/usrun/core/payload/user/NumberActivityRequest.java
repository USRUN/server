package com.usrun.core.payload.user;

import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NumberActivityRequest {

  @Min(1)
  int size = 10;
  @Min(0)
  int offset = 0;
}

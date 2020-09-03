package com.usrun.core.payload.user;

import lombok.Data;

@Data
public class GetUsersRequest {

  private int offset;
  private int limit;
}

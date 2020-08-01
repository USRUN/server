package com.usrun.core.payload.user;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class BanUserRequest {
  private long userId;
  private boolean banned;
}

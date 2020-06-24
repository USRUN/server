package com.usrun.core.payload;

import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class LoginRequest {

  private int type = -1;
  private String token;
  private String email;
  private String password;
}

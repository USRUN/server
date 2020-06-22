package com.usrun.core.payload.track;

import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class RegisterRequest {

  private String email;
  private String password;
  private String name;
}

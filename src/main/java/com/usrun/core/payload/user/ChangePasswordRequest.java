package com.usrun.core.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

  private String oldPassword;
  private String newPassword;
}

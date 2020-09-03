package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagerDTO {

  private long userId;
  private String email;
  private String displayName;
  private String authType;
  private boolean isEnabled;
}

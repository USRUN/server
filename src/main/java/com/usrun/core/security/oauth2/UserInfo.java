package com.usrun.core.security.oauth2;

import com.usrun.core.model.type.AuthType;
import lombok.Data;

@Data
public class UserInfo {

  private AuthType type;
  private String id;
  private String name;
  private String email;
  private String imageUrl;
}

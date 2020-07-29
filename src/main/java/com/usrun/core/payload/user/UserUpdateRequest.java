package com.usrun.core.payload.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
public class UserUpdateRequest {

  private String name;
  private String avatar;
  private Integer gender;
  private Long birthday;
  private Double weight;
  private Double height;
  private String deviceToken;
  private Integer province;
}

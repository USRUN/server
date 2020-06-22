package com.usrun.core.payload.user;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class UserUpdateRequest {

  @Size(max = 50)
  private String name;

  private String base64Image;

  @Min(0)
  @Max(1)
  private Integer gender;

  @Min(1)
  private Long birthdayNum;

  @Min(1)
  private Double weight;

  @Min(1)
  private Double height;

  private String deviceToken;
}

package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUserDTO {

  private long userId;
  private String displayName;
  private String avatar;
}

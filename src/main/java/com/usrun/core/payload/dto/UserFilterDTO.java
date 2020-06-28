package com.usrun.core.payload.dto;

import com.usrun.core.model.type.Gender;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

  private Long id;
  private String name;
  private String email;
  private String code;
  private Gender gender;
  private Date birthday;
  private String avatar;
}

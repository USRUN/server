package com.usrun.core.payload.dto;

import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.TeamMemberType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterWithTypeDTO {

  private Long userId;
  private String name;
  private String email;
  private String code;
  private Gender gender;
  private Date birthday;
  private String avatar;
  private TeamMemberType teamMemberType;
}

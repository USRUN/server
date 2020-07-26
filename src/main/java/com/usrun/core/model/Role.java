package com.usrun.core.model;

import com.usrun.core.model.type.RoleType;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class Role {

  private RoleType roleType;

  public Role() {
  }

  public Role(RoleType roleType) {
    this.roleType = roleType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Role role = (Role) o;
    return roleType == role.roleType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleType);
  }
}

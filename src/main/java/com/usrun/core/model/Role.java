package com.usrun.core.model;

import com.usrun.core.model.type.RoleType;
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
}

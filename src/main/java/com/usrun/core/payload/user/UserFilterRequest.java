package com.usrun.core.payload.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @author phuctt4
 */

@Setter
@Getter
public class UserFilterRequest {
    @Min(0)
    private int offset = 0;
    @Min(1)
    private int count = 30;
    private String key;
}

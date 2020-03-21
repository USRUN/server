package com.usrun.core.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinTeamRequest {
    private Long teamId;
    private Long userId;
}

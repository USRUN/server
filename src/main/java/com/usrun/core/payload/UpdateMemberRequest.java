package com.usrun.core.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequest {
    private Long teamId;
    private Long memberId;
    private int memberType;
}

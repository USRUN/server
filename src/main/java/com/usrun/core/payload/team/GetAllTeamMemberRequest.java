package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllTeamMemberRequest {
    public long teamId;
    public int pageNum;
    public int perPage;
}

package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateTeamRequest {
    // OwnerId is assumed to be the current user

    private int privacy;

    private String teamName;

    private String district;

    private String province;

    private String thumbnailBase64;
}

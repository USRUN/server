package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateTeamRequest {
    private int privacy;

    private String teamName;

    private String thumbnail;

    private String location;

    private String description;
}

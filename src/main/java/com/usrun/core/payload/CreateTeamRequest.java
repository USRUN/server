package com.usrun.core.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateTeamRequest {
    private Long userId;

    private int privacy;

    private String teamName;

    private String thumbnail;

    private String location;

    private String description;
}

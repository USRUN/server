package com.usrun.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Love {
    private long activityId;
    private long userId;

    public Love( long activityId, long userId){
        this.activityId = activityId;
        this.userId = userId;
    }
}

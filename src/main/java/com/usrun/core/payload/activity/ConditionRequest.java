package com.usrun.core.payload.activity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class ConditionRequest {
    private Date fromTime;
    private Date toTime;
    private Long distance;
    private Double pace;
    private Double elevation;
}

package com.usrun.core.payload.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

@Setter
@Getter
public class TimeRequest {
    private Date fromTime;
    private Date toTime;
}

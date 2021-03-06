package com.usrun.core.model.track;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author phuctt4
 */

@Getter
@Setter
@ToString
public class Location {
    private Float latitude;
    private Float longitude;
    private Date time;

    public Location(Float latitude, Float longitude, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}

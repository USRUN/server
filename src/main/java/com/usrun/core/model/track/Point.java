package com.usrun.core.model.track;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author phuctt4
 */

@Setter
@Getter
@ToString
@Document("PointLog")
public class Point {
    private Long trackId;

    private Float latitude;

    private Float longitude;

    private double time;

    public Point() {
    }

    public Point(Long trackId, Float latitude, Float longitude) {
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        Date date = new Date();
        this.time = date.getTime();
    }

    public Point(Long trackId, Float latitude, Float longitude, double time) {
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}

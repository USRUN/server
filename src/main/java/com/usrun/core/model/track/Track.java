package com.usrun.core.model.track;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;

import java.util.Date;
import java.util.List;

/**
 * @author phuctt4
 */

@Getter
@Setter
@ToString(exclude = {"description"})
@Document(collection = "TrackLog")
public class Track {
    @Id
    private Long trackId;

    private Long userId;

    private String description;

    private Date time;

    public Track() {
    }

    public Track(Long trackId, Long userId) {
        this.trackId = trackId;
        this.userId = userId;
        this.time = new Date();
    }

    public Track(Long trackId, Long userId, String description) {
        this.trackId = trackId;
        this.userId = userId;
        this.description = description;
        this.time = new Date();
    }
}

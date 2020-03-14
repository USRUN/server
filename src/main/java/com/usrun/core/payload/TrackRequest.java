package com.usrun.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usrun.core.model.track.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class TrackRequest {
    private long trackId;
    private List<Location> locations;
    private long time;
    private String sig;
}

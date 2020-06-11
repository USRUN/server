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
    private List<List<Location>> locations;
    private double time;
}

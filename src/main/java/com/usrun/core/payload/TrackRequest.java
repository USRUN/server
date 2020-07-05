package com.usrun.core.payload;

import com.usrun.core.model.track.Location;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */
@Getter
@Setter
public class TrackRequest {

    private List<List<Location>> locations;
    private Map<String, Double> splitDistance;
    private double time;
}

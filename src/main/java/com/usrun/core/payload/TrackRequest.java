package com.usrun.core.payload;

import com.usrun.core.model.track.Location;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class TrackRequest {

  private List<List<Location>> locations;
  private double time;
}

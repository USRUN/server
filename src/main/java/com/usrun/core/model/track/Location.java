package com.usrun.core.model.track;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author phuctt4
 */

@Getter
@Setter
@ToString
public class Location {

  private Double latitude;
  private Double longitude;
  private long time;

  public Location(Double latitude, Double longitude, long time) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.time = time;
  }
}

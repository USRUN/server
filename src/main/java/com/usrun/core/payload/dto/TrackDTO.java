package com.usrun.core.payload.dto;

import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class TrackDTO {

  private Long trackId;

  private Long userId;

  private String description;

  private Date time;

  private List<Point> points;

  public TrackDTO(Track track, List<Point> points) {
    this.trackId = track.getTrackId();
    this.userId = track.getUserId();
    this.description = track.getDescription();
    this.time = track.getTime();
    this.points = points;
  }
}

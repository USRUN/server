package com.usrun.core.model.track;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

  @JsonFormat(shape = JsonFormat.Shape.NUMBER)
  private Date time;

  private List<List<Location>> locations;

  private List<DistanceAndPace> splitDistance = new ArrayList<>();

  private class DistanceAndPace {

    private Double distance;
    private Double pace;

    public DistanceAndPace(Double distance, Double pace) {
      this.distance = distance;
      this.pace = pace;
    }
  }

  public Track() {
  }

  public Track(Long trackId, Long userId) {
    this.trackId = trackId;
    this.userId = userId;
    this.time = new Date();
  }

  public Track(Long trackId, Long userId, String description, Map<String, Double> splitDistance) {
    this.trackId = trackId;
    this.userId = userId;
    this.description = description;
    this.time = new Date();
    if (splitDistance != null) {
      for (Map.Entry<String, Double> entry : splitDistance.entrySet()) {
        Double distance = Double.parseDouble(entry.getKey());
        Double pace = entry.getValue();
        DistanceAndPace objSpilt = new DistanceAndPace(distance, pace);
        this.splitDistance.add(objSpilt);
      }
    }
  }
}

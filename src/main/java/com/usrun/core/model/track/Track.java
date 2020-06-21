package com.usrun.core.model.track;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
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

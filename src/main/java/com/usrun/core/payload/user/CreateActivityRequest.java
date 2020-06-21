package com.usrun.core.payload.user;

import com.usrun.core.payload.TrackRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateActivityRequest {

  private Long totalDistance;
  private Long totalTime;
  private Long totalStep;
  private Double avgPace;
  private Double avgHeart;
  private Double maxHeart;
  private Integer calories;
  private Double elevGain;
  private Double elevMax;
  private String photo;
  private String title;
  private String description;
  private Integer totalLove;
  private Integer totalComment;
  private Integer totalShare;
  private Boolean processed;
  private Integer deleted;
  private Integer privacy;
  private TrackRequest trackRequest;
  private String sig;
}

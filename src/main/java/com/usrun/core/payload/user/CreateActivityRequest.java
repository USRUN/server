package com.usrun.core.payload.user;

import com.usrun.core.payload.TrackRequest;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
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
    private List<String> photosBase64 = Collections.emptyList();
    private String title;
    private String description;
    private Integer totalLove;
    private Integer totalComment;
    private Integer totalShare;
    private Boolean processed;
    private Integer deleted;
    private Integer privacy;
    private TrackRequest trackRequest;
    private long time;
    private String sig;
    private Long eventId;
}

package com.usrun.core.payload.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EditActivityRequest {
    private long activityId;
    private String title;
    private List<String> photos;
    private String description;
    @JsonProperty
    private boolean isShowMap;
}

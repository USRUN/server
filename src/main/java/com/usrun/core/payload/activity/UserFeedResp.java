/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.activity;

import com.usrun.core.payload.dto.SplitPaceDTO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna
 */
@Getter
@Setter
public class UserFeedResp {
    long userActivityId;
    long userId;
    String userDisplayName;
    String userAvatar;
    boolean userHcmus;
    long eventId;
    String eventName;
    String eventThumbnail;
    Date createTime;
    long totalDistance;
    long totalTime;
    long totalStep;
    double avgPace;
    double avgHeart;
    double maxHeart;
    long calories;
    double elevGain;
    double elevMax;
    List<String> photos;
    String title;
    String description;
    long totalLove;
    long totalComment;
    long totalShare;
    List<SplitPaceDTO> splitPace;

    public UserFeedResp(long userActivityId, long userId, String userDisplayName, String userAvatar, boolean userHcmus, long eventId, String eventName, String eventThumbnail, Date createTime, long totalDistance, long totalTime, long totalStep, double avgPace, double avgHeart, double maxHeart, long calories, double elevGain, double elevMax, List<String> photos, String title, String description, long totalLove, long totalComment, long totalShare, List<SplitPaceDTO> splitPace) {
        this.userActivityId = userActivityId;
        this.userId = userId;
        this.userDisplayName = userDisplayName;
        this.userAvatar = userAvatar;
        this.userHcmus = userHcmus;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventThumbnail = eventThumbnail;
        this.createTime = createTime;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.totalStep = totalStep;
        this.avgPace = avgPace;
        this.avgHeart = avgHeart;
        this.maxHeart = maxHeart;
        this.calories = calories;
        this.elevGain = elevGain;
        this.elevMax = elevMax;
        this.photos = photos;
        this.title = title;
        this.description = description;
        this.totalLove = totalLove;
        this.totalComment = totalComment;
        this.totalShare = totalShare;
        this.splitPace = splitPace;
    }

    

    public UserFeedResp(){}
}

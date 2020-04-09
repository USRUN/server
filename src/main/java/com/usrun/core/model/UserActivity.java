/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model;

import java.sql.Time;
import java.util.Date;
import javax.persistence.*;

import com.usrun.core.payload.user.CreateActivityRequest;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna
 */
@Setter
@Getter
@Entity
@Table(name = "userActivity")
public class UserActivity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userActivityId;
    @Column(name="userId")
    private long userId;
    private Date createTime;
    private long totalDistance;
    private Time totalTime;
    private long totalStep;
    private double avgPace;
    private double avgHeart;
    private double maxHeart;
    private int calories;
    private double elevGain;
    private double elevMax;
    private String photo;
    private String title;
    private String description;
    private int totalLike;
    private int totalComment;
    private int totalShare;
    private boolean processed;
    private int deleted;
    private int privacy;
    public UserActivity(long userActivityId, long userId, Date createTime, long totalDistance, Time totalTime, long totalStep, double avgPace, double avgHeart, double maxHeart, int calories, double elevGain, double elevMax, String photo, String title, String description, int totalLike, int totalComment, int totalShare, boolean processed, int deleted, int privacy) {
        this.userActivityId = userActivityId;
        this.userId = userId;
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
        this.photo = photo;
        this.title = title;
        this.description = description;
        this.totalLike = totalLike;
        this.totalComment = totalComment;
        this.totalShare = totalShare;
        this.processed = processed;
        this.deleted = deleted;
        this.privacy = privacy;
    }
    public UserActivity(CreateActivityRequest createActivityRequest) {
        this.createTime = createActivityRequest.getCreateTime();
        this.totalDistance = createActivityRequest.getTotalDistance();
        this.totalTime = createActivityRequest.getTotalTime();
        this.totalStep = createActivityRequest.getTotalStep();
        this.avgPace = createActivityRequest.getAvgPace();
        this.avgHeart = createActivityRequest.getAvgHeart();
        this.maxHeart = createActivityRequest.getMaxHeart();
        this.calories = createActivityRequest.getCalories();
        this.elevGain = createActivityRequest.getElevGain();
        this.elevMax = createActivityRequest.getElevMax();
        this.photo = createActivityRequest.getPhoto();
        this.title = createActivityRequest.getTitle();
        this.description = createActivityRequest.getDescription();
        this.totalLike = createActivityRequest.getTotalLike();
        this.totalComment = createActivityRequest.getTotalComment();
        this.totalShare = createActivityRequest.getTotalShare();
        this.processed = createActivityRequest.getProcessed();
        this.deleted = createActivityRequest.getDeleted();
        this.privacy = createActivityRequest.getPrivacy();
    }

    public UserActivity(long userId, long totalDistance, Time totalTime, double avgPace, Date createTime) {
        this.userId = userId;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.avgPace = avgPace;
        this.createTime = createTime;
    }
    public UserActivity(){
        this.totalDistance = 0l;
        this.totalTime = new Time(0l);
        this.totalStep = 0l;
        this.avgPace = 0d;
        this.avgHeart = 0d;
        this.maxHeart = 0d;
        this.calories = 0;
        this.elevGain = 0d;
        this.elevMax = 0d;
        this.totalLike = 0;
        this.totalComment = 0;
        this.totalShare = 0;
    }
}

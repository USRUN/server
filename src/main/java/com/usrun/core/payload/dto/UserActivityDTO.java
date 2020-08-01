package com.usrun.core.payload.dto;

import com.usrun.core.model.UserActivity;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@NoArgsConstructor
public class UserActivityDTO {

  private long userActivityId;
  private long userId;
  private Date createTime;
  private long totalDistance;
  private long totalTime;
  private long totalStep;
  private double avgPace;
  private double avgHeart;
  private double maxHeart;
  private int calories;
  private double elevGain;
  private double elevMax;
  private List<String> photos;
  private String title;
  private String description;
  private long totalLove;
  private int totalComment;
  private int totalShare;
  private boolean processed;
  private int deleted;
  private int privacy;
  private String displayName;
  private boolean hcmus;
  private String avatar;

  public UserActivityDTO(UserActivity userActivity, UserDTO userDTO) {
    this.userActivityId = userActivity.getUserActivityId();
    this.userId = userActivity.getUserId();
    this.createTime = userActivity.getCreateTime();
    this.totalDistance = userActivity.getTotalDistance();
    this.totalTime = userActivity.getTotalTime();
    this.totalStep = userActivity.getTotalStep();
    this.avgPace = userActivity.getAvgPace();
    this.avgHeart = userActivity.getAvgHeart();
    this.maxHeart = userActivity.getMaxHeart();
    this.calories = userActivity.getCalories();
    this.elevGain = userActivity.getElevGain();
    this.elevMax = userActivity.getElevMax();
    this.photos = userActivity.getPhotos();
    this.title = userActivity.getTitle();
    this.description = userActivity.getDescription();
    this.totalLove = userActivity.getTotalLove();
    this.totalComment = userActivity.getTotalComment();
    this.totalShare = userActivity.getTotalShare();
    this.processed = userActivity.isProcessed();
    this.deleted = userActivity.getDeleted();
    this.privacy = userActivity.getPrivacy();
    this.avatar = userDTO.getAvatar();
    this.displayName = userDTO.getDisplayName();
    this.hcmus = userDTO.isHcmus();
  }
}

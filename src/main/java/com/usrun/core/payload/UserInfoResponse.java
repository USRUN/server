package com.usrun.core.payload;

import com.usrun.core.model.User;
import com.usrun.core.model.type.Gender;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {

  private int code;
  private Data data;

  @Getter
  @Setter
  public static class Data {

    private Long userId;
    private int type;
    private String code;
    private String email;
    private String avatar;
    private String name;
    private Date createTime;
    private Date updateTime;
    private int isActive;
    private String deviceToken;
    private Date birthday;
    private String phone;
    private Gender gender;
    private Double weight;
    private Double height;
    private Date lastLogin;
    private String accessToken;
    private String tokenType = "Bearer";

    public Data(User user, String accessToken) {
      this.accessToken = accessToken;
      this.type = user.getType().ordinal();
      this.userId = user.getId();
      this.email = user.getEmail();
      this.avatar = user.getAvatar();
      this.name = user.getName();
      this.birthday = user.getBirthday();
      this.weight = user.getWeight();
      this.height = user.getHeight();
      this.gender = user.getGender();
      this.lastLogin = user.getLastLogin();
      this.isActive = user.isEnabled() ? 1 : 0;
      this.deviceToken = user.getDeviceToken();
      this.createTime = user.getCreateTime();
      this.updateTime = user.getUpdateTime();
    }
  }

  public UserInfoResponse(User user, String accessToken) {
    this.data = new Data(user, accessToken);
  }
}

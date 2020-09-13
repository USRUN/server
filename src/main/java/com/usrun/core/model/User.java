package com.usrun.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import java.util.Date;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user")
@Getter
@Setter
public class User {

  @Id
  private Long id;

  private String name;

  @Email
  private String email;

  private String password;

  private AuthType type;

  private String avatar;

  private Date lastLogin;

  private double weight;

  private double height;

  private Gender gender;

  private Date birthday;

  private String code;

  private String deviceToken;

  private Date createTime;

  private Date updateTime;

  @JsonProperty("isActive")
  private boolean isEnabled = true;

  private boolean hcmus = false;

  private int province;

  private Set<Role> roles;

  private Set<Long> teams;

  public User() {
    this.createTime = new Date();
    this.updateTime = new Date();
  }

  public User(String name, @NotBlank @Size(max = 50) @Email String email,
      @NotBlank @Size(max = 100) String password, @NotNull @Size(max = 20) AuthType type) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.type = type;
  }

  public User(Long id, String name, @Email String email, String password, AuthType type,
      String avatar, Date lastLogin, Double weight, Double height, Gender gender, Date birthday,
      String code, String deviceToken, boolean isEnabled, boolean hcmus, Date createTime,
      Date updateTime, int province) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.type = type;
    this.avatar = avatar;
    this.lastLogin = lastLogin;
    this.weight = weight;
    this.height = height;
    this.gender = gender;
    this.birthday = birthday;
    this.code = code;
    this.deviceToken = deviceToken;
    this.isEnabled = isEnabled;
    this.hcmus = hcmus;
    this.createTime = createTime;
    this.updateTime = updateTime;
    this.province = province;
  }
}

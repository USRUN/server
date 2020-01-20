package com.usrun.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Table("users")
@Getter
@Setter
public class User {
    @Id
    private Long id;

    private String name;

    @Email
    private String email;

    @JsonIgnore
    private String password;

    private AuthType type;

    private String openId;

    private String img;

    private Date lastLogin;

    private Double weight;

    private Double height;

    private Gender gender;

    private Date birthday;

    private String code;

    private String deviceToken;

    private String nameSlug;

    private Date dateAdd;

    private Date dateUpdate;

    @JsonProperty("isActive")
    private boolean isEnabled = true;

    private boolean hcmus = false;

    private Set<Role> roles;

    public User() {
        this.dateUpdate = new Date();
        this.dateAdd = new Date();
    }

    public User( String name, @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 100) String password, @NotNull @Size(max = 20) AuthType type, String openId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.openId = openId;
    }

    public User(Long id, String name, @Email String email, String password, AuthType type, String openId, String img, Date lastLogin, Double weight, Double height, Gender gender, Date birthday, String code, String deviceToken, String nameSlug, boolean isEnabled, boolean hcmus, Date dateAdd, Date dateUpdate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.openId = openId;
        this.img = img;
        this.lastLogin = lastLogin;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.birthday = birthday;
        this.code = code;
        this.deviceToken = deviceToken;
        this.nameSlug = nameSlug;
        this.isEnabled = isEnabled;
        this.hcmus = hcmus;
        this.dateAdd = dateAdd;
        this.dateUpdate = dateUpdate;
    }
}

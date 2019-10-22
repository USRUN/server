package com.usrun.backend.payload;

import com.usrun.backend.model.Gender;
import com.usrun.backend.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class LoginResponse {

    private int code;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Long userId;
        private String openId;
        private int type;
        private String code;
        private String email;
        private String img;
        private String name;
        private String nameSlug;
        private Instant dateAdd;
        private Instant dateUpdate;
        private int isActive;
        private String deviceToken;
        private Instant birthday;
        private String phone;
        private Gender gender;
        private Double weight;
        private Double height;
        private Instant lastLogin;
        private String accessToken;
        private String tokenType = "Bearer";

        public Data(User user, String accessToken) {
            this.accessToken = accessToken;
            this.type = user.getType().ordinal();
            this.userId = user.getId();
            this.openId = user.getOpenId();
            this.email = user.getEmail();
            this.img = user.getImg();
            this.name = user.getName();
            this.birthday = user.getBirthday();
            this.dateAdd = user.getDateAdd();
            this.dateUpdate = user.getDateUpdate();
            this.weight = user.getWeight();
            this.height = user.getHeight();
            this.gender = user.getGender();
            this.lastLogin = user.getLastLogin();
            this.isActive = user.isEnabled() ? 1 : 0;
        }
    }

    public LoginResponse(User user, String accessToken) {
        this.data = new Data(user, accessToken);
    }
}

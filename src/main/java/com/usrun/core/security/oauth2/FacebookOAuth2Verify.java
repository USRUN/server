package com.usrun.core.security.oauth2;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.security.oauth2.GoogleOAuth2Verify.GoogleUserInfo;
import com.usrun.core.utility.ObjectUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author phuctt4
 */

@Slf4j
public class FacebookOAuth2Verify implements OAuth2Verify {

  @Override
  public UserInfo verify(String accessToken) {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=email,id,picture,name";
    try {
      ResponseEntity<String> response = restTemplate
          .getForEntity(url, String.class);
      if (response.getStatusCode().isError()) {
        log.error("Request to {} failed, body: {}",url, response.getBody());
        return null;
      } else {
        log.info("Request to {} success, body: {}", url, response.getBody());
        FacebookUserInfo facebookUserInfo = ObjectUtils.fromJsonString(response.getBody(), FacebookUserInfo.class);
        return facebookUserInfo == null ? null : toUserInfo(facebookUserInfo);
      }
    } catch (HttpClientErrorException ex) {
      log.error(ex.getResponseBodyAsString());
      throw new CodeException(ErrorCode.USER_OAUTH2_VERIFY_FAILED);
    }
  }

  private UserInfo toUserInfo(FacebookUserInfo facebookUserInfo) {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(facebookUserInfo.getId());
    userInfo.setEmail(facebookUserInfo.getEmail());
    userInfo.setName(facebookUserInfo.getName());
    userInfo.setImageUrl(facebookUserInfo.getPicture().getData().getUrl());
    userInfo.setType(AuthType.facebook);
    return userInfo;
  }

  @Data
  static class FacebookUserInfo {
    private String id;
    private String name;
    private String email;
    private Picture picture;

    @Data
    static class Picture {
      private PictureData data;

      @Data
      static class PictureData {
        private String url;
      }
    }
  }
}

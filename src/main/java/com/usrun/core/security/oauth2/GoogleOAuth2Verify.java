package com.usrun.core.security.oauth2;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.utility.ObjectUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author phuctt4
 */

@Slf4j
@Component
public class GoogleOAuth2Verify implements OAuth2Verify {

  private final ObjectUtils objectUtils;

  public GoogleOAuth2Verify(ObjectUtils objectUtils) {
    this.objectUtils = objectUtils;
  }

  @Override
  public UserInfo verify(String accessToken) {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://www.googleapis.com/oauth2/v3/userinfo";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<?> entity = new HttpEntity<>(headers);
    try {
      ResponseEntity<String> response = restTemplate
          .exchange(url, HttpMethod.GET, entity, String.class);
      if (response.getStatusCode().isError()) {
        log.error("Request to {} failed, body: {}", url, response.getBody());
        return null;
      } else {
        log.info("Request to {} success, body: {}", url, response.getBody());
        GoogleUserInfo googleUserInfo = objectUtils
            .fromJsonString(response.getBody(), GoogleUserInfo.class);
        return toUserInfo(googleUserInfo);
      }
    } catch (HttpClientErrorException ex) {
      log.error(ex.getResponseBodyAsString());
      throw new CodeException(ErrorCode.USER_OAUTH2_VERIFY_FAILED);
    }
  }

  @Data
  static class GoogleUserInfo {

    private String id;
    private String name;
    private String picture;
    private String email;
  }

  private UserInfo toUserInfo(GoogleUserInfo googleUserInfo) {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(googleUserInfo.getId());
    userInfo.setEmail(googleUserInfo.getEmail());
    userInfo.setImageUrl(googleUserInfo.getPicture());
    userInfo.setName(googleUserInfo.getName());
    userInfo.setType(AuthType.google);
    return userInfo;
  }

}

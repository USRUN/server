package com.usrun.core.security.oauth2;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.ObjectUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class OAuth2UserDetailsService {


  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final AppProperties appProperties;

  private final CacheClient cacheClient;

  private final ObjectUtils objectUtils;

  private final Map<AuthType, OAuth2Verify> verifyMap;

  public OAuth2UserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      AppProperties appProperties, CacheClient cacheClient, ObjectUtils objectUtils,
      GoogleOAuth2Verify googleOAuth2Verify,
      FacebookOAuth2Verify facebookOAuth2Verify) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.appProperties = appProperties;
    this.cacheClient = cacheClient;
    this.objectUtils = objectUtils;
    this.verifyMap = new HashMap<>();
    this.verifyMap.put(AuthType.google, googleOAuth2Verify);
    this.verifyMap.put(AuthType.facebook, facebookOAuth2Verify);
  }

  public User loadUser(String token, AuthType type) {
    try {
      OAuth2Verify oAuth2Verify = verifyMap.get(type);
      if (oAuth2Verify == null) {
        log.error("AuthType [{}] not support", type.toString());
        throw new CodeException(ErrorCode.USER_OAUTH2_TYPE_NOT_SUPPORT);
      }
      UserInfo oAuth2UserInfo = oAuth2Verify.verify(token);
      if (oAuth2UserInfo == null) {
        log.error("Verify [{}] failed", type.toString());
        throw new CodeException(ErrorCode.USER_OAUTH2_VERIFY_FAILED);
      }
      return processOAuth2User(oAuth2UserInfo);
    }  catch (Exception ex) {
      log.error("", ex);
      throw new CodeException(ErrorCode.USER_OAUTH2_VERIFY_FAILED);
    }
  }

  public User processOAuth2User(UserInfo oAuth2UserInfo) {
    if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      log.error("Email not found from OAuth2 provider, userInfo: {}",
          objectUtils.toJsonString(oAuth2UserInfo));
      throw new CodeException(ErrorCode.USER_CREATE_FAIL);
    }

    User user = userRepository.findUserByEmail(oAuth2UserInfo.getEmail());

    if (user != null) {
      if (!user.getType().equals(oAuth2UserInfo.getType())) {
        log.error("Looks like you're signed up with " +
            user.getType() + " account. Please use your " + user.getType() +
            " account to login.");
        throw new CodeException(ErrorCode.USER_EMAIL_IS_USED);
      }
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      user = registerNewUser(oAuth2UserInfo);
    }
    return user;
  }

  private User registerNewUser(UserInfo oAuth2UserInfo) {
    User user = new User();

    user.setType(oAuth2UserInfo.getType());
    user.setName(oAuth2UserInfo.getName());
    user.setEmail(oAuth2UserInfo.getEmail());
    user.setAvatar(oAuth2UserInfo.getImageUrl());
    user.setPassword(passwordEncoder.encode(""));

    user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
    userRepository.insert(user);
    cacheClient.setUser(user);

    log.info("Register User: {}", objectUtils.toJsonString(user));

    return user;
  }

  private User updateExistingUser(User existingUser, UserInfo oAuth2UserInfo) {
    existingUser.setName(oAuth2UserInfo.getName());
    existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
    userRepository.update(existingUser);
    cacheClient.setUser(existingUser);
    log.info("Update User: {}", objectUtils.toJsonString(existingUser));
    return existingUser;
  }
}

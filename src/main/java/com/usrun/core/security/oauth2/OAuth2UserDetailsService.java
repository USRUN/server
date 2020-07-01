package com.usrun.core.security.oauth2;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.ObjectUtils;
import com.usrun.core.utility.UniqueIDGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class OAuth2UserDetailsService {


  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final CacheClient cacheClient;

  private final Map<AuthType, OAuth2Verify> verifyMap;

  private final UniqueIDGenerator uniqueIDGenerator;

  public OAuth2UserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      CacheClient cacheClient, UniqueIDGenerator uniqueIDGenerator) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.cacheClient = cacheClient;
    this.verifyMap = new HashMap<>();
    this.verifyMap.put(AuthType.google, new GoogleOAuth2Verify());
    this.verifyMap.put(AuthType.facebook, new FacebookOAuth2Verify());
    this.uniqueIDGenerator = uniqueIDGenerator;
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
    } catch (Exception ex) {
      log.error("", ex);
      throw new CodeException(ErrorCode.USER_OAUTH2_VERIFY_FAILED);
    }
  }

  public User processOAuth2User(UserInfo oAuth2UserInfo) {
    if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      log.error("Email not found from OAuth2 provider, userInfo: {}",
          ObjectUtils.toJsonString(oAuth2UserInfo));
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
    uniqueIDGenerator.generateID(user);

    user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
    userRepository.insert(user);
    cacheClient.setUser(user);

    log.info("Register User: {}", ObjectUtils.toJsonString(user));

    return user;
  }

  private User updateExistingUser(User existingUser, UserInfo oAuth2UserInfo) {
    existingUser.setName(oAuth2UserInfo.getName());
    existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
    userRepository.update(existingUser);
    cacheClient.setUser(existingUser);
    log.info("Update User: {}", ObjectUtils.toJsonString(existingUser));
    return existingUser;
  }
}

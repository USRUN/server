package com.usrun.core.security.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.OAuth2AuthenticationProcessingException;
import com.usrun.core.model.Role;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.model.User;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.oauth2.user.GoogleOAuth2UserInfo;
import com.usrun.core.security.oauth2.user.OAuth2UserInfo;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class OAuth2UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private ObjectUtils objectUtils;

    public User loadUser(String token, AuthType type) {
        OAuth2UserInfo oAuth2UserInfo = null;
        try {
            if(type.equals(AuthType.google)) {
                oAuth2UserInfo = verifyGoogle(token);
            }
            return processOAuth2User(oAuth2UserInfo);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2UserInfo verifyGoogle(String token) throws GeneralSecurityException, IOException {
        final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                        .setAudience(Collections.singletonList(appProperties.getOauth2().getGoogle().getClientId()))
                        .build();

        final GoogleIdToken googleIdToken = verifier.verify(token);
        if(googleIdToken == null) {
            throw new OAuth2AuthenticationProcessingException("Token Invalid", ErrorCode.USER_CREATE_FAIL);
        }

        final GoogleIdToken.Payload payload = googleIdToken.getPayload();
        return new GoogleOAuth2UserInfo(AuthType.google, payload);
    }

    public User processOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider", ErrorCode.USER_CREATE_FAIL);
        }

        User user = userRepository.findUserByEmail(oAuth2UserInfo.getEmail());

        if(user != null) {
            if(!user.getType().equals(oAuth2UserInfo.getType())) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getType() + " account. Please use your " + user.getType() +
                        " account to login.", ErrorCode.USER_EMAIL_IS_USED);
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserInfo);
        }
        return user;
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setType(oAuth2UserInfo.getType());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setAvatar(oAuth2UserInfo.getImageUrl());
        user.setPassword(passwordEncoder.encode(""));

        user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
        userRepository.insert(user);
        cacheClient.setUser(user);

        LOGGER.info("Register User: {}", objectUtils.toJsonString(user));

        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
        userRepository.update(existingUser);
        cacheClient.setUser(existingUser);
        LOGGER.info("Update User: {}", objectUtils.toJsonString(existingUser));
        return existingUser;
    }
}

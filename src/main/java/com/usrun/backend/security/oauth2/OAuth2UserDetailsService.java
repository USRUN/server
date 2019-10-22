package com.usrun.backend.security.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.usrun.backend.config.AppProperties;
import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.exception.OAuth2AuthenticationProcessingException;
import com.usrun.backend.exception.ResourceNotFoundException;
import com.usrun.backend.model.AuthType;
import com.usrun.backend.model.Role;
import com.usrun.backend.model.RoleName;
import com.usrun.backend.model.User;
import com.usrun.backend.repository.RoleRepository;
import com.usrun.backend.repository.UserRepository;
import com.usrun.backend.security.oauth2.user.GoogleOAuth2UserInfo;
import com.usrun.backend.security.oauth2.user.OAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class OAuth2UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

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

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if(userOptional.isPresent()) {
            user = userOptional.get();
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
        user.setOpenId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImg(oAuth2UserInfo.getImageUrl());
        user.setPassword(passwordEncoder.encode(""));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_USER.name()));

        user.setRoles(Collections.singleton(userRole));
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImg(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}

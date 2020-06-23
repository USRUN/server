package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.LoginRequest;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.payload.track.RegisterRequest;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.security.oauth2.OAuth2UserDetailsService;
import com.usrun.core.service.UserService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private OAuth2UserDetailsService oAuth2UserDetailsService;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    try {
      Authentication authentication = null;
      User user = null;

      AuthType authType = AuthType.fromInt(loginRequest.getType());

      if (authType == null) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_OAUTH2_TYPE_NOT_SUPPORT),
            HttpStatus.BAD_REQUEST);
      }

      if (AuthType.local == authType) {
        user = userService.loadUser(loginRequest.getEmail());
        authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword())
        );
      } else {
        user = oAuth2UserDetailsService.loadUser(loginRequest.getToken(), authType);
        authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), "")
        );
      }

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = tokenProvider.createToken(authentication);
      return ResponseEntity.ok(new UserInfoResponse(user, jwt));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
    try {
      if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_IS_USED),
            HttpStatus.BAD_REQUEST);
      }

      User user = userService.createUser(registerRequest.getName(), registerRequest.getEmail(),
          registerRequest.getPassword());

      String jwt = tokenProvider.createTokenUserId(user.getId());

      URI location = ServletUriComponentsBuilder
          .fromCurrentContextPath().path("/user/info")
          .buildAndExpand(user.getId()).toUri();

      return ResponseEntity.created(location)
          .body(new UserInfoResponse(user, jwt));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }
}

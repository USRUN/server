package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.OAuth2AuthenticationProcessingException;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.LoginRequest;
import com.usrun.core.payload.track.RegisterRequest;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.security.oauth2.OAuth2UserDetailsService;
import com.usrun.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

        Authentication authentication = null;
        User user = null;

        if (loginRequest.getType() == AuthType.local.ordinal()) {
            try {
                user = userService.loadUser(loginRequest.getEmail());
            } catch (Exception ex) {
                return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_NOT_FOUND), HttpStatus.BAD_REQUEST);
            }

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } else if (loginRequest.getType() == AuthType.google.ordinal()) {

            try {
                user = oAuth2UserDetailsService.loadUser(loginRequest.getToken(), AuthType.google);
            } catch (OAuth2AuthenticationProcessingException ex) {
                return new ResponseEntity<>(new CodeResponse(ex.getCode()), HttpStatus.BAD_REQUEST);
            }

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), "")
            );
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new UserInfoResponse(user, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_IS_USED), HttpStatus.BAD_REQUEST);
        }

        User user = userService.createUser(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());

        String jwt = tokenProvider.createTokenUserId(user.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/info")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new UserInfoResponse(user, jwt));
    }
}

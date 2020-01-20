package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.OAuth2AuthenticationProcessingException;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.security.oauth2.OAuth2UserDetailsService;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.UniqueIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    public ResponseEntity<?> authenticateUser(
            @RequestParam("type") Integer type,
            @RequestParam(name = "id_token", required = false, defaultValue = "") String token,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "password", required = false, defaultValue = "") String password) {

        Authentication authentication = null;
        User user = null;

        if (type == AuthType.local.ordinal()) {
            try {
                user = userService.loadUser(email);
            } catch (Exception ex) {
                return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_NOT_FOUND), HttpStatus.BAD_REQUEST);
            }

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } else if (type == AuthType.google.ordinal()) {

            try {
                user = oAuth2UserDetailsService.loadUser(token, AuthType.google);
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
    public ResponseEntity<?> registerUser(
            @Email @NotBlank @RequestParam("email") String email,
            @NotBlank @Size(max = 50) @RequestParam("password") String password,
            @NotBlank @Size(max = 50) @RequestParam("name") String name
    ) {
        if (userRepository.findUserByEmail(email) != null) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_IS_USED), HttpStatus.BAD_REQUEST);
        }

        User user = userService.createUser(name, email, password);

        String jwt = tokenProvider.createTokenUserId(user.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/info")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new UserInfoResponse(user, jwt));
    }
}

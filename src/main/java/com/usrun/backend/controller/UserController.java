package com.usrun.backend.controller;

import com.usrun.backend.exception.ResourceNotFoundException;
import com.usrun.backend.model.User;
import com.usrun.backend.payload.UserInfoResponse;
import com.usrun.backend.repository.UserRepository;
import com.usrun.backend.security.CurrentUser;
import com.usrun.backend.security.TokenProvider;
import com.usrun.backend.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping("/user/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        String jwt = tokenProvider.createTokenUserId(user.getId());

        return new ResponseEntity<>(new UserInfoResponse(user, jwt), HttpStatus.OK);
    }
}

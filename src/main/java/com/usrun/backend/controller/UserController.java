package com.usrun.backend.controller;

import com.usrun.backend.exception.ResourceNotFoundException;
import com.usrun.backend.model.User;
import com.usrun.backend.payload.CodeResponse;
import com.usrun.backend.payload.UserInfoResponse;
import com.usrun.backend.repository.UserRepository;
import com.usrun.backend.security.CurrentUser;
import com.usrun.backend.security.TokenProvider;
import com.usrun.backend.security.UserPrincipal;
import com.usrun.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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

    @GetMapping("/user/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> findUser(
            @RequestParam(name = "key", required = false) String key,
            @Min(0) @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @Min(1) @RequestParam(name = "count", defaultValue = "30") Integer count) {
        Pageable pageable = PageRequest.of(offset, count);
        List<User> users = userRepository.findUserIsEnable('%' + key + '%', pageable);
        return new ResponseEntity<>(new CodeResponse(users), HttpStatus.OK);
    }

    @PostMapping("/user/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(
            @CurrentUser UserPrincipal userPrincipal,
//            @Email @Size(max = 50) @RequestParam(name = "email", required = false) String email,
//            @Size(max = 50) @RequestParam(name = "img", required = false) String img,
            @Size(max = 50) @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "deviceToken", required = false) String deviceToken
    ) {
        User user = userService.updateUser(userPrincipal.getId(), name, deviceToken);
        return ResponseEntity.ok(user);
    }
}

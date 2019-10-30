package com.usrun.backend.controller;

import com.usrun.backend.config.ErrorCode;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
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
            @RequestParam(name = "gender", required = false) Integer gender,
            @RequestParam(name = "birthday", required = false) Long birthdayNum,
            @RequestParam(name = "weight", required = false) Double weight,
            @RequestParam(name = "height", required = false) Double height,
            @RequestParam(name = "deviceToken", required = false) String deviceToken
    ) {
        Instant birthday = new Date(birthdayNum).toInstant();
        User user = userService.updateUser(userPrincipal.getId(), name, deviceToken, gender, birthday, weight, height);
        return ResponseEntity.ok(new CodeResponse(user));
    }

    @PostMapping("/user/verifyStudentHcmus")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyStudentHcmus(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam("otp") String otp) {
        boolean verified = userService.verifyOTP(userPrincipal.getId(), otp);

        if (verified) {
            User user = userRepository.findById(userPrincipal.getId()).get();
            user.setHcmus(true);
            userRepository.save(user);
        }

        return verified ?
                ResponseEntity.ok(new CodeResponse(0)) :
                new ResponseEntity<>(new CodeResponse(ErrorCode.OTP_INVALID), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user/resendOTP")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resendOTP(@CurrentUser UserPrincipal userPrincipal) throws MessagingException {
        if (userPrincipal.isHcmus()) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_VERIFIED), HttpStatus.BAD_REQUEST);
        }

        if (!userPrincipal.getEmail().endsWith("@student.hcmus.edu.vn")) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_IS_NOT_STUDENT_EMAIL), HttpStatus.BAD_REQUEST);
        }

        userService.sendEmailOTP(userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(new CodeResponse(0));
    }
}

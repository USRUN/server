package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.model.User;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.payload.user.UserFilterRequest;
import com.usrun.core.payload.user.UserUpdateRequest;
import com.usrun.core.payload.user.VerifyStudentHcmusRequest;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CacheClient cacheClient;

    @PostMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        User user = userService.loadUser(userId);
        String jwt = tokenProvider.createTokenUserId(user.getId());
        return new ResponseEntity<>(new UserInfoResponse(user, jwt), HttpStatus.OK);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> findUser(@RequestBody UserFilterRequest userFilterRequest) {

        Pageable pageable = PageRequest.of(userFilterRequest.getOffset(), userFilterRequest.getCount());
        List<UserFilterDTO> users = userRepository.findUserIsEnable('%' + userFilterRequest.getKey() + '%', pageable);
        return new ResponseEntity<>(new CodeResponse(users), HttpStatus.OK);

    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UserUpdateRequest userUpdateRequest
            ) {
        Date birthday = null;
        if (userUpdateRequest.getBirthdayNum() != null)
            birthday = new Date(userUpdateRequest.getBirthdayNum());

        User user = userService.updateUser(userPrincipal.getId(),
                userUpdateRequest.getName(),
                userUpdateRequest.getDeviceToken(),
                userUpdateRequest.getGender(), birthday,
                userUpdateRequest.getWeight(),
                userUpdateRequest.getHeight(),
                userUpdateRequest.getBase64Image());
        return ResponseEntity.ok(new CodeResponse(user));
    }

    @PostMapping("/verifyStudentHcmus")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyStudentHcmus(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody VerifyStudentHcmusRequest request) {

        Long userId = userPrincipal.getId();
        Boolean verified = userService.verifyOTP(userId, request.getOtp());
        return verified ?
                ResponseEntity.ok(new CodeResponse(0)) :
                new ResponseEntity<>(new CodeResponse(ErrorCode.OTP_INVALID), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/resendOTP")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resendOTP(@CurrentUser UserPrincipal userPrincipal) throws MessagingException {
        if (userPrincipal.isHcmus()) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_VERIFIED), HttpStatus.BAD_REQUEST);
        }

        if (!userPrincipal.getEmail().endsWith("@student.hcmus.edu.vn")) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_EMAIL_IS_NOT_STUDENT_EMAIL), HttpStatus.BAD_REQUEST);
        }

        if (!cacheClient.expireOTP(userPrincipal.getId())) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.OTP_SENT), HttpStatus.BAD_REQUEST);
        }

        userService.sendEmailOTP(userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(new CodeResponse(0));
    }
}

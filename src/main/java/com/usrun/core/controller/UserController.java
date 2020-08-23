package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.payload.user.ChangePasswordRequest;
import com.usrun.core.payload.user.ResetPasswordRequest;
import com.usrun.core.payload.user.UserFilterRequest;
import com.usrun.core.payload.user.UserInfoRequest;
import com.usrun.core.payload.user.UserUpdateRequest;
import com.usrun.core.payload.user.VerifyStudentHcmusRequest;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.UniqueGenerator;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

  @Autowired
  private UniqueGenerator uniqueGenerator;

  @PostMapping("/info")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getCurrentUser(@RequestBody UserInfoRequest request) {
    try {
      long userId = request.getUserId();
      User user = userService.loadUser(userId);
      return ResponseEntity.ok(new CodeResponse(new UserInfoResponse(user)));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }

  @PostMapping("/filter")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findUser(@RequestBody UserFilterRequest request) {
    try {
      int count = request.getCount() > 0 ? request.getCount() : 10;
      int offset = Math.max(0, request.getOffset() - 1);
      List<UserFilterDTO> users = userRepository
          .findUserIsEnable('%' + request.getKey() + '%', offset, count);
      return ResponseEntity.ok(new CodeResponse(users));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/update")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUser(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody UserUpdateRequest userUpdateRequest
  ) {
    try {
      Date birthday = null;
      if (userUpdateRequest.getBirthday() != null) {
        birthday = new Date(userUpdateRequest.getBirthday());
      }

      User user = userService.updateUser(userPrincipal.getId(),
          userUpdateRequest.getName(),
          userUpdateRequest.getDeviceToken(),
          userUpdateRequest.getGender(), birthday,
          userUpdateRequest.getWeight(),
          userUpdateRequest.getHeight(),
          userUpdateRequest.getAvatar(),
          userUpdateRequest.getProvince());
      String jwt = tokenProvider.createTokenUserId(user.getId());
      return ResponseEntity.ok(new CodeResponse(new UserInfoResponse(user, jwt)));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/verifyStudentHcmus")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> verifyStudentHcmus(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody VerifyStudentHcmusRequest request) {
    try {
      Long userId = userPrincipal.getId();
      Boolean verified = userService.verifyOTP(userId, request.getOtp());
      return verified ?
          ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS)) :
          ResponseEntity.ok(new CodeResponse(ErrorCode.OTP_INVALID));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/resendOTP")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> resendOTP(@CurrentUser UserPrincipal userPrincipal) {
    try {
      if (userPrincipal.isHcmus()) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.USER_EMAIL_VERIFIED));
      }

      if (!userPrincipal.getEmail().endsWith("@student.hcmus.edu.vn")) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.USER_EMAIL_IS_NOT_STUDENT_EMAIL));
      }

      if (!cacheClient.expireOTP(userPrincipal.getId())) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.OTP_SENT));
      }

      userService.sendEmailOTP(userPrincipal.getId(), userPrincipal.getEmail());
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/changePassword")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> resetPassword(@CurrentUser UserPrincipal userPrincipal,
      @RequestBody ChangePasswordRequest request) {
    try {
      long userId = userPrincipal.getId();
      String oldPassword = request.getOldPassword();
      String newPassword = request.getNewPassword();
      userService.changePassword(userId, oldPassword, newPassword);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/resetPassword")
  public ResponseEntity<?> resetPassword(
      @RequestBody ResetPasswordRequest request) {
    try {
      String email = request.getEmail();
      User user = userService.loadUser(email);
      if (user.getType() != AuthType.local) {
        log.error("Reset password failed, email: {}, authType: {}", email, user.getType().name());
        return ResponseEntity.ok(new CodeResponse(ErrorCode.USER_RESET_PASSWORD_FAIL));
      }
      userService.resetPassword(user);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }
}

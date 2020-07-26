package com.usrun.core.controller.admin;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.LoginRequest;
import com.usrun.core.payload.UserInfoResponse;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author phuctt4
 */

@Slf4j
@RestController
@RequestMapping("/admin/user")
public class AdminAuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private TokenProvider tokenProvider;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      User user = userService.verifyUser(request.getEmail(), request.getPassword());
      if (user == null) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.USER_LOGIN_FAIL),
            HttpStatus.BAD_REQUEST);
      } else if (!user.getRoles().contains(new Role(RoleType.ROLE_ADMIN))) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new CodeResponse(ErrorCode.USER_DOES_NOT_PERMISSION));
      }

      String jwt = tokenProvider.createTokenUserId(user.getId());
      return ResponseEntity.ok(new CodeResponse(new UserInfoResponse(user, jwt)));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

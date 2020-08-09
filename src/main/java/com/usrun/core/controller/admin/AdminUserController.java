package com.usrun.core.controller.admin;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.dto.UserManagerDTO;
import com.usrun.core.payload.user.BanUserRequest;
import com.usrun.core.payload.user.GetUsersRequest;
import com.usrun.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author phuctt4
 */

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/banUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@RequestBody BanUserRequest request) {
        try {
            long userId = request.getUserId();
            boolean banned = request.isBanned();
            userService.banUser(userId, banned);
            return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
        } catch (CodeException ex) {
            return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
        } catch (Exception ex) {
            log.error("", ex);
            return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
        }
    }

    @PostMapping("/getAllUsersPaged")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsersPaged(@RequestBody GetUsersRequest request) {
        try {
            int offset
                    = request.getOffset() > 0 ? request.getOffset() - 1 : 0;
            int count
                    = request.getLimit() > 0 ? request.getLimit() : 10;

            List<UserManagerDTO> users = userService.getAllUsersPaged(offset, count);
            return new ResponseEntity<>(new CodeResponse(users), HttpStatus.OK);

        } catch (CodeException ex) {
            return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
        } catch (Exception ex) {
            log.error("", ex);
            return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
        }
    }
}

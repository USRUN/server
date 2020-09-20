package com.usrun.core.controller.admin;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.app.UpdateVersionRequest;
import com.usrun.core.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/admin/app")
public class AdminAppController {

  @Autowired
  private AppService appService;

  @PostMapping("/update-version")
  public ResponseEntity<?> updateAppVersion(@RequestBody UpdateVersionRequest request) {
    try {
      appService.setAppVersion(request.getVersion());
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
    } catch (Exception e) {
      log.error("Update Version failed, {}", e.getMessage(), e);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }
}

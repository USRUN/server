package com.usrun.core.controller;

import com.usrun.core.payload.CodeResponse;
import com.usrun.core.service.AppService;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author phuctt4
 */

@Slf4j
@RestController
@RequestMapping("/app")
public class AppController {

  private final AppService appService;

  public AppController(AppService appService) {
    this.appService = appService;
  }

  @PostMapping("/version")
  public ResponseEntity<?> getAppVersion() {
    try {
      String appVersion = appService.getAppVersion();
      Map<String, String> map = Collections.singletonMap("version", appVersion);
      return ResponseEntity.ok(new CodeResponse(map));
    } catch (Exception e) {
      log.error("Get appVersion failed, {}", e.getMessage(), e);
      Map<String, String> map = Collections.singletonMap("version", "1.0.0");
      return ResponseEntity.ok(new CodeResponse(map));
    }
  }
}

package com.usrun.core.service;

import com.usrun.core.repository.AppRepository;
import com.usrun.core.utility.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * @author phuctt4
 */

@Slf4j
@Service
public class AppService {

  private final AppRepository appRepository;
  private final CacheClient cacheClient;

  public AppService(AppRepository appRepository, CacheClient cacheClient) {
    this.appRepository = appRepository;
    this.cacheClient = cacheClient;
  }

  public String getAppVersion() {
    String appVersion = cacheClient.getAppVersion();
    if (appVersion == null) {
      appVersion = appRepository.getAppVersion();
      if (appVersion == null) {
        appVersion = "1.0.0";
      }
    }
    return appVersion;
  }

  public void setAppVersion(String version) {
    appRepository.setAppVersion(version);
    cacheClient.setAppVersion(version);
  }
}

package com.usrun.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

  private String defaultThumbnailTeam;
  private String defaultBannerTeam;
  private String defaultAvatar;
  private long activityLock;
  private long maxImageSize;
  private final Auth auth = new Auth();
  private int node;
  private final Activity activity = new Activity();

  @Data
  public static class Auth {

    private String tokenSecret;
    private long tokenExpirationMs;
  }

  @Data
  public static final class Activity {

    private String key;
  }
}

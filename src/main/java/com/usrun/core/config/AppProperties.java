package com.usrun.core.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

  private String defaultThumbnailTeam;
  private long activityLock;
  private long maxImageSize;
  private final Auth auth = new Auth();
  private final OAuth2 oauth2 = new OAuth2();
  private final Track track = new Track();
  private int node;
  private final Activity activity = new Activity();

  @Getter
  @Setter
  public static class Auth {

    private String tokenSecret;
    private long tokenExpirationMs;
  }

  @Getter
  @Setter
  public static final class OAuth2 {

    private Provider google;

    @Getter
    @Setter
    public static final class Provider {

      private String clientId;
      private String clientSecret;
      private List<String> scopes;
    }
  }

  @Getter
  @Setter
  public static final class Track {

    private String key;
    private Long timeInMicroseconds;
  }

  @Getter
  @Setter
  public static final class Activity {

    private String key;
  }
}

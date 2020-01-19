package com.usrun.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private String redisUrl;

    public void setRedisUrl(String redisUrl) {
        this.redisUrl = redisUrl;
    }

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
}

package com.usrun.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

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

package com.usrun.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author phuctt4
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "imgur")
public class ImgurConfig {
  private String clientId;
}

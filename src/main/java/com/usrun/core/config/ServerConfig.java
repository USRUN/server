package com.usrun.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author phuctt4
 */

@Configuration
public class ServerConfig {
  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}

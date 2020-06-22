package com.usrun.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "amazon")
@Getter
@Setter
public class AmazonS3Config {

  private String endpointUrl;
  private String bucketName;
  private String accessKey;
  private String secretKey;
}

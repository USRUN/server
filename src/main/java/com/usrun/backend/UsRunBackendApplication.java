package com.usrun.backend;

import com.usrun.backend.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(
        basePackageClasses = {
                UsRunBackendApplication.class,
                Jsr310JpaConverters.class
        }
)
@EnableConfigurationProperties(AppProperties.class)
public class UsRunBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsRunBackendApplication.class, args);
    }
}

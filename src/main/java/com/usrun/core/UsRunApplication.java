package com.usrun.core;

import com.usrun.core.config.AppProperties;
import com.usrun.core.service.TeamLeaderBoardWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EntityScan(
        basePackageClasses = {
            UsRunApplication.class
        }
)
@EnableConfigurationProperties(AppProperties.class)
public class UsRunApplication {

    @Autowired
    TeamLeaderBoardWorker teamLeaderBoardWorker;

    public static void main(String[] args) {
        SpringApplication.run(UsRunApplication.class, args);
    }
}

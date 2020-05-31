package com.usrun.core.listener;

import com.usrun.core.model.Team;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.service.ActivityService;
import com.usrun.core.utility.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author phuctt4
 */

@Component
@Slf4j
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ActivityService activityService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        activityService.setCountAllActivityByTeam();
        log.info("Set count all activity by Team");
    }
}

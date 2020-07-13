/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.service;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author anhhuy
 */
@Component
public class TeamLeaderBoardWorker {

    private static final Logger _logger = LoggerFactory.getLogger(TeamLeaderBoardWorker.class);

    @Autowired
    private TeamService teamService;
    @PostConstruct
    public boolean run() {
        System.out.println("Run worker");
        System.out.println(teamService);
        BackgroundService.schedule("BuildTeamLeaderBoard", new LeaderBoardBuilder(), 0, 30, TimeUnit.MINUTES);
        return true;
    }

    public class LeaderBoardBuilder implements Runnable {
        

        @Override
        public void run() {
            try {
                teamService.buildTeamLeaderBoard();
            } catch (Exception ex) {
                _logger.error(ex.getMessage(), ex);
            } finally {
            }
        }
    }
}

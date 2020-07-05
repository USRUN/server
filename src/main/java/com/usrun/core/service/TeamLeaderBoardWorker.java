/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author anhhuy
 */
public class TeamLeaderBoardWorker {

    private static final Logger _logger = LoggerFactory.getLogger(TeamLeaderBoardWorker.class);
    public static final TeamLeaderBoardWorker INSTANCE = new TeamLeaderBoardWorker();

    public boolean run() {
        BackgroundService.schedule("Warmup.MSG", new LeaderBoardBuilder(), 0, 1, TimeUnit.HOURS);
        return true;
    }

    public class LeaderBoardBuilder implements Runnable {

        @Override
        public void run() {
            try {

            } catch (Exception ex) {
                _logger.error(ex.getMessage(), ex);
            } finally {
            }
        }
    }
}

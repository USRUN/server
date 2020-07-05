package com.usrun.core.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thangbq
 */
public class BackgroundService {

    private static final ExecutorService executors = Executors.newFixedThreadPool(16);
    private static final ScheduledExecutorService schedulor = Executors.newScheduledThreadPool(2);
    private static final Logger logger = LoggerFactory.getLogger(BackgroundService.class);

    public static void execute(String processName, Runnable thread) {
        executors.execute(() -> {
            try {
                thread.run();
            } catch (Exception ex) {
                logger.error("Error execute " + processName, ex);
            } finally {
            }
        });
    }

    public static <T> Future<T> submit(String processName, Callable<T> thread) {
        return executors.submit(() -> {
            try {
                return thread.call();
            } catch (Exception ex) {
                logger.error("Error execute " + processName, ex);
            } finally {
            }
            return null;
        });
    }

    public static void schedule(String processName, Runnable thread, long period, TimeUnit unit) {
        schedulor.scheduleAtFixedRate(() -> {
            try {
                thread.run();
            } catch (Exception ex) {
                logger.error("Error execute " + processName, ex);
            } finally {
            }
        }, period, period, unit);
    }

    public static void schedule(String processName, Runnable thread, long delay, long period, TimeUnit unit) {
        schedulor.scheduleAtFixedRate(() -> {
            try {
                thread.run();
            } catch (Exception ex) {
                logger.error("Error execute " + processName, ex);
            } finally {
            }
        }, delay, period, unit);
    }
}

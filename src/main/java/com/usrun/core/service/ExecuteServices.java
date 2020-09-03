/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huyna3
 */
public class ExecuteServices {

  private static final ExecutorService executor = new ThreadPoolExecutor(1, 8, 0,
      TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

  public static void execute(Runnable task) {
    executor.execute(task);

  }

  public static void submit(Runnable task) {
    executor.submit(task);
  }
}

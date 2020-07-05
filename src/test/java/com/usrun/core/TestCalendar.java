/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import java.util.Date;

/**
 *
 * @author lap11382-local
 */
public class TestCalendar {

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        Date date = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println("Today: " + today);
        System.out.println("Monday of the Week: " + date);
        System.out.println("Sunday of the Week: " + sunday);
    }

}

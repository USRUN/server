/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.activity;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna
 */
@Getter
@Setter
public class UserStatResp {
    private int numberActivity;
    private long totalDistance;
    private long totalStep;
    private long totalTime;
    private double avgTime;
    private double avgPace;
    private double avgheart;
    private long totalCal;
    private double avgElev;
    private long maxElev;
    
    public UserStatResp(){
        this.avgElev = 0;
        this.avgPace = 0;
        this.avgTime = 0;
        this.avgheart = 0;
        this.maxElev = 0;
        this.numberActivity = 0;
        this.totalCal = 0;
        this.totalDistance = 0;
        this.totalStep = 0;
        this.totalTime = 0;
    }
}

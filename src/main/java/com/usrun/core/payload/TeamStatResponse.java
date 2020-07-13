/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload;

import com.usrun.core.payload.dto.TeamStatDTO;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna
 */
@Getter
@Setter
public class TeamStatResponse {

    private int rank;
    private long totalDistance;
    private long maxTime;
    private long maxDistance;
    private long memInWeek;
    private int totalMember;
    private long totalActivity;

    public TeamStatResponse(int rank, long totalDistance, long maxTime, long maxDistance, long memInWeek, int totalMember, long totalActivity) {
        this.rank = rank;
        this.totalDistance = totalDistance;
        this.maxTime = maxTime;
        this.maxDistance = maxDistance;
        this.memInWeek = memInWeek;
        this.totalMember = totalMember;
        this.totalActivity = totalActivity;
    }
    

}

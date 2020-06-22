/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Getter
@Setter
public class EventReq {
    
    private short status;
    private String eventName;
    private String subtitle;
    private String thumbnail;
    private Date startTime;
    private Date endTime;
    private short delete;

    public EventReq(short status, String eventName, String subtitle, String thumbnail, Date startTime, Date endTime, short delete) {
        this.status = status;
        this.eventName = eventName;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.delete = delete;
    }
    
}

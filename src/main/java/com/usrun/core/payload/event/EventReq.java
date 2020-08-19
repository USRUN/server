/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huyna3
 */
@Getter
@Setter
public class EventReq {

    private short status;
    private String eventName;
    private List<Long> organizers;
    private String subtitle;
    private String thumbnail;
    private Date startTime;
    private Date endTime;
    private short delete;
    private String poster;
    private String banner;
    private String poweredBy;
    private List<String> eventDetail;

    public EventReq(short status, String eventName, List<Long> organizers, String subtitle, String thumbnail, Date startTime, Date endTime, short delete, String poster, String banner, String poweredBy, List<String> eventDetail) {
        this.status = status;
        this.eventName = eventName;
        this.organizers = organizers;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.delete = delete;
        this.poster = poster;
        this.banner = banner;
        this.poweredBy = poweredBy;
        this.eventDetail = eventDetail;
    }
}

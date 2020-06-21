/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.sponsor;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Setter
@Getter
public class SponsorCreateReq {
    private long eventId;
    private List<Long> organizationId;

    public SponsorCreateReq(long eventId, List<Long> organizationId) {
        this.eventId = eventId;
        this.organizationId = organizationId;
    }
    
    
}

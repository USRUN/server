/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Getter
@Setter
public class EventOrganization {
    private long organizationId;
    private String avatar;
    private String name;

    public EventOrganization(long organizationId, String avatar, String name) {
        this.organizationId = organizationId;
        this.avatar = avatar;
        this.name = name;
    }
}

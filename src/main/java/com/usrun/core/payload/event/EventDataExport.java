/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDataExport {
    public long userId;
    public long teamId;
    public String email;
    public long distance;
    public long time;
    public Double pace;
}

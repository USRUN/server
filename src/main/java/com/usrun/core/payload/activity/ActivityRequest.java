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
 * @author huyna3
 */
@Getter
@Setter
public class ActivityRequest {
    private long userId;
    private int offset ;
    private int limit ;
}

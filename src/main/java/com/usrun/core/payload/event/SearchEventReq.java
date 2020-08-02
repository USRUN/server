/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Getter
@Setter
public class SearchEventReq {
    @Min(1)
    public int limit;
    @Min(0)
    public int offset;
    String name;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.team;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author huyna3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTeamOfEventReq {

    private long eventId;
    @Min(0)
    private int offset;
    @Min(1)
    private int limit;
    private String name;
}

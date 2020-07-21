/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 */
@Getter
@Setter
public class TeamNewMemberDTO {
    private long teamId;
    private int numberNewMember;

    public TeamNewMemberDTO(long teamId, int numberNewMember) {
        this.teamId = teamId;
        this.numberNewMember = numberNewMember;
    }
    
    public TeamNewMemberDTO(){}
    
}

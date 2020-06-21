/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Sponsor;

/**
 *
 * @author huyna3
 */
public interface SponsorRepository {
    
    Sponsor insert(Sponsor sponsor);

    Sponsor findById(long id);
}

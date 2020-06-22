/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Organization;
import org.springframework.stereotype.Repository;

/**
 *
 * @author huyna3
 */
public interface OrganizationRepository {

    int insert(Organization organization);

    Organization findById(long id);
    
    Organization findByName(String name);
}

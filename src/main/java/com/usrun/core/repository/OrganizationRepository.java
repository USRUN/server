/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Organization;
import java.util.List;

/**
 * @author huyna3
 */
public interface OrganizationRepository {

    int insert(Organization organization);

    int update(Organization organization);

    Organization findById(long id);

    Organization findByName(String name);

    List<Organization> listOrganization(int offset, int limit, String keyword);

    List<Organization> listAllOrganization();

}

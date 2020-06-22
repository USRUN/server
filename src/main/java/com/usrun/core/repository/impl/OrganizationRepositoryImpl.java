/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.Organization;
import com.usrun.core.repository.OrganizationRepository;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author huyna3
 */
@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRepositoryImpl.class);
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private MapSqlParameterSource mapOrganization(Organization organization) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("id", organization.getId());
    map.addValue("name", organization.getName());
    return map;
  }

  @Override
  public int insert(Organization organization) {
    MapSqlParameterSource map = mapOrganization(organization);
    try {
      int resp = namedParameterJdbcTemplate.update(
          "INSERT INTO organization(id,name)"
              + " VALUES(:id, :name)",
          map
      );
      return resp;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return -1;
    }
  }

  @Override
  public Organization findById(long id) {
    MapSqlParameterSource params = new MapSqlParameterSource("id", id);
    String sql = "SELECT * FROM Organization WHERE id = :id";
    List<Organization> organization = findOrganization(sql, params);
    if (organization.size() > 0) {
      return organization.get(0);
    } else {
      return null;
    }
  }

  @Override
  public Organization findByName(String name) {
    MapSqlParameterSource params = new MapSqlParameterSource("name", name);
    String sql = "SELECT * FROM Organization WHERE name = :name";
    List<Organization> organization = findOrganization(sql, params);
    if (organization.size() > 0) {
      return organization.get(0);
    } else {
      return null;
    }
  }

  private List<Organization> findOrganization(String sql, MapSqlParameterSource params) {
    List<Organization> listOrganization = namedParameterJdbcTemplate.query(sql,
        params,
        (rs, i) -> new Organization(rs.getLong("id"),
            rs.getString("name")
        ));
    if (listOrganization.size() > 0) {
      return listOrganization;
    } else {
      return Collections.emptyList();
    }
  }

}

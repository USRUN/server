/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.Sponsor;
import com.usrun.core.repository.SponsorRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author huyna3
 */
@Repository
public class SponsorRespositoryImpl implements SponsorRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MapSqlParameterSource mapSponsor(Sponsor sponsor) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("eventId", sponsor.getEventId());
        map.addValue("organizationId", sponsor.getOrganizationId());
        return map;
    }

    @Override
    public Sponsor insert(Sponsor sponsor) {
        MapSqlParameterSource map = mapSponsor(sponsor);
        try {
            namedParameterJdbcTemplate.update(
                    "INSERT INTO sponsor(eventId,organizationId)"
                    + " VALUES(:eventId, :organizationId)",
                    map
            );
            return sponsor;
        } catch (Exception ex) {
            return null;
        }
    }

    private List<Sponsor> findSponsor(String sql, MapSqlParameterSource params) {
        List<Sponsor> listSponsor = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new Sponsor(rs.getLong("eventId"),
                        rs.getLong("organizationId")
                ));
        if (listSponsor.size() > 0) {
            return listSponsor;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Sponsor findById(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

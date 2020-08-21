/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.Sponsor;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.SponsorType;
import com.usrun.core.payload.event.EventOrganization;
import com.usrun.core.repository.SponsorRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
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
        map.addValue("role", sponsor.getRole().toValue());
        return map;
    }

    @Override
    public Sponsor insert(Sponsor sponsor) {
        MapSqlParameterSource map = mapSponsor(sponsor);
        try {
            namedParameterJdbcTemplate.update(
                    "INSERT INTO sponsor(eventId,organizationId, role)"
                    + " VALUES(:eventId, :organizationId,:role)",
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
                        rs.getLong("organizationId"),
                        SponsorType.getSponsor(rs.getInt("role"))
                ));
        if (listSponsor.size() > 0) {
            return listSponsor;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Sponsor findById(long id) {
        throw new UnsupportedOperationException(
                "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public int[] addOrganizers(long eventId, List<Long> organizationIds) {
        List<Sponsor> data = organizationIds.stream()
                .map(item -> new Sponsor(eventId, item, SponsorType.POWERED))
                .collect(Collectors.toList());
        String sql = "INSERT INTO sponsor(eventId,organizationId, role) "
                + "VALUES(:eventId, :organizationId,:role)";
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(data.toArray());
        int[] resp = namedParameterJdbcTemplate.batchUpdate(sql, params);
        return resp;
    }

    @Override
    public List<EventOrganization> getEventOrganizationWithRole(long eventId, int role) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("eventId", eventId);
        map.addValue("role", role);
        String sql = "select * from organization where id in (select organizationId from sponsor where eventId = :eventId and role = :role)";
        List<EventOrganization> result = namedParameterJdbcTemplate.query(
                sql,
                map,
                (rs, i) -> new EventOrganization(
                        rs.getLong("id"),
                        rs.getString("avatar"),
                        rs.getString("name")));
        return result;
    }

}

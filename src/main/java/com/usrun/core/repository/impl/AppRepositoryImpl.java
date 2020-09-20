package com.usrun.core.repository.impl;

import com.usrun.core.repository.AppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author phuctt4
 */

@Slf4j
@Repository
public class AppRepositoryImpl implements AppRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public AppRepositoryImpl(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public String getAppVersion() {
    return namedParameterJdbcTemplate
        .query("SELECT version FROM app", (rs, i) -> rs.getString("version"))
        .stream()
        .findFirst()
        .orElse("1.0.0");
  }

  @Override
  public void setAppVersion(String version) {
    MapSqlParameterSource params = new MapSqlParameterSource("version", version);
    namedParameterJdbcTemplate.update("UPDATE app SET version = :version", params);
  }
}

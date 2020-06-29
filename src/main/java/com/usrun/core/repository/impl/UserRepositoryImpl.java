package com.usrun.core.repository.impl;

import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.payload.dto.UserLeaderBoardDTO;
import com.usrun.core.repository.UserRepository;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phuctt4
 */

@Repository
public class UserRepositoryImpl implements UserRepository {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  @Transactional
  public User insert(User user) {
    MapSqlParameterSource map = getMapUser(user);
    final KeyHolder holder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(
        "INSERT INTO user (displayName, email, password, userType, " +
            "avatar, lastLogin, weight, height, gender, " +
            "birthday, userCode, deviceToken, isEnabled, hcmus, createTime, updateTime) values (" +
            ":displayName, :email, :password, :userType, :avatar, :lastLogin, :weight, :height, " +
            ":gender, :birthday, :userCode, :deviceToken, :isEnabled, :hcmus, :createTime, :updateTime)",
        map,
        holder,
        new String[]{"GENERATED_ID"}
    );

    Number generatedId = holder.getKey();
    user.setId(generatedId.longValue());

    MapSqlParameterSource[] paramsRoles = user.getRoles().stream().map(role -> {
      MapSqlParameterSource paramsRole = new MapSqlParameterSource();
      paramsRole.addValue("userId", user.getId());
      paramsRole.addValue("roleId", role.getRoleType().toValue());
      return paramsRole;
    }).toArray(MapSqlParameterSource[]::new);
    namedParameterJdbcTemplate.batchUpdate(
        "INSERT INTO userRole (userId, roleId) values (:userId, :roleId)",
        paramsRoles
    );
    return user;
  }

  @Override
  public User update(User user) {
    user.setUpdateTime(new Date());
    MapSqlParameterSource map = getMapUser(user);
    namedParameterJdbcTemplate.update(
        "UPDATE user SET displayName = :displayName, email = :email, "
            + "password = :password, userType = :userType, "
            + "avatar = :avatar , lastLogin = :lastLogin, "
            + "weight = :weight, height = :height, gender = :gender, "
            + "birthday = :birthday, userCode = :userCode, deviceToken = :deviceToken, "
            + "isEnabled = :isEnabled, hcmus = :hcmus, "
            + "updateTime = :updateTime, province = :province "
            + "WHERE userId = :userId",
        map
    );
    return user;
  }

  @Override
  public User findById(Long userId) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    String sql = "SELECT * FROM user WHERE userId = :userId";
    return getUser(sql, params);
  }

  @Override
  public User findUserByEmail(String email) {
    MapSqlParameterSource params = new MapSqlParameterSource("email", email);
    String sql = "SELECT * FROM user WHERE email = :email";
    return getUser(sql, params);
  }

  @Override
  public List<UserFilterDTO> findUserIsEnable(String keyword, Pageable pageable) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("keyword", keyword);
    params.addValue("size", pageable.getPageSize());
    params.addValue("offset", pageable.getOffset());
    String sql = "SELECT u.* " +
        "FROM user u " +
        "WHERE u.isEnabled = TRUE " +
        "AND (u.displayName LIKE :keyword OR u.email LIKE :keyword OR u.userCode LIKE :keyword) " +
        "LIMIT :size " +
        "OFFSET :offset";
    return getUserFilterDTO(sql, params);
  }

  @Override
  public User findUserByCode(String code) {
    MapSqlParameterSource params = new MapSqlParameterSource("userCode", code);
    String sql = "SELECT * FROM user WHERE userCode = :userCode";
    return getUser(sql, params);
  }

  @Override
  public List<UserLeaderBoardDTO> getUserLeaderBoard(List<Long> users) {
    if (users == null || users.isEmpty()) {
      users = Collections.singletonList(-1L);
    }
    MapSqlParameterSource params = new MapSqlParameterSource("users", users);
    String sql = "SELECT userId, displayName, avatar FROM user WHERE userId IN (:users)";
    return namedParameterJdbcTemplate.query(sql, params,
        (rs, i) -> new UserLeaderBoardDTO(rs.getLong("userId"), rs.getString("displayName"),
            rs.getString("avatar")));
  }

  @Override
  public List<UserFilterDTO> getUserByMemberType(long teamId, TeamMemberType teamMemberType, int offset, int limit) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("teamId", teamId);
    params.addValue("teamMemberType", teamMemberType.toValue());
    params.addValue("offset", offset * limit);
    params.addValue("limit", limit);
    String sql  = "SELECT u.* "
        + "FROM user u, teamMember tm "
        + "WHERE tm.teamId = :teamId "
        + "AND teamMemberType = :teamMemberType "
        + "AND tm.userId = u.userId "
        + "LIMIT :offset, :limit";
    return getUserFilterDTO(sql, params);
  }

  @Override
  public List<UserFilterDTO> getAllMemberByLessEqualTeamMemberType(long teamId,
      TeamMemberType teamMemberType, int offset, int limit) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("teamId", teamId);
    params.addValue("offset", offset * limit);
    params.addValue("limit", limit);
    params.addValue("teamMemberType", teamMemberType.toValue());

    String sql = "SELECT u.* FROM teamMember tm, user u "
        + "WHERE tm.teamId = :teamId "
        + "AND tm.teamMemberType <= :teamMemberType "
        + "AND tm.userId = u.userId "
        + "LIMIT :limit "
        + "OFFSET :offset";

    return getUserFilterDTO(sql, params);
  }

  private List<User> getUsers(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new User(
            rs.getLong("userId"),
            rs.getString("displayName"),
            rs.getString("email"),
            rs.getString("password"),
            AuthType.fromInt(rs.getInt("userType")),
            rs.getString("avatar"),
            rs.getDate("lastLogin"),
            rs.getDouble("weight"),
            rs.getDouble("height"),
            Gender.fromInt(rs.getInt("gender")),
            rs.getDate("birthday"),
            rs.getString("userCode"),
            rs.getString("deviceToken"),
            rs.getBoolean("isEnabled"),
            rs.getBoolean("hcmus"),
            rs.getDate("createTime"),
            rs.getDate("updateTime"),
            rs.getInt("province")
        ));
  }

  private User getUser(String sql, MapSqlParameterSource params) {
    Optional<User> optionalUser = getUsers(sql, params).stream().findFirst();
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      List<Role> list = namedParameterJdbcTemplate
          .query("SELECT roleId FROM userRole WHERE userId = :userId",
              new MapSqlParameterSource("userId", user.getId()),
              (resultSet, i) -> new Role(RoleType.fromInt(resultSet.getInt("roleId"))));
      Set<Role> roles = new HashSet<>(list);
      user.setRoles(roles);
      return user;
    } else {
      return null;
    }
  }

  private MapSqlParameterSource getMapUser(User user) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("userId", user.getId());
    map.addValue("displayName", user.getName());
    map.addValue("email", user.getEmail());
    map.addValue("password", user.getPassword());
    map.addValue("userType", user.getType() == null ? null : user.getType().toValue());
    map.addValue("avatar", user.getAvatar());
    map.addValue("lastLogin", user.getLastLogin());
    map.addValue("weight", user.getWeight());
    map.addValue("height", user.getHeight());
    map.addValue("gender", user.getGender() == null ? null : user.getGender().toValue());
    map.addValue("birthday", user.getBirthday());
    map.addValue("userCode", user.getCode());
    map.addValue("deviceToken", user.getDeviceToken());
    map.addValue("isEnabled", user.isEnabled());
    map.addValue("hcmus", user.isHcmus());
    map.addValue("createTime", user.getCreateTime());
    map.addValue("updateTime", user.getUpdateTime());
    map.addValue("province", user.getProvince());
    return map;
  }

  private List<UserFilterDTO> getUserFilterDTO(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new UserFilterDTO(
            rs.getLong("userId"),
            rs.getString("displayName"),
            rs.getString("email"),
            rs.getString("userCode"),
            Gender.fromInt(rs.getInt("gender")),
            rs.getDate("birthday"),
            rs.getString("avatar")
        )
    );
  }
}

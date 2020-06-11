package com.usrun.core.repository.impl;

import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                "UPDATE user SET displayName = :displayName, email = :email, password = :password, userType = :userType, " +
                        "avatar = :avatar , lastLogin = :lastLogin, " +
                        "weight = :weight, height = :height, gender = :gender, " +
                        "birthday = :birthday, userCode = :userCode, deviceToken = :deviceToken, " +
                        "isEnabled = :isEnabled, " +
                        "hcmus := hcmus, updateTime = :updateTime WHERE userId = :userId",
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

    private User getUser(String sql, MapSqlParameterSource params) {
        Optional<User> optionalUser = namedParameterJdbcTemplate.query(
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
                        rs.getDate("updateTime")
                )).stream().findFirst();
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Role> list = namedParameterJdbcTemplate.query("SELECT roleId FROM userRole WHERE userId = :userId",
                    new MapSqlParameterSource("userId", user.getId()),
                    (resultSet, i) -> new Role(RoleType.fromInt(resultSet.getInt("roleId"))));
            Set<Role> roles = list.stream().collect(Collectors.toSet());
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
        return map;
    }

    private List<UserFilterDTO> getUserFilterDTO(String sql, MapSqlParameterSource params) {
        List<UserFilterDTO> userFilterDTOS = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new UserFilterDTO(
                        rs.getLong("userId"),
                        rs.getString("displayName"),
                        rs.getString("email"),
                        rs.getString("userCode"),
                        Gender.fromInt(rs.getInt("gender")),
                        rs.getDate("birthday")
                )
        );
        return userFilterDTOS;
    }
}

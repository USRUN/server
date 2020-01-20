package com.usrun.core.repository.impl;

import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                "INSERT INTO users (name, email, password, type, " +
                        "open_id, img, last_login, weight, height, gender, " +
                        "birthday, code, device_token, name_slug, is_enabled, hcmus) values (" +
                        ":name, :email, :password, :type, :open_id, :img, :last_login, :weight, :height, " +
                        ":gender, :birthday, :code, :device_token, :name_slug, :is_enabled, :hcmus)",
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
                "INSERT INTO user_roles (user_id, role_id) values (:userId, :roleId)",
                paramsRoles
        );
        return user;
    }

    @Override
    public User update(User user) {
        MapSqlParameterSource map = getMapUser(user);
        namedParameterJdbcTemplate.update(
                "UPDATE users SET name = :name, email = :email, password = :password, type = :type, " +
                        "open_id = :open_id, img = :img , last_login = :last_login, " +
                        "weight = :weight, height = :height, gender = :gender, " +
                        "birthday = :birthday, code = :code, device_token = :device_token, " +
                        "name_slug = :name_slug, is_enabled = :is_enabled, hcmus := hcmus WHERE id = :id",
                map
        );
        return user;
    }

    @Override
    public User findById(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", userId);
        String sql = "SELECT * FROM users WHERE id = :id";
        return getUser(sql, params);
    }

    @Override
    public User findUserByEmail(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        String sql = "SELECT * FROM users WHERE email = :email";
        return getUser(sql, params);
    }

    @Override
    public List<User> findUserIsEnable(String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public User findUserByCode(String code) {
        MapSqlParameterSource params = new MapSqlParameterSource("code", code);
        String sql = "SELECT * FROM users WHERE code = :code";
        return getUser(sql, params);
    }

    private User getUser(String sql, MapSqlParameterSource params) {
        Optional<User> optionalUser = namedParameterJdbcTemplate.queryForObject(
                sql,
                params,
                (rs, i) -> Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        AuthType.fromInt(rs.getInt("type")),
                        rs.getString("open_id"),
                        rs.getString("img"),
                        rs.getDate("last_login"),
                        rs.getDouble("weight"),
                        rs.getDouble("height"),
                        Gender.fromInt(rs.getInt("gender")),
                        rs.getDate("birthday"),
                        rs.getString("code"),
                        rs.getString("device_token"),
                        rs.getString("name_slug"),
                        rs.getBoolean("is_enabled"),
                        rs.getBoolean("hcmus")
                ))
        );
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Role> list = namedParameterJdbcTemplate.query("SELECT role_id FROM user_roles WHERE user_id = :userId",
                    new MapSqlParameterSource("userId", user.getId()),
                    (resultSet, i) -> new Role(RoleType.fromInt(resultSet.getInt("role_id"))));
            Set<Role> roles = list.stream().collect(Collectors.toSet());
            user.setRoles(roles);
            return user;
        } else {
            return null;
        }
    }

    private MapSqlParameterSource getMapUser(User user) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", user.getId());
        map.addValue("name", user.getName());
        map.addValue("email", user.getEmail());
        map.addValue("password", user.getPassword());
        map.addValue("type", user.getType().toValue());
        map.addValue("open_id", user.getOpenId());
        map.addValue("img", user.getImg());
        map.addValue("last_login", user.getLastLogin());
        map.addValue("weight", user.getWeight());
        map.addValue("height", user.getHeight());
        map.addValue("gender", user.getGender().toValue());
        map.addValue("birthday", user.getBirthday());
        map.addValue("code", user.getCode());
        map.addValue("device_token", user.getDeviceToken());
        map.addValue("name_slug", user.getNameSlug());
        map.addValue("is_enabled", user.isEnabled());
        map.addValue("hcmus", user.isHcmus());
        return map;
    }
}

package com.usrun.core.repository;

import com.usrun.core.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User insert(User user);

    User update(User user);

    User findById(Long userId);

    User findUserByEmail(String email);

//    @Modifying
//    @Query("UPDATE User u set u.lastLogin = current_date where u.id = :userId")
//    void updateLastLogin(@Param("userId") Long userId);

//    @Query(value = "SELECT u.* FROM users u WHERE u.isEnabled = TRUE AND (u.name LIKE :keyword OR u.email LIKE :keyword OR u.code LIKE :keyword)")
    List<User> findUserIsEnable(String keyword, Pageable pageable);

    User findUserByCode(String code);
}

package com.usrun.core.repository;

import com.usrun.core.model.User;
import com.usrun.core.payload.dto.UserFilterDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository {

    User insert(User user);

    User update(User user);

    User findById(Long userId);

    User findUserByEmail(String email);

//    @Query(value = "SELECT u.* FROM users u WHERE u.isEnabled = TRUE AND (u.name LIKE :keyword OR u.email LIKE :keyword OR u.code LIKE :keyword)")
    List<UserFilterDTO> findUserIsEnable(String keyword, Pageable pageable);

    User findUserByCode(String code);
}

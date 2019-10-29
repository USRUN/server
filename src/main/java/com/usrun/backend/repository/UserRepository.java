package com.usrun.backend.repository;

import com.usrun.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u set u.lastLogin = current_date where u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId);

    @Query(value = "SELECT u FROM User u WHERE u.name LIKE :keyword OR u.email LIKE :keyword OR u.code LIKE :keyword")
    List<User> findUser(String keyword, Pageable pageable);
}

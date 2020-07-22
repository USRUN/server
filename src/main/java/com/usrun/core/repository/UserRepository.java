package com.usrun.core.repository;

import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.payload.dto.UserFilterWithTypeDTO;
import com.usrun.core.payload.dto.UserLeaderBoardDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

  User insert(User user);

  User update(User user);

  User findById(Long userId);

  User findUserByEmail(String email);

  //    @Query(value = "SELECT u.* FROM users u WHERE u.isEnabled = TRUE AND (u.name LIKE :keyword OR u.email LIKE :keyword OR u.code LIKE :keyword)")
  List<UserFilterDTO> findUserIsEnable(String keyword, int offset, int count);

  List<UserFilterWithTypeDTO> findUserIsEnable(String keyword, long teamId, int offset, int count);

  User findUserByCode(String code);

  List<UserLeaderBoardDTO> getUserLeaderBoard(List<Long> users);

  List<UserFilterDTO> getUserByMemberType(long teamId, TeamMemberType teamMemberType, int offset, int limit);

  List<UserFilterWithTypeDTO> getAllMemberByLessEqualTeamMemberType(long teamId,
      TeamMemberType teamMemberType, int offset, int limit);

  User findByEmailOrUserCode(String emailOrUserCode);
}

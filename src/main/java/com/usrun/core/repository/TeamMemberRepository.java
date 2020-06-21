package com.usrun.core.repository;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.TeamMemberCountDTO;
import java.util.List;
import java.util.Set;

public interface TeamMemberRepository {

  TeamMember insert(TeamMember toInsert);

  TeamMember update(TeamMember toUpdate);

  boolean delete(TeamMember toDelete);

  TeamMember findById(Long teamId, Long userId);

  List<TeamMember> filterByMemberType(long teamId, TeamMemberType toFilter);

  List<TeamMember> getAllMemberOfTeam(long teamId);

  List<TeamMember> getAllMemberOfTeamPaged(long teamId, int pageNum, int perPage);
}

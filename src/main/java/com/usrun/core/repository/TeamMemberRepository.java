package com.usrun.core.repository;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.TeamNewMemberDTO;
import com.usrun.core.payload.dto.TeamStatDTO;
import java.util.List;

public interface TeamMemberRepository {

  TeamMember insert(TeamMember toInsert);

  TeamMember update(TeamMember toUpdate);

  boolean delete(TeamMember toDelete);

  TeamMember findById(Long teamId, Long userId);

  List<TeamMember> filterByMemberType(long teamId, TeamMemberType toFilter);

  List<TeamMember> getAllMemberOfTeam(long teamId);
  
  List<Long> getAllIdMemberOfTeam(long teamId);
  
  List<TeamNewMemberDTO> getNewMemberInWeek();
  
  List<TeamMember> getMemberAvailable(long teamId); // member, admin, owner
  
  List<TeamStatDTO> getTeamStat();

  List<TeamMember> getAll();

  List<TeamMember> getAllByLessEqualTeamMemberType(TeamMemberType teamMemberType);
  
}

package com.usrun.core.repository;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.type.TeamMemberType;
import java.util.List;
import java.util.Set;

public interface TeamRepository {

  Team insert(Team toInsert, Long ownerUserId);

  Team update(Team toUpdate);

  boolean delete(Team toDelete);

  Team findTeamById(Long teamId);

  Team findTeamByName(String teamName);

  boolean joinTeam(Long requestingId, Long teamId);

  boolean cancelJoinTeam(Long requestingId, Long teamId);

  int changeTotalMember(Long teamId, int changeAmount);

  List<User> getMemberListByType(Long teamId, TeamMemberType toGet);

  boolean updateTeamMemberType(Long teamId, Long memberId, TeamMemberType action);

  Set<Team> findTeamWithNameContains(String searchString, int offset, int count);

  Set<Long> getTeamsByUser(long userId);

  Set<Team> getTeamSuggestionByUserLocation(String district, String province, int howMany,
      Set<Long> toExclude);

  List<Team> findAllTeam();
}

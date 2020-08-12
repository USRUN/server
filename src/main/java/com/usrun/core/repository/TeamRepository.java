package com.usrun.core.repository;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.LeaderBoardTeamDTO;
import com.usrun.core.payload.dto.ShortTeamDTO;
import com.usrun.core.payload.dto.TeamDTO;
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

    Set<Team> getTeamSuggestionByUserLocation(int province, int count,
            Set<Long> toExclude);

    List<Team> findAllTeam();

    List<Team> getTeamsByUserReturnTeam(long userId);

    List<TeamDTO> getTeamsByUserAndNotEqualTeamMemberTypeReturnTeam(long userId, TeamMemberType teamMemberType);

    List<LeaderBoardTeamDTO> getLeaderBoard(long teamId);

    boolean acceptTeam(long userId, long teamId);

    List<Team> getTeamOfEvent(long eventId, int offset, int limit);

    List<Team> searchTeamOfEvent(long eventId, String name, int offset, int limit);

    List<ShortTeamDTO> getShortTeams(List<Long> teams);
}

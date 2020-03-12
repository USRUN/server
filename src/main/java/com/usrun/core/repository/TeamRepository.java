package com.usrun.core.repository;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.type.TeamMemberType;

import java.util.List;

public interface TeamRepository {
    Team insert(Team toInsert,Long ownerUserId);

    Team update(Team toUpdate);

    boolean delete(Team toDelete);

    Team findTeamById(Long teamId);

    Team findTeamByName(String teamName);

    boolean joinTeam(Long requestingId,Long teamId);

    List<User> getMemberListByType(Long teamId, TeamMemberType toGet);

    boolean updateTeamMemberType(Long teamId,Long memberId, TeamMemberType action);
}

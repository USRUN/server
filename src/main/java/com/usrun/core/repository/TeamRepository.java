package com.usrun.core.repository;

import com.usrun.core.model.Team;

public interface TeamRepository {
    Team insert(Team toInsert,Long ownerUserId);

    Team update(Team team);

    Team findTeamById(Long teamId);

    Team findTeamByName(String teamName);

    boolean joinTeam(Long teamId);

    boolean getPendingList(Long teamId);

    boolean updatePendingList(Long teamId, int action);
}

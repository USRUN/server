package com.usrun.core.repository;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;

import java.util.Optional;


public interface TeamMemberRepository {
    TeamMember insert(TeamMember toInsert);
    TeamMember update(TeamMember toUpdate);
    TeamMember findById(Long userIdToFind);
    Optional<TeamMember[]> filterByMemberType(TeamMemberType toFilter);
}

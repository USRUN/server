package com.usrun.core.service;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.TeamException;
import com.usrun.core.model.Team;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.utility.CacheClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private CacheClient cacheClient;

    @Transactional
    public Team createTeam(
            Long ownerId, String teamName, String thumbnail, int privacy, String location, String description
    ) {
        Team toCreate = new Team(teamName, thumbnail, location, privacy, new Date(), description);

        toCreate = teamRepository.insert(toCreate, ownerId);
        cacheClient.setTeamMemberType(toCreate.getId(), ownerId, TeamMemberType.OWNER);

        return toCreate;
    }

    public boolean requestToJoinTeam(Long requestId, Long teamId) {
        return teamRepository.joinTeam(requestId, teamId);
    }

    public boolean updateTeamRole(Long teamId, Long pendingId, TeamMemberType toChangeInto) {
        teamRepository.updateTeamMemberType(teamId, pendingId, toChangeInto);
        cacheClient.setTeamMemberType(teamId, pendingId, toChangeInto);
        return true;
    }

    public TeamMemberType loadTeamMemberType(long teamId, long userId) {
        TeamMemberType teamMemberType = cacheClient.getTeamMemberType(teamId, userId);
        if (teamMemberType == null) {
            TeamMember teamMember = teamMemberRepository.findById(teamId, userId);
            if (teamMember == null) {
                String msg = String.format("User %s not belong to Team %s", userId, teamId);
                LOGGER.warn(msg);
                throw new TeamException(msg, ErrorCode.TEAM_USER_NOT_FOUND);
            }
            teamMemberType = teamMember.getTeamMemberType();
        }
        return teamMemberType;
    }
}

//    private Team updateTeam(
//            User user, String name, String img, String logo, Integer leagueId, String description
//    ) {
//        Team team = user.getTeam();
//
//        team.setName(name);
//
//        LeagueType[] leagueTypes = LeagueType.values();
//        if(leagueId != null && leagueId < leagueTypes.length) {
//            team.setLeagueType(leagueTypes[leagueId]);
//        }
//
//        if(img != null) {
//            String imgUrl = amazonClient.uploadFile(img, "team-" + team.getId() + "-img");
//            team.setImg(imgUrl);
//        }
//
//        if(logo != null) {
//            String logoUrl = amazonClient.uploadFile(logo, "team-" + team.getId() + "-logo");
//            team.setLogo(logoUrl);
//        }
//
//        if(description != null) {
//            team.setDescription(description);
//        }
//
//        teamRepository.save(team);
//        logger.info("Update team {}", team.getId());
//        return team;
//    }
//}

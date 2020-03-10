 package com.usrun.core.service;

import com.usrun.core.model.Team;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

 @Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Transactional
    public Team createTeam(
            Long ownerId, String teamName, String thumbnail, int privacy, String location, String description
    ) {
        Team toCreate = new Team(teamName,thumbnail,location,privacy,new Date(),description);

        toCreate = teamRepository.insert(toCreate,ownerId);

        return toCreate;
    }

    public boolean requestToJoinTeam(Long requestId, Long teamId){
        return teamRepository.joinTeam(requestId,teamId);
    }

    public boolean approvePendingMember(Long requestId, Long teamId, Long pendingId){
        TeamMember requestUser = teamMemberRepository.findById(teamId,requestId);

        //check if it's the owner/admin who posted the request
        if(requestUser.getTeamMemberType() != TeamMemberType.OWNER && requestUser.getTeamMemberType() != TeamMemberType.ADMIN){
            return false;
        }

        teamRepository.updateTeamMemberType(teamId,pendingId,TeamMemberType.MEMBER);
        return true;
    }

    public boolean updateTeamAdmin(Long requestId, Long teamId, Long pendingId, TeamMemberType toChangeInto){
        TeamMember requestUser = teamMemberRepository.findById(teamId,requestId);

        //check if it's the owner who posted the request
        if(requestUser.getTeamMemberType() != TeamMemberType.OWNER){
            return false;
        }

        teamRepository.updateTeamMemberType(teamId,pendingId,toChangeInto);
        return true;
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

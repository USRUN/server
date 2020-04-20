package com.usrun.core.service;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.TeamException;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private UserService userService;

    @Transactional
    public Team createTeam(
            Long ownerId, String teamName, String thumbnail, int privacy, String location, String description
    ) {
        Team toCreate = new Team(teamName, thumbnail, location, privacy, new Date(), description);

        toCreate = teamRepository.insert(toCreate, ownerId);
        cacheClient.setTeamMemberType(toCreate.getId(), ownerId, TeamMemberType.OWNER);

        addTeamToCache(toCreate.getId(),ownerId);

        return toCreate;
    }

    public void deleteTeam(Long ownerId, Long teamId) throws Exception {
        Team toDelete = teamRepository.findTeamById(teamId);

        if(toDelete == null){
            throw new Exception("Team Not Found");
        }

        teamRepository.delete(toDelete);

        List<TeamMember> toRemove = teamMemberRepository.getAllMemberOfTeam(teamId);

        toRemove.forEach((teamMember -> {
            removeTeamFromCache(teamId,teamMember.getUserId());
            teamMemberRepository.delete(teamMember);
        }));
    }

    public Team getTeamById(Long teamId){
        Team toGet = teamRepository.findTeamById(teamId);

        if(toGet == null){
           throw new DataRetrievalFailureException("Team not found");
        }

        return toGet;
    }

    private void addTeamToCache(Long teamId, Long userId){
        User current = userService.loadUser(userId);

        if(current.getTeams() == null){
            current.setTeams(new HashSet<>());
        }

        current.getTeams().add(teamId);

        cacheClient.setUser(current);
    }

    private void removeTeamFromCache(Long teamId,Long userId){
        User current = userService.loadUser(userId);
        Set<Long> currentTeam = current.getTeams();

        currentTeam.remove(teamId);

        cacheClient.setUser(current);
    }

    public boolean requestToJoinTeam(Long requestId, Long teamId) {
        return teamRepository.joinTeam(requestId, teamId);
    }

    public boolean updateTeamRole(Long teamId, Long memberId, TeamMemberType toChangeInto) {
        if(toChangeInto == TeamMemberType.OWNER){
            return false;
        }
        if(toChangeInto == TeamMemberType.BLOCKED){
            teamRepository.changeTotalMember(teamId,-1);
            removeTeamFromCache(teamId,memberId);
        }
        if(toChangeInto == TeamMemberType.MEMBER){
            teamRepository.changeTotalMember(teamId,1);
            addTeamToCache(teamId,memberId);
        }

        teamRepository.updateTeamMemberType(teamId, memberId, toChangeInto);
        cacheClient.setTeamMemberType(teamId, memberId, toChangeInto);
        return true;
    }

    public boolean cancelJoinTeam(Long requestId, Long teamId){
        return teamRepository.cancelJoinTeam(requestId,teamId);
    }

    public TeamMemberType loadTeamMemberType(Long teamId, Long userId) {
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

    public Team updateTeam(Long teamId, String teamName, String thumbnail, int privacy, String location, String description){
        Team toUpdate = teamRepository.findTeamById(teamId);

        if(toUpdate == null) {
            throw new DataRetrievalFailureException("Team not found");
        }

        if(teamName != null) toUpdate.setTeamName(teamName);
        if(thumbnail != null) toUpdate.setThumbnail(thumbnail);
        if(privacy != toUpdate.getPrivacy()) toUpdate.setPrivacy(privacy);
        if(location != null) toUpdate.setLocation(location);
        if(description != null) toUpdate.setDescription(description);

        teamRepository.update(toUpdate);
        LOGGER.info("Update team {}", teamId);

        return toUpdate;
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

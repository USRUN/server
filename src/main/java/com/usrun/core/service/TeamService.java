package com.usrun.core.service;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.LeaderBoardTeamDTO;
import com.usrun.core.payload.dto.UserLeaderBoardDTO;
import com.usrun.core.payload.dto.UserLeaderBoardInfo;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TeamService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private TeamMemberRepository teamMemberRepository;

  @Autowired
  private AmazonClient amazonClient;

  @Autowired
  private CacheClient cacheClient;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AppProperties appProperties;

  public Team createTeam(
      Long ownerId, int privacy, String teamName, String district, String province,
      String thumbnailBase64
  ) {
    String thumbnail = appProperties.getDefaultThumbnailTeam();
    if (!StringUtils.isEmpty(thumbnailBase64)) {
      String fileUrl = amazonClient.uploadFile(thumbnailBase64, "thumbnail-team-" + teamName);
      if (fileUrl != null) {
        thumbnail = fileUrl;
      }
    }

    Team toCreate = new Team(privacy, teamName, district, province, new Date(), thumbnail);

    toCreate = teamRepository.insert(toCreate, ownerId);
    cacheClient.setTeamMemberType(toCreate.getId(), ownerId, TeamMemberType.OWNER);

    addTeamToCache(toCreate.getId(), ownerId);

    return toCreate;
  }

  public void deleteTeam(Long teamId) throws Exception {
    Team toDelete = teamRepository.findTeamById(teamId);

    if (toDelete == null) {
      throw new DataRetrievalFailureException("Team not found");
    }

    if (!teamRepository.delete(toDelete)) {
      throw new Exception("Can't delete team");
    }
    List<TeamMember> toRemove = teamMemberRepository.getAllMemberOfTeam(teamId);

    toRemove.forEach((teamMember -> {
      removeTeamFromCache(teamId, teamMember.getUserId());
      teamMemberRepository.delete(teamMember);
    }));
  }

  public Team getTeamById(Long teamId) {
    Team toGet = teamRepository.findTeamById(teamId);

    if (toGet == null) {
      throw new CodeException(ErrorCode.TEAM_NOT_FOUND);
    }

    return toGet;
  }

  public List<Team> getTeamByUser(long userId) {
    return teamRepository.getTeamsByUserReturnTeam(userId);
  }

  public TeamMember getTeamMemberById(long teamId, long userId) {
    TeamMember teamMember = teamMemberRepository.findById(teamId, userId);
    if (teamMember == null) {
      throw new CodeException(ErrorCode.TEAM_USER_NOT_FOUND);
    }
    return teamMember;
  }

  private void addTeamToCache(Long teamId, Long userId) {
    User current = userService.loadUser(userId);
    if (current.getTeams() == null) {
      current.setTeams(new HashSet<>());
    }

    current.getTeams().add(teamId);

    cacheClient.setUser(current);
  }

  private void removeTeamFromCache(Long teamId, Long userId) {
    User current = userService.loadUser(userId);
    Set<Long> currentTeam = current.getTeams();
    currentTeam.remove(teamId);
    current.setTeams(currentTeam);
    cacheClient.setUser(current);
  }

  public boolean requestToJoinTeam(Long requestId, Long teamId) {
    return teamRepository.joinTeam(requestId, teamId);
  }

  public boolean updateTeamRole(Long teamId, Long memberId, TeamMemberType toChangeInto) {
    if (toChangeInto == null || toChangeInto == TeamMemberType.OWNER) {
      return false;
    }

    boolean updated = teamRepository.updateTeamMemberType(teamId, memberId, toChangeInto);

    if (updated) {
      if (toChangeInto == TeamMemberType.BLOCKED) {
        teamRepository.changeTotalMember(teamId, -1);
        removeTeamFromCache(teamId, memberId);
      }

      if (toChangeInto == TeamMemberType.MEMBER) {
        teamRepository.changeTotalMember(teamId, 1);
        addTeamToCache(teamId, memberId);
      }

      cacheClient.setTeamMemberType(teamId, memberId, toChangeInto);
    }
    return updated;
  }

  public boolean cancelJoinTeam(Long requestId, Long teamId) {
    return teamRepository.cancelJoinTeam(requestId, teamId);
  }

  public TeamMemberType loadTeamMemberType(Long teamId, Long userId) {
    TeamMemberType teamMemberType = cacheClient.getTeamMemberType(teamId, userId);
    if (teamMemberType == null) {
      TeamMember teamMember = teamMemberRepository.findById(teamId, userId);
      if (teamMember == null) {
        String msg = String.format("User %s not belong to Team %s", userId, teamId);
        LOGGER.warn(msg);
        throw new CodeException(msg, ErrorCode.TEAM_USER_NOT_FOUND);
      }
      teamMemberType = teamMember.getTeamMemberType();
    }
    return teamMemberType;
  }

  public Team updateTeam(Long teamId, String teamName, String thumbnail, String banner, int privacy,
      String district, String province, String description) {
    Team toUpdate = teamRepository.findTeamById(teamId);

    if (toUpdate == null) {
      throw new CodeException(ErrorCode.TEAM_NOT_FOUND);
    }

    if (teamName != null) {
      toUpdate.setTeamName(teamName);
    }
    if (thumbnail != null) {
      String thumbnailURL = amazonClient.uploadFile(thumbnail, "thumbnail-team-" + teamName);
      toUpdate.setThumbnail(thumbnailURL);
    }
    if (banner != null) {
      String bannerURL = amazonClient.uploadFile(banner, "banner-team-" + teamName);
      toUpdate.setBanner(bannerURL);
    }
    if (privacy != toUpdate.getPrivacy()) {
      toUpdate.setPrivacy(privacy);
    }
    if (province != null) {
      toUpdate.setProvince(province);
    }
    if (district != null) {
      toUpdate.setDistrict(district);
    }
    if (description != null) {
      toUpdate.setDescription(description);
    }

    teamRepository.update(toUpdate);
    LOGGER.info("Update team {}", teamId);

    return toUpdate;
  }

  public Set<Team> getTeamSuggestion(Long currentUserId, String district, String province,
      int howMany) {
    Set<Team> toReturn;
    Set<Long> toExclude = userService.loadUser(currentUserId).getTeams();
    toReturn = teamRepository
        .getTeamSuggestionByUserLocation(district, province, howMany, toExclude);
    return toReturn;
  }

  public Set<Team> findTeamWithNameContains(String searchString, int offset, int count) {
    Set<Team> toGet = teamRepository.findTeamWithNameContains(searchString, offset, count);

    if (toGet == null) {
      throw new DataRetrievalFailureException("Team not found");
    }

    return toGet;
  }

  public Set<User> getAllTeamMemberPaged(Long teamId, int offset, int count) {
    Set<User> toReturn = new HashSet<>();

    List<TeamMember> teamMembers = teamMemberRepository
        .getAllMemberOfTeamPaged(teamId, offset, count);

    teamMembers.forEach(teamMember -> {
      toReturn.add(userService.loadUser(teamMember.getUserId()));
    });

    return toReturn;
  }

  public List<UserLeaderBoardInfo> getLeaderBoard(long teamId, int limit) {
    List<LeaderBoardTeamDTO> leaderBoard = teamRepository.getLeaderBoard(teamId);
    List<Long> userIds = leaderBoard.stream()
        .map(LeaderBoardTeamDTO::getUserId)
        .limit(limit)
        .collect(Collectors.toList());
    Map<Long, UserLeaderBoardDTO> mapUsers = new HashMap<>();
    List<UserLeaderBoardDTO> users = userRepository
        .getUserLeaderBoard(userIds);
    users.forEach(user -> mapUsers.put(user.getUserId(), user));
    return leaderBoard.stream()
        .map(user -> new UserLeaderBoardInfo(mapUsers.get(user.getUserId()), user.getTotal()))
        .collect(
            Collectors.toList());
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

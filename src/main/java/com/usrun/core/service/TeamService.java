package com.usrun.core.service;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.LeaderBoardTeamDTO;
import com.usrun.core.payload.dto.TeamDTO;
import com.usrun.core.payload.dto.TeamLeaderBoardDTO;
import com.usrun.core.payload.dto.TeamStatDTO;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.payload.dto.UserFilterWithTypeDTO;
import com.usrun.core.payload.dto.UserLeaderBoardDTO;
import com.usrun.core.payload.dto.UserLeaderBoardInfo;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

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
    private UserActivityRepository acticityRepository;

    @Autowired
    private AppProperties appProperties;

    public Team createTeam(
            Long ownerId, int privacy, String teamName, Integer province,
            String thumbnailBase64) {

        String encodedName = Base64.getEncoder()
                .encodeToString(teamName.getBytes(StandardCharsets.UTF_8));
        String thumbnail = appProperties.getDefaultThumbnailTeam();
        String banner = appProperties.getDefaultBannerTeam();
        if (thumbnailBase64 != null && thumbnailBase64.length() > appProperties.getMaxImageSize()) {
            throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
        }
        if (!StringUtils.isEmpty(thumbnailBase64)) {
            String fileUrl = amazonClient.uploadFile(thumbnailBase64,
                    "thumbnail-team-" + encodedName + System.currentTimeMillis());
            if (fileUrl != null) {
                thumbnail = fileUrl;
            }
        }

        Team toCreate = new Team(privacy, teamName, province, new Date(), thumbnail, banner);

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

    public List<TeamDTO> getTeamDTOByUserAndNotEqualTeamMemberType(long userId, TeamMemberType teamMemberType) {
      return teamRepository.getTeamsByUserAndNotEqualTeamMemberTypeReturnTeam(userId, teamMemberType);
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
                log.warn(msg);
                throw new CodeException(msg, ErrorCode.TEAM_USER_NOT_FOUND);
            }
            teamMemberType = teamMember.getTeamMemberType();
        }
        return teamMemberType;
    }

    public Team updateTeam(Long teamId, String thumbnail, String banner, int privacy,
            Integer province, String description) {
        Team toUpdate = teamRepository.findTeamById(teamId);

        if (toUpdate == null) {
            throw new CodeException(ErrorCode.TEAM_NOT_FOUND);
        }

        String encodedName = Base64.getEncoder()
                .encodeToString(toUpdate.getTeamName().getBytes(StandardCharsets.UTF_8));

        if ((thumbnail != null && thumbnail.length() > appProperties.getMaxImageSize())
                || (banner != null && banner.length() > appProperties.getMaxImageSize())) {
            throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
        }

        if (thumbnail != null) {
            String thumbnailURL = amazonClient
                    .uploadFile(thumbnail, "thumbnail-team-" + encodedName + System.currentTimeMillis());
            if (thumbnailURL != null) {
                amazonClient.deleteFile(toUpdate.getThumbnail());
                toUpdate.setThumbnail(thumbnailURL);
            }
        }
        if (banner != null) {
            String bannerURL = amazonClient
                    .uploadFile(banner, "banner-team-" + encodedName + System.currentTimeMillis());
            if (bannerURL != null) {
                amazonClient.deleteFile(toUpdate.getBanner());
                toUpdate.setBanner(bannerURL);
            }
        }
        if (privacy != toUpdate.getPrivacy()) {
            toUpdate.setPrivacy(privacy);
        }
        if (province != null && province >= 1 && province <= 63) {
            toUpdate.setProvince(province);
        }

        if (description != null) {
            toUpdate.setDescription(description);
        }

        teamRepository.update(toUpdate);
        log.info("Update team {}", teamId);

        return toUpdate;
    }

    public Set<Team> getTeamSuggestion(Long currentUserId, int province, int count) {
        Set<Team> toReturn;
        Set<Long> toExclude = userService.loadUser(currentUserId).getTeams();
        toReturn = teamRepository
                .getTeamSuggestionByUserLocation(province, count, toExclude);
        return toReturn;
    }

    public Set<Team> findTeamWithNameContains(String searchString, int offset, int count) {
        Set<Team> toGet = teamRepository.findTeamWithNameContains(searchString, offset, count);

        if (toGet == null) {
            throw new DataRetrievalFailureException("Team not found");
        }

        return toGet;
    }

    public void buildTeamLeaderBoard() {
        logger.info("start build teamLeaderBoard");
        List<Team> teams = teamRepository.findAllTeam();
        List<Long> listTeamId = teams.stream()
                .map(team -> team.getId())
                .collect(Collectors.toList());
        List<TeamLeaderBoardDTO> teamLeaderBoardDTO = new ArrayList<>();
        listTeamId.forEach(teamId -> {
            Team team = teamRepository.findTeamById(teamId);
            List<Long> ids = teamMemberRepository.getAllIdMemberOfTeam(teamId);
            List<UserActivity> activities = acticityRepository.findByUserIds(ids);
            int numberActivity = activities.size();
            long totalDistance = 0l;
            long maxTotalTime = 0l;
            long maxDistance = 0l;
            for (UserActivity activity : activities) {
                totalDistance += activity.getTotalDistance();
                maxTotalTime = Math.max(maxTotalTime, activity.getTotalTime());
                maxDistance = Math.max(maxDistance, activity.getTotalDistance());
            }
            List<TeamMember> newTeamMemberInWeek = teamMemberRepository.getNewMemberInWeek(teamId);
            int newMember = newTeamMemberInWeek.size();
            TeamStatDTO teamStat = new TeamStatDTO(totalDistance, maxTotalTime, maxDistance, newMember, ids.size(), numberActivity);
            cacheClient.setTeamStat(teamId, teamStat);
            TeamLeaderBoardDTO itemLeaderBoard = new TeamLeaderBoardDTO();
            itemLeaderBoard.distance = totalDistance;
            itemLeaderBoard.teamId = teamId;
            itemLeaderBoard.name = team.getTeamName();
            itemLeaderBoard.avatar = team.getThumbnail();
            teamLeaderBoardDTO.add(itemLeaderBoard);
            List<TeamLeaderBoardDTO> sortedTeamLeaderBoard = teamLeaderBoardDTO
                    .stream()
                    .sorted(Comparator.comparingLong(TeamLeaderBoardDTO::getDistance).reversed())
                    .collect(Collectors.toList());
            cacheClient.setTeamLeaderBoard(sortedTeamLeaderBoard);
        });
        logger.info("finish build teamLeaderBoard");
    }

    public List<UserFilterWithTypeDTO> getAllTeamMemberPaged(Long teamId, int offset, int limit) {
        return userRepository
                .getAllMemberByLessEqualTeamMemberType(teamId, TeamMemberType.MEMBER, offset, limit);
    }

    public List<UserFilterWithTypeDTO> findTeamMember(String keyword, long teamId, int offset, int limit) {
        return userRepository
                .findUserIsEnable(keyword, teamId, offset, limit);
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

    public List<UserFilterDTO> getUserByMemberType(long teamId, TeamMemberType teamMemberType,
            int offset, int limit) {
        return userRepository.getUserByMemberType(teamId, teamMemberType, offset, limit);
    }

}

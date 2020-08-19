package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.TeamStatResponse;
import com.usrun.core.payload.dto.*;
import com.usrun.core.payload.team.*;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TeamService;
import com.usrun.core.utility.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

  @Autowired
  private TeamService teamService;

  @Autowired
  private CacheClient cacheClient;

  @PostMapping("/update")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'OWNER',#updateTeamRequest.getTeamId())")
  public ResponseEntity<?> updateTeam(
      @RequestBody UpdateTeamRequest updateTeamRequest
  ) {
    Team updated;
    try {
      updated = teamService.updateTeam(
          updateTeamRequest.getTeamId(),
          updateTeamRequest.getThumbnail(),
          updateTeamRequest.getBanner(),
          updateTeamRequest.getPrivacy(),
          updateTeamRequest.getProvince(),
          updateTeamRequest.getDescription());
      return new ResponseEntity<>(new CodeResponse(updated), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/join")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> joinTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody JoinTeamRequest joinTeamRequest) {
    try {
      teamService.requestToJoinTeam(userPrincipal.getId(), joinTeamRequest.getTeamId());
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/accept")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> acceptTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody JoinTeamRequest request) {
    try {
      teamService.requestToAcceptTeam(userPrincipal.getId(), request.getTeamId());
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/invite")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'MEMBER',#request.getTeamId())")
  public ResponseEntity<?> inviteToTeam(
      @RequestBody InviteTeamRequest request
  ) {
    try {
      String emailOrUserCode = request.getEmailOrUserCode();
      long teamId = request.getTeamId();
      if (StringUtils.isBlank(emailOrUserCode) || teamId <= 0) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.INVALID_PARAM));
      }
      teamService.inviteToTeam(emailOrUserCode, teamId);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }

  @PostMapping("/cancelJoin")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'PENDING',#joinTeamRequest.getTeamId())")
  public ResponseEntity<?> cancelJoinTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody JoinTeamRequest joinTeamRequest) {
    try {
      teamService.cancelJoinTeam(userPrincipal.getId(), joinTeamRequest.getTeamId());
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/changeMemberType")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'ADMIN',#updateMemberRequest.getTeamId())")
  public ResponseEntity<?> changeMemberType(
      @RequestBody UpdateMemberRequest updateMemberRequest) {
    try {
      boolean updateRole = teamService.updateTeamRole(
          updateMemberRequest.getTeamId(),
          updateMemberRequest.getMemberId(),
          TeamMemberType.fromInt(updateMemberRequest.getMemberType()));
      if (!updateRole) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_UPDATE_ROLE_FAILED));
      }
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getTeamById")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamById(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody GetTeamByIdRequest getTeamRequest
  ) {
    try {
      long userId = userPrincipal.getId();
      long teamId = getTeamRequest.getTeamId();
      Team team = teamService.getTeamById(teamId);
      TeamMember teamMember = null;
      try {
        teamMember = teamService.getTeamMemberById(teamId, userId);
      } catch (CodeException ex) {
        teamMember = new TeamMember();
        teamMember.setTeamMemberType(TeamMemberType.GUEST);
      }

      TeamDTO teamDTO = new TeamDTO(team, teamMember);
      return new ResponseEntity<>(new CodeResponse(teamDTO), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }

  @PostMapping("/getTeamSuggestion")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamSuggestion(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody SuggestTeamRequest request
  ) {
    try {
       int count = request.getCount() <= 0 ? 10 :request.getCount();
      int province
          = request.getProvince() >= 1 && request.getProvince() <= 63 ? request.getProvince() : 0;
      Set<Team> teams = teamService.getTeamSuggestion(
          userPrincipal.getId(),
          province,
          count);

      return new ResponseEntity<>(new CodeResponse(teams), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }

  @PostMapping("/findTeam")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findTeamWithNameContains(
      @RequestBody FindTeamRequest findTeamRequest
  ) {
    try {
      int offset
          = findTeamRequest.getPageNum() > 0 ? findTeamRequest.getPageNum() - 1 : 0;
      int count
          = findTeamRequest.getPerPage() > 0 ? findTeamRequest.getPerPage() : 10;
      Set<Team> toGet = teamService
          .findTeamWithNameContains(findTeamRequest.getTeamName(), offset, count);
      return new ResponseEntity<>(new CodeResponse(toGet), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getAllTeamMember")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'MEMBER',#getAllTeamMemberRequest.getTeamId())")
  public ResponseEntity<?> getAllTeamMember(
      @RequestBody GetAllTeamMemberRequest getAllTeamMemberRequest
  ) {
    try {
      int offset
          = getAllTeamMemberRequest.getPageNum() > 0 ? getAllTeamMemberRequest.getPageNum() - 1 : 0;
      int count
          = getAllTeamMemberRequest.getPerPage() > 0 ? getAllTeamMemberRequest.getPerPage() : 10;
      List<UserFilterWithTypeDTO> toGet = teamService
          .getAllTeamMemberPaged(getAllTeamMemberRequest.getTeamId(), offset, count);
      return new ResponseEntity<>(new CodeResponse(toGet), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getTeamByUser")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamByUser(@RequestBody(required=false)  GetTeamByUserRequest request, @CurrentUser UserPrincipal userPrincipal) {
    try {
      long userId = userPrincipal.getId();

      if(request != null){
        userId = request.getUserId();
      }

      List<TeamDTO> teams = teamService
          .getTeamDTOByUserAndNotEqualTeamMemberType(userId, TeamMemberType.BLOCKED);
      return ResponseEntity.ok(new CodeResponse(teams));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getLeaderBoard")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getLeaderBoard(@RequestBody GetLeaderBoardRequest request) {
    try {
      long teamId = request.getTeamId();
      List<UserLeaderBoardInfo> leaderBoards = teamService.getLeaderBoard(teamId, 100);
      return ResponseEntity.ok(new CodeResponse(leaderBoards));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getUserByMemberType")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'MEMBER',#request.getTeamId())")
  public ResponseEntity<?> getUserByMemberType(@RequestBody GetUserByMemberTypeRequest request) {
    try {
      TeamMemberType memberType = TeamMemberType.fromInt(request.getMemberType());
      if (memberType == null) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_MEMBER_TYPE_NOT_FOUND));
      }

      long teamId = request.getTeamId();
      int offset = Math.max(0, request.getPage() - 1);
      int limit = request.getCount() == 0 ? 10 : request.getCount();

      List<UserFilterDTO> users = teamService
          .getUserByMemberType(teamId, memberType, offset, limit);

      return ResponseEntity.ok(new CodeResponse(users));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/findUser")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'MEMBER',#request.getTeamId())")
  public ResponseEntity<?> findUser(@RequestBody FindTeamMemberRequest request) {
    try {
      int count = request.getCount() > 0 ? request.getCount() : 10;
      int offset = Math.max(0, request.getOffset() - 1);
      long teamId = request.getTeamId();
      String keyword = '%' + request.getKeyword() + '%';
      List<UserFilterWithTypeDTO> users = teamService
          .findTeamMember(keyword, teamId, offset, count);
      return ResponseEntity.ok(new CodeResponse(users));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getTeamStat")
  public ResponseEntity<?> getTeamStat(@RequestBody GetLeaderBoardRequest request) {
    try {
      long teamId = request.getTeamId();
      if (teamId < 0) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_NOT_FOUND));
      }

      List<TeamStatDTO> teamStat = cacheClient.getTeamStat();

      if (teamStat == null) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
      }
      AtomicInteger rank = new AtomicInteger(-1);

      TeamStatDTO teamStatValue = teamStat.stream()
          .peek(i -> rank.incrementAndGet())
          .filter(item -> item.getTeamId() == teamId)
          .findFirst()
          .get();

      TeamStatResponse resp = new TeamStatResponse(rank.get(), teamStatValue.getTotalDistance(),
          teamStatValue.getMaxTime(), teamStatValue.getMaxDistance(), teamStatValue.getMemInWeek(),
          teamStatValue.getTotalMember(), teamStatValue.getTotalActivity());
      return ResponseEntity.ok(new CodeResponse(resp));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getTeamLeaderBoard")
  public ResponseEntity<?> getTeamLeaderBoard(@RequestBody GetLeaderBoardRequest request) {
    try {
      long teamId = request.getTeamId();
      if (teamId < 0) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_NOT_FOUND));
      }

      List<TeamStatDTO> teamList = cacheClient.getTeamStat();
      List<TeamLeaderBoardResp> teamLeaderBoardResp = new ArrayList<>();
      if (teamList == null || teamList.size() <= 0) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
      }
      List<TeamStatDTO> top10 = teamList.subList(0, 10);
      for (int i = 0; i < top10.size(); i++) {
        TeamLeaderBoardResp itemResp = new TeamLeaderBoardResp();
        itemResp.avatar = top10.get(i).getAvatar();
        itemResp.name = top10.get(i).getTeamName();
        itemResp.teamId = top10.get(i).getTeamId();
        itemResp.distance = top10.get(i).getTotalDistance();
        itemResp.rank = i + 1;
        teamLeaderBoardResp.add(itemResp);
      }

      int myTeamPosition = teamList
          .stream()
          .map(item -> item.getTeamId())
          .collect(Collectors.toList())
          .indexOf(teamId);
      if (myTeamPosition >= 10) {
        TeamLeaderBoardResp itemResp = new TeamLeaderBoardResp();
        itemResp.rank = myTeamPosition + 1;
        itemResp.avatar = teamList.get(myTeamPosition).getAvatar();
        itemResp.name = teamList.get(myTeamPosition).getTeamName();
        itemResp.teamId = teamList.get(myTeamPosition).getTeamId();
        itemResp.distance = teamList.get(myTeamPosition).getTotalDistance();
        teamLeaderBoardResp.add(itemResp);
      }

      return ResponseEntity.ok(new CodeResponse(teamLeaderBoardResp));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }
  @PostMapping("/getTeamByEvent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTeamOfEvent(@RequestBody GetTeamOfEventReq request) {
        try {
            int count = request.getLimit()> 0 ? request.getLimit() : 10;
            int offset = request.getOffset();
            long eventId =  request.getEventId();
            List<Team> teams = teamService.getTeamByEvent(eventId, count, offset);
            return ResponseEntity.ok(new CodeResponse(teams));
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
                    HttpStatus.OK);
        } catch (Exception ex) {
            log.error("", ex);
            return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
                    HttpStatus.OK);
        }
    }
    
    @PostMapping("/searchTeamByEvent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> searchTeamOfEvent(@RequestBody SearchTeamOfEventReq request) {
        try {
            int count = request.getLimit()> 0 ? request.getLimit() : 10;
            int offset = request.getOffset();
            long eventId =  request.getEventId();
            String keyword = request.getName();
            List<Team> teams = teamService.searchTeamByEvent(eventId,keyword, count, offset);
            return ResponseEntity.ok(new CodeResponse(teams));
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
                    HttpStatus.OK);
        } catch (Exception ex) {
            log.error("", ex);
            return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
                    HttpStatus.OK);
        }
    }
}

package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.dto.TeamDTO;
import com.usrun.core.payload.dto.TeamSummaryDTO;
import com.usrun.core.payload.dto.UserLeaderBoardInfo;
import com.usrun.core.payload.team.CreateTeamRequest;
import com.usrun.core.payload.team.FindTeamRequest;
import com.usrun.core.payload.team.GetAllTeamMemberRequest;
import com.usrun.core.payload.team.GetLeaderBoardRequest;
import com.usrun.core.payload.team.GetTeamByIdRequest;
import com.usrun.core.payload.team.JoinTeamRequest;
import com.usrun.core.payload.team.SuggestTeamRequest;
import com.usrun.core.payload.team.UpdateMemberRequest;
import com.usrun.core.payload.team.UpdateTeamRequest;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TeamService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

  @Autowired
  private TeamService teamService;

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody CreateTeamRequest createTeamRequest
  ) {
    try {
      Team team = teamService.createTeam(
          userPrincipal.getId(),
          createTeamRequest.getPrivacy(),
          createTeamRequest.getTeamName(),
          createTeamRequest.getDistrict(),
          createTeamRequest.getProvince(),
          createTeamRequest.getThumbnailBase64());
      return new ResponseEntity<>(new CodeResponse(team), HttpStatus.CREATED);
    } catch (DuplicateKeyException e) {
      return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_EXISTED), HttpStatus.BAD_REQUEST);
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/update")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'OWNER',#updateTeamRequest.getTeamId())")
  public ResponseEntity<?> updateTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody UpdateTeamRequest updateTeamRequest
  ) {
    Team updated;
    try {
      updated = teamService.updateTeam(
          updateTeamRequest.getTeamId(),
          updateTeamRequest.getTeamName(),
          updateTeamRequest.getThumbnail(),
          updateTeamRequest.getBanner(),
          updateTeamRequest.getPrivacy(),
          updateTeamRequest.getDistrict(),
          updateTeamRequest.getProvince(),
          updateTeamRequest.getDescription());
      return new ResponseEntity<>(new CodeResponse(updated), HttpStatus.OK);
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
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
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/cancelJoin")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'PENDING',#joinTeamRequest.getTeamId())")
  public ResponseEntity<?> cancelJoinTeam(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody JoinTeamRequest joinTeamRequest) {
    try {
      boolean deleted = teamService
          .cancelJoinTeam(userPrincipal.getId(), joinTeamRequest.getTeamId());
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
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
        return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_UPDATE_ROLE_FAILED),
            HttpStatus.BAD_REQUEST);
      }
      return ResponseEntity.ok(new CodeResponse(0));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
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
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @PostMapping("/getTeamSuggestion")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamSuggestion(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody SuggestTeamRequest suggestTeamRequest
  ) {
    try {
      Set<Team> toGet = teamService.getTeamSuggestion(
          userPrincipal.getId(),
          suggestTeamRequest.getDistrict(),
          suggestTeamRequest.getProvince(),
          suggestTeamRequest.getHowMany());

      Set<TeamSummaryDTO> summaries = toGet.stream().map(TeamSummaryDTO::new).collect(
          Collectors.toSet());
      return new ResponseEntity<>(new CodeResponse(summaries), HttpStatus.OK);
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @PostMapping("/findTeam")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findTeamWithNameContains(
      @RequestBody FindTeamRequest findTeamRequest
  ) {
    try {
      int offset =
          findTeamRequest.getPageNum() > 0 ? findTeamRequest.getPageNum() - 1 : 0;
      int count =
          findTeamRequest.getPerPage() > 0 ? findTeamRequest.getPerPage() : 10;
      Set<Team> toGet = teamService
          .findTeamWithNameContains(findTeamRequest.getTeamName(), offset, count);
      return new ResponseEntity<>(new CodeResponse(toGet), HttpStatus.OK);
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getAllTeamMember")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'MEMBER',#getAllTeamMemberRequest.getTeamId())")
  public ResponseEntity<?> getAllTeamMember(
      @RequestBody GetAllTeamMemberRequest getAllTeamMemberRequest
  ) {
    try {
      int offset =
          getAllTeamMemberRequest.getPageNum() > 0 ? getAllTeamMemberRequest.getPageNum() - 1 : 0;
      int count =
          getAllTeamMemberRequest.getPerPage() > 0 ? getAllTeamMemberRequest.getPerPage() : 10;
      Set<User> toGet = teamService
          .getAllTeamMemberPaged(getAllTeamMemberRequest.getTeamId(), offset, count);
      return new ResponseEntity<>(new CodeResponse(toGet), HttpStatus.OK);
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getTeamByUser")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamByUser(@CurrentUser UserPrincipal userPrincipal) {
    try {
      long userId = userPrincipal.getId();
      List<Team> teams = teamService.getTeamByUser(userId);
      return ResponseEntity.ok(new CodeResponse(teams));
    } catch (CodeException ex) {
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
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
      return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()),
          HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      log.error("", ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.SYSTEM_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

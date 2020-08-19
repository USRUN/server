package com.usrun.core.controller.admin;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.team.AdminGetAllTeamRequest;
import com.usrun.core.payload.team.CreateTeamRequest;
import com.usrun.core.service.TeamService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author phuctt4
 */

@Slf4j
@RestController
@RequestMapping("/admin/teams")
public class AdminTeamController {

  @Autowired
  private TeamService teamService;

  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createTeam(
      @RequestBody CreateTeamRequest request
  ) {
    try {
      Team team = teamService.createTeam(
          request.getOwnerId(),
          request.getPrivacy(),
          request.getTeamName(),
          request.getProvince(),
          request.getThumbnail());
      return ResponseEntity.ok(new CodeResponse(team));
    } catch (DuplicateKeyException e) {
      return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_EXISTED));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getAll")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAll(@RequestBody AdminGetAllTeamRequest request) {
    try {
      int offset = Math.max(request.getOffset() - 1, 0);
      int limit = request.getLimit() <= 0 ? 10 : request.getLimit();
      String teamName = request.getTeamName() == null ? "" : request.getTeamName();
      List<Team> teams = teamService.getAll(teamName, offset, limit);
      return ResponseEntity.ok(new CodeResponse(teams));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      log.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }
}

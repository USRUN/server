package com.usrun.core.security.impl;

import com.usrun.core.exception.CodeException;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.security.TeamAuthorization;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author phuctt4
 */

@Service("teamAuthorization")
public class TeamAuthorizationImpl implements TeamAuthorization {

  public static final Logger LOGGER = LoggerFactory.getLogger(TeamAuthorizationImpl.class);

  @Autowired
  private TeamService teamService;

  @Override
  public boolean authorize(Authentication authentication, String roleTeam, long teamId) {
    TeamMemberType requestTeamRole = TeamMemberType.fromString(roleTeam);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    long userId = userPrincipal.getId();
    TeamMemberType userTeamRole = null;
    try {
      userTeamRole = teamService.loadTeamMemberType(teamId, userId);
      return requestTeamRole.compareTo(userTeamRole) >= 0;
    } catch (CodeException ex) {
      return false;
    }
  }
}

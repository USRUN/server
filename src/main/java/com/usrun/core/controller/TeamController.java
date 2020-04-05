package com.usrun.core.controller;

import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Team;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.*;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
                    createTeamRequest.getTeamName(),
                    createTeamRequest.getThumbnail(),
                    createTeamRequest.getPrivacy(),
                    createTeamRequest.getLocation(),
                    createTeamRequest.getDescription());
            return new ResponseEntity<>(new CodeResponse(team), HttpStatus.CREATED);
        } catch (CodeException e) {
            return new ResponseEntity<>(new CodeResponse(e.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }
//
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER') && teamAuthorization.authorize(authentication,'OWNER',#teamInfoRequest.getTeamId())")
//    public ResponseEntity<?> updateTeam(
//            @CurrentUser UserPrincipal userPrincipal,
//            @RequestBody UpdateTeamRequest updateTeamRequest
//    ) {
//        try {
//            Team team = teamService.updateTeam(
//                    updateTeamRequest.getTeamId(),
//                    updateTeamRequest.getTeamName(),
//                    updateTeamRequest.getThumbnail(),
//                    updateTeamRequest.getPrivacy(),
//                    updateTeamRequest.getLocation(),
//                    updateTeamRequest.getDescription());
//            return new ResponseEntity<>(new CodeResponse(team), HttpStatus.OK);
//        } catch (CodeException e) {
//            return new ResponseEntity<>(new CodeResponse(e.getErrorCode()), HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> joinTeam(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JoinTeamRequest joinTeamRequest){
        teamService.requestToJoinTeam(userPrincipal.getId(),joinTeamRequest.getTeamId());
        return  ResponseEntity.ok(new CodeResponse(200));
    }

    @PostMapping("/cancelJoin")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'PENDING',#joinTeamRequest.getTeamId())")
    public ResponseEntity<?> cancelJoinTeam(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JoinTeamRequest joinTeamRequest){
        teamService.cancelJoinTeam(userPrincipal.getId(),joinTeamRequest.getTeamId());
        return ResponseEntity.ok(new CodeResponse(200));
    }

    @PostMapping("/changeMemberType")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'ADMIN',#updateMemberRequest.getTeamId())")
    public ResponseEntity<?> changeMemberType(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UpdateMemberRequest updateMemberRequest){
        teamService.updateTeamRole(
                updateMemberRequest.getTeamId(),
                updateMemberRequest.getMemberId(),
                TeamMemberType.fromInt(updateMemberRequest.getMemberType()));
        return ResponseEntity.ok(new CodeResponse(200));
    }
}

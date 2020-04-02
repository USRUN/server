package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.model.Team;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.*;
import com.usrun.core.payload.team.*;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
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
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_EXISTED), HttpStatus.BAD_REQUEST);
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
                    updateTeamRequest.getPrivacy(),
                    updateTeamRequest.getLocation(),
                    updateTeamRequest.getDescription());
        } catch (DataRetrievalFailureException e) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new CodeResponse(updated), HttpStatus.OK);
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> joinTeam(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JoinTeamRequest joinTeamRequest){
        try{
        teamService.requestToJoinTeam(userPrincipal.getId(),joinTeamRequest.getTeamId());
        }
        catch (DataRetrievalFailureException e){
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(new CodeResponse(200));
    }

    @PostMapping("/cancelJoin")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'PENDING',#joinTeamRequest.getTeamId())")
    public ResponseEntity<?> cancelJoinTeam(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JoinTeamRequest joinTeamRequest){
        try{
            teamService.cancelJoinTeam(userPrincipal.getId(),joinTeamRequest.getTeamId());
        }
        catch (DataRetrievalFailureException e){
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(new CodeResponse(200));
    }

    @PostMapping("/changeMemberType")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication,'ADMIN',#updateMemberRequest.getTeamId())")
    public ResponseEntity<?> changeMemberType(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UpdateMemberRequest updateMemberRequest){
        try{
            teamService.updateTeamRole(
                    updateMemberRequest.getTeamId(),
                    updateMemberRequest.getMemberId(),
                    TeamMemberType.fromInt(updateMemberRequest.getMemberType()));
        }
        catch (DataRetrievalFailureException e){
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(new CodeResponse(200));
    }

    @PostMapping("/getTeamById")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTeamById(
            @RequestBody GetTeamOfUserRequest getTeamRequest
    ){
        Team toGet = null;
        try {
            toGet = teamService.getTeamById(getTeamRequest.getTeamId());
        } catch (DataRetrievalFailureException e){
            return new ResponseEntity<>(new CodeResponse(ErrorCode.TEAM_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new CodeResponse(toGet),HttpStatus.OK);
    }
}

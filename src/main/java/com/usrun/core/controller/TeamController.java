//package com.usrun.core.controller;
//
//import com.usrun.core.exception.CodeException;
//import com.usrun.core.model.Team;
//import com.usrun.core.payload.CodeResponse;
//import com.usrun.core.payload.ImageTeamRequest;
//import com.usrun.core.security.CurrentUser;
//import com.usrun.core.security.UserPrincipal;
//import com.usrun.core.service.TeamService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/team")
//public class TeamController {
//
//    @Autowired
//    private TeamService teamService;
//
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> createTeam(
//            @CurrentUser UserPrincipal userPrincipal,
//            @RequestParam("name") String name,
//            @RequestParam(name = "description", required = false) String description,
//            @RequestParam(name = "leagueId", required = false) Integer leagueId,
//            @RequestBody(required = false) ImageTeamRequest imageTeamRequest
//            ) {
//        try {
//            if(imageTeamRequest == null)
//                imageTeamRequest = new ImageTeamRequest();
//            Team team = teamService.createTeam(
//                    userPrincipal.getId(), name,
//                    imageTeamRequest.getImg(), imageTeamRequest.getLogo(),
//                    leagueId, description);
//            return new ResponseEntity<>(new CodeResponse(team), HttpStatus.CREATED);
//        } catch (CodeException e) {
//            return new ResponseEntity<>(new CodeResponse(e.getErrorCode()), HttpStatus.BAD_REQUEST);
//        }
//    }
//}

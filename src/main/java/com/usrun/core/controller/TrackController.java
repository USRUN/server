package com.usrun.core.controller;

import com.usrun.core.exception.TrackException;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.track.CreateTrackRequest;
import com.usrun.core.payload.TrackRequest;
import com.usrun.core.payload.dto.TrackDTO;
import com.usrun.core.payload.track.GetTrackRequest;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author phuctt4
 */

@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private TrackService trackService;
//
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> createTrack(
//            @CurrentUser UserPrincipal userPrincipal,
//            @RequestBody CreateTrackRequest createTrackRequest
//            ) {
//        Long userId = userPrincipal.getId();
//        Track track = trackService.createTrack(userId, createTrackRequest.getDescription());
//        return ResponseEntity.ok(new CodeResponse(track));
//    }
//
//    @PostMapping("/point")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> track(
//            @CurrentUser UserPrincipal userPrincipal,
//            @RequestBody TrackRequest trackRequest
//            ) {
////        Long userId = userPrincipal.getId();
////
////        List<Point> points = null;
////
////        try {
////            points = trackService.track(userId,
////                    trackRequest.getTrackId(),
////                    trackRequest.getLocations(),
////                    trackRequest.getTime(),
////                    trackRequest.getSig());
////        } catch (TrackException exp) {
////            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
////        }
//        return ResponseEntity.ok(new CodeResponse(0));
//    }

    @PostMapping("/gettrack")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTrack(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody GetTrackRequest request
            ) {
        Long userId = userPrincipal.getId();

        Track track = null;

        try {
            track = trackService.getTrack(userId, request.getTrackId());
        } catch (TrackException exp) {
            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new CodeResponse(track));
    }

}

package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.TrackException;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author phuctt4
 */

@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTrack(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(name = "description", required = false) String description
    ) {
        Long userId = userPrincipal.getId();
        Track track = trackService.createTrack(userId, description);
        return ResponseEntity.ok(track);
    }

    @PostMapping("/point")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> track(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(name = "trackid") Long trackId,
            @RequestParam(name = "lat") Float latitude,
            @RequestParam(name = "long") Float longitude
    ) {
        Long userId = userPrincipal.getId();
        Point point = null;
        try {
            point = trackService.track(userId, trackId, latitude, longitude);
        } catch (TrackException exp) {
            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(point);
    }

}

package com.usrun.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usrun.core.exception.TrackException;
import com.usrun.core.model.track.Location;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.dto.TrackDTO;
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

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author phuctt4
 */

@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTrack(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(name = "description", required = false, defaultValue = "") String description
    ) {
        Long userId = userPrincipal.getId();
        Track track = trackService.createTrack(userId, description);
        return ResponseEntity.ok(new CodeResponse(track));
    }

    @PostMapping("/point")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> track(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(name = "trackid") Long trackId,
            @RequestParam(name = "locations") String locationsInput,
            @RequestParam(name = "time") Long time,
            @RequestParam(name = "sig") String sig
            ) throws JsonProcessingException {
        Long userId = userPrincipal.getId();

        List<Location> locations = objectMapper.readValue(locationsInput, new TypeReference<List<Location>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        List<Point> points = null;
        
        try {
            points = trackService.track(userId, trackId, locations, time, sig);
        } catch (TrackException exp) {
            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new CodeResponse(points));
    }

    @PostMapping("/gettrack")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTrack(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(name = "trackid") Long trackId
    ) {
        Long userId = userPrincipal.getId();

        TrackDTO dto = null;

        try {
            dto = trackService.getTrack(userId, trackId);
        } catch (TrackException exp) {
            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new CodeResponse(dto));
    }

}

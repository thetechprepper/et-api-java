package com.thetechprepper.emcommtools.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.model.position.MaidenheadPosition;
import com.thetechprepper.emcommtools.api.model.position.NullPosition;
import com.thetechprepper.emcommtools.api.service.GpsService;
import com.thetechprepper.emcommtools.api.service.UserService;

@RestController
@RequestMapping("/api/geo")
public class PositionController {

    private static final Logger LOG = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    private GpsService gpsService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/position", produces = "application/json")
    public ResponseEntity<PositionResponseStatus> getPosition() {
        PositionResponseStatus status = gpsService.currentPosition();

        if (!status.getReady()) {
            try {
                MaidenheadPosition fallbackPosition = MaidenheadPosition.newInstance(
                    userService.getUserConfig().getGrid()
                );
                LOG.info("User: {}", userService.getUserConfig());
                status.setPosition(fallbackPosition);
                status.setHttpStatus(HttpStatus.OK.value());
            } catch(Exception e) {
                LOG.error("No GPS detected and invalid grid square provided", e);
                status.setPosition(NullPosition.NO_POSITION);
                status.setHttpStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            }
        }

        return new ResponseEntity<PositionResponseStatus>(status, HttpStatus.valueOf(status.getHttpStatus()));
    }
}
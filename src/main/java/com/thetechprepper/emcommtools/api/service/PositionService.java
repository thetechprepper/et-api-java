/*
 * Copyright (c) 2026 The Tech Prepper, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thetechprepper.emcommtools.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.model.position.MaidenheadPosition;
import com.thetechprepper.emcommtools.api.model.position.NullPosition;
import com.thetechprepper.emcommtools.api.model.position.Position;

/**
 * The Position Service is responsible for resolving the current user position.
 * <p>
 * The position is obtained primarily from the GPS subsystem. If GPS data
 * is unavailable or not ready, a fallback position derived from the user's
 * configured Maidenhead grid square is used. If neither source is valid,
 * a null position is returned.
 */
@Service
public class PositionService 
{
    private static final Logger LOG = LoggerFactory.getLogger(GpsService.class);

    @Autowired
    private GpsService gpsService;

    @Autowired
    private UserService userService;

    /**
     * Resolves the current position along with response status information.
     * <p>
     * This method attempts to obtain the current position from the GPS
     * subsystem. If GPS data is unavailable, a fallback position derived
     * from the user's configured Maidenhead grid square is used. The
     * returned object includes both the resolved position and an HTTP
     * status code indicating the outcome.
     *
     * @return a {@link PositionResponseStatus} containing the resolved
     *         position and corresponding HTTP status
     */
    public PositionResponseStatus currentPositionResponseStatus() {

        PositionResponseStatus status = gpsService.currentPosition();

        if (!status.getReady()) {
            try {
                MaidenheadPosition fallbackPosition = MaidenheadPosition.newInstance(
                    userService.getUserConfig().getGrid()
                );
                status.setPosition(fallbackPosition);
                status.setHttpStatus(HttpStatus.OK.value());
            } catch(Exception e) {
                LOG.error("No GPS detected and invalid grid square provided", e);
                status.setPosition(NullPosition.NO_POSITION);
                status.setHttpStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            }
        }

        return status;
    }

    /**
     * Returns the current position of the user.
     * <p>
     * This method first attempts to retrieve a live GPS position. If GPS
     * data is not available, it falls back to a position derived from the
     * user's configured Maidenhead grid square. If no valid position can
     * be determined, a {@link NullPosition} is returned.
     *
     * @return the resolved current {@link Position}, or {@link NullPosition}
     *         if no valid position is available
     */
    public Position currentPosition() {
        return currentPositionResponseStatus().getPosition();
    }
}
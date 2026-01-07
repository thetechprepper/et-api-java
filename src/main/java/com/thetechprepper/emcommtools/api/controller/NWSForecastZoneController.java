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
package com.thetechprepper.emcommtools.api.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thetechprepper.emcommtools.api.model.NWSZoneCounty;
import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.service.PositionService;
import com.thetechprepper.emcommtools.api.service.search.NWSForecastZoneSearchService;
import com.thetechprepper.emcommtools.api.util.CollectionUtils;
import com.thetechprepper.emcommtools.api.util.GeoUtils;

/**
 * REST controller for accessing U.S. National Weather Service (NWS) forecast zones.
 *
 * <p>This controller provides endpoints to retrieve forecast zones based on 
 * either the client’s geolocation or explicit latitude/longitude coordinates.
 * The data is sourced from the U.S. NWS FTPmail gateway service, allowing 
 * clients to discover the correct forecast zone for generating weather requests 
 * or preparing Winlink messages.</p>
 *
 */
@RestController
@RequestMapping("/api/nws")
public class NWSForecastZoneController {
    private static final Logger LOG = LoggerFactory.getLogger(NWSForecastZoneController.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private NWSForecastZoneSearchService nwsForecastZoneSearchService;

    @GetMapping(
        value = "/forecast-zones",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<NWSZoneCounty>> findForecastZonesByLocation(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lon
    )
    {
        // Use position service if no lat/lon specified
        if (null == lat || null == lon) {
            PositionResponseStatus status = positionService.currentPositionResponseStatus();
  	        lat = status.getPosition().getLat();
            lon = status.getPosition().getLon();
        }

        // Reject invalid coordinates 
        if (GeoUtils.isLatLonInvalid(lat, lon)) {
            return ResponseEntity.badRequest().body(List.of());
        }

        List<NWSZoneCounty> zones = nwsForecastZoneSearchService.findNear(lat, lon);

        return zones.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(zones);
    }

    @GetMapping(
        value = "/forecast/raw",
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> fetchRawForecast(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lon
    ) {
        // Use position service if no lat/lon specified
        if (null == lat || null == lon) {
            PositionResponseStatus status = positionService.currentPositionResponseStatus();
  	        lat = status.getPosition().getLat();
            lon = status.getPosition().getLon();
        }

        // Reject invalid coordinates 
        if (GeoUtils.isLatLonInvalid(lat, lon)) {
            return ResponseEntity.badRequest().body("");
        }

	    NWSZoneCounty zone = CollectionUtils.firstOrNull(nwsForecastZoneSearchService.findNear(lat, lon));
        if (null == zone) {
            return ResponseEntity.notFound().build();         
        }

        LOG.info("Requesting {} for lat/lon: {},{}", zone.getUrl(), lat, lon);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(zone.getUrl()))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return (response.statusCode() == 200)
                    ? ResponseEntity.ok(response.body())
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("");

        } catch (IOException | InterruptedException e) {
            LOG.error("Error fetching: {}", zone.getUrl(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("");
        }
    }
}

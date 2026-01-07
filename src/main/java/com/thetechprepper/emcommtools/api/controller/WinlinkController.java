/*
 * Copyright (c) 2025 The Tech Prepper, LLC
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.thetechprepper.emcommtools.api.model.NWSZoneCounty;
import com.thetechprepper.emcommtools.api.model.WinlinkRmsChannel;
import com.thetechprepper.emcommtools.api.model.http.ActionResponse;
import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.service.PositionService;
import com.thetechprepper.emcommtools.api.service.search.NWSForecastZoneSearchService;
import com.thetechprepper.emcommtools.api.service.search.WinlinkSearchService;
import com.thetechprepper.emcommtools.api.service.template.SimpleTemplateEngine;
import com.thetechprepper.emcommtools.api.service.template.TemplateLoader;
import com.thetechprepper.emcommtools.api.util.CollectionUtils;
import com.thetechprepper.emcommtools.api.util.UtcTimestamp;

@RestController
@RequestMapping("/api/winlink")
public class WinlinkController {
    private static final Logger LOG = LoggerFactory.getLogger(WinlinkController.class);

    @Autowired
    private WinlinkSearchService winlinkSearchService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private NWSForecastZoneSearchService nwsForecastZoneSearchService;

    @GetMapping(value = "/near", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WinlinkRmsChannel>> findNearbyChannels(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String band)
    {

        List<WinlinkRmsChannel> channels = new ArrayList<>();

	channels = winlinkSearchService.findNear(lat, lon, band, mode);

        return channels.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(channels);
    }

    @PostMapping(
        value = "/messages/weather/nws/forecast",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ActionResponse> postForecastRequestToFtpMail() {

        List<NWSZoneCounty> zones = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/mailbox/out";
        String bodyTemplate = TemplateLoader.load(
            "templates/winlink/nws-ftpmail-state-forecast.txt"
        );
        String subjectTemplate = TemplateLoader.load(
            "templates/winlink/nws-ftpmail-state-forecast-subject.txt"
        );

        // Resolve current position
        PositionResponseStatus status = positionService.currentPositionResponseStatus();
        zones = nwsForecastZoneSearchService.findNear(
                status.getPosition().getLat(), status.getPosition().getLon()
        );

        NWSZoneCounty zone = CollectionUtils.firstOrNull(zones);

        if (zone == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ActionResponse(404, "No forecast zone found for current location"));
        }

        // Prepare replacement template tokens 
        Map<String, String> vars = Map.of(
                "STATE", zone.getState(),
                "STATE_LOWERCASE", zone.getState().toLowerCase(),
                "COUNTY", zone.getCounty(),
                "NAME", zone.getName(),
                "ZONE", zone.getZone()
        );

        String emailBody = SimpleTemplateEngine.renderStrict(bodyTemplate, vars);
        String emailSubject = SimpleTemplateEngine.renderStrict(subjectTemplate, vars);

        // Prepare request entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("to", "NWS.FTPMail.OPS@noaa.gov");
        body.add("cc", "");
        body.add("subject", emailSubject);
        body.add("body", emailBody);
        body.add("date", UtcTimestamp.now());

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, requestEntity, Void.class);

            HttpStatus statusCode = response.getStatusCode();
            String message;

            if (statusCode.is2xxSuccessful()) {
                message = "Forecast request successfully queued: " + emailSubject;
            } else {
                message = "Forecast request failed with status: " + statusCode.value();
            }

            return ResponseEntity.status(statusCode)
                    .body(new ActionResponse(statusCode.value(), message));

        } catch (Exception e) {
            String errorMsg = "Error posting forecast request: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ActionResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), errorMsg));
        }
    }
}

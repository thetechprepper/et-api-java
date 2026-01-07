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
import com.thetechprepper.emcommtools.api.model.http.ActionResponse;
import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.service.PositionService;
import com.thetechprepper.emcommtools.api.service.search.WeatherForecastZoneService;
import com.thetechprepper.emcommtools.api.service.template.SimpleTemplateEngine;
import com.thetechprepper.emcommtools.api.service.template.TemplateLoader;
import com.thetechprepper.emcommtools.api.util.UtcTimestamp;

@RestController
@RequestMapping("/api/nws")
public class WeatherForecastZoneController {
    private static final Logger LOG = LoggerFactory.getLogger(WeatherForecastZoneController.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private WeatherForecastZoneService weatherForecastZoneService;

    @GetMapping(
        value = "/forecast-zones",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<NWSZoneCounty>> findForecastZonesByLocation(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lon
    )
    {
        List<NWSZoneCounty> zones = new ArrayList<>();

        // Use position service if no lat/lon specified
        if (null == lat || null == lon) {
            PositionResponseStatus status = positionService.currentPositionResponseStatus();
  	        lat = status.getPosition().getLat();
            lon = status.getPosition().getLon();
        }

    	zones = weatherForecastZoneService.findNear(lat, lon);

        return zones.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(zones);
    }

    @PostMapping(
        value = "/winlink/forecast/post",
	    produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ActionResponse> postForecastRequestToFtpMail()
    {
        List<NWSZoneCounty> zones = new ArrayList<>();

	    RestTemplate restTemplate = new RestTemplate();
	
	    String url = "http://localhost:8080/api/mailbox/out";
	    String bodyTemplate = TemplateLoader.load(
            "templates/winlink/nws-ftpmail-state-forecast.txt"
        );
	    String subjectTemplate = TemplateLoader.load(
            "templates/winlink/nws-ftpmail-state-forecast-url.txt"
        );


        PositionResponseStatus status = positionService.currentPositionResponseStatus();
        zones = weatherForecastZoneService.findNear(
  	    status.getPosition().getLat(), status.getPosition().getLon());

	    NWSZoneCounty zone = firstOrNull(zones);

        if (zone != null) {
            Map<String, String> vars = Map.of(
                "STATE", zone.getState().toLowerCase(),
                "ZONE", zone.getZone().toString()
            );

            String emailBody = SimpleTemplateEngine.renderStrict(bodyTemplate, vars);
            String emailSubject = SimpleTemplateEngine.renderStrict(subjectTemplate, vars);

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

            restTemplate.postForEntity(url, requestEntity, Void.class);
	}

        return ResponseEntity
            .accepted()
            .body(new ActionResponse(202, "test"));
    }

    public static <T> T firstOrNull(List<T> list) 
    {
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}

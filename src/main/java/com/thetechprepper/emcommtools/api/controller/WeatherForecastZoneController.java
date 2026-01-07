package com.thetechprepper.emcommtools.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thetechprepper.emcommtools.api.model.NWSZoneCounty;
import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.model.position.MaidenheadPosition;
import com.thetechprepper.emcommtools.api.model.position.NullPosition;
import com.thetechprepper.emcommtools.api.service.GpsService;
import com.thetechprepper.emcommtools.api.service.UserService;
import com.thetechprepper.emcommtools.api.service.search.WeatherForecastZoneService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.thetechprepper.emcommtools.api.service.template.*;
import com.thetechprepper.emcommtools.api.util.UtcTimestamp;
import com.thetechprepper.emcommtools.api.model.http.ActionResponse;

@RestController
@RequestMapping("/api/nws")
public class WeatherForecastZoneController {
    private static final Logger LOG = LoggerFactory.getLogger(WeatherForecastZoneController.class);

    @Autowired
    private WeatherForecastZoneService weatherForecastZoneService;

    @Autowired
    private GpsService gpsService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/nearme", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NWSZoneCounty>> findNearMe()
    {
        List<NWSZoneCounty> zones = new ArrayList<>();

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

	zones = weatherForecastZoneService.findNear(
  	    status.getPosition().getLat(), status.getPosition().getLon());

        return zones.isEmpty()
            ? ResponseEntity.status(status.getHttpStatus()).body(List.of())
            : ResponseEntity.ok(zones);
    }

    @GetMapping(value = "/near", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NWSZoneCounty>> findNearbyZones(
            @RequestParam double lat,
            @RequestParam double lon)
    {

        List<NWSZoneCounty> zones = new ArrayList<>();

	zones = weatherForecastZoneService.findNear(lat, lon);

        return zones.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(zones);
    }

    @PostMapping(
         value = "/ftpmail/forecast",
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

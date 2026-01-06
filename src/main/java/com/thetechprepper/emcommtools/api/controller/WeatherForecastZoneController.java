package com.thetechprepper.emcommtools.api.controller;

import java.util.ArrayList;
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
import com.thetechprepper.emcommtools.api.service.search.WeatherForecastZoneService;

@RestController
@RequestMapping("/api/wx-zone")
public class WeatherForecastZoneController {
    private static final Logger LOG = LoggerFactory.getLogger(WeatherForecastZoneController.class);

    @Autowired
    private WeatherForecastZoneService weatherForecastZoneService;

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
}

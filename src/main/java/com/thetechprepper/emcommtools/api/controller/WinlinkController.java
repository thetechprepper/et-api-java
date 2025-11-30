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

import com.thetechprepper.emcommtools.api.model.WinlinkRmsChannel;
import com.thetechprepper.emcommtools.api.service.search.WinlinkSearchService;

@RestController
@RequestMapping("/api/winlink")
public class WinlinkController {
    private static final Logger LOG = LoggerFactory.getLogger(WinlinkController.class);

    @Autowired
    private WinlinkSearchService winlinkSearchService;

    @GetMapping(value = "/near", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WinlinkRmsChannel>> findNearbyChannels(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String band)
    {

        List<WinlinkRmsChannel> channels = new ArrayList<>();

	channels = winlinkSearchService.findNear(lat, lon, band);

        return channels.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(channels);
    }
}

package com.thetechprepper.emcommtools.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thetechprepper.emcommtools.api.model.Aircraft;
import com.thetechprepper.emcommtools.api.service.search.FaaSearchService;
import com.thetechprepper.emcommtools.api.util.FaaUtils;

@RestController
@RequestMapping("/api")
public class FaaSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(FaaSearchService.class);

    @Autowired
    private FaaSearchService faaSearchService;

    @GetMapping(value = "/aircraft", produces = "application/json")
    public ResponseEntity<List<Aircraft>> getAircraft(@RequestParam List<String> icao24) {

        List<Aircraft> aircrafts = new ArrayList<>();

        // TODO: Replace with an OR search and pass the whole list to the search service
        for (String curHex: icao24) {

            if (FaaUtils.isValidIcao24(curHex)) {

                Aircraft aircraft = faaSearchService.findByField(
                    FaaSearchService.INDEX_FIELD_ICAO24,
                    curHex.trim()
                );

                if (aircraft != null) {
                    aircrafts.add(aircraft);
                }
            }
        }

        return aircrafts.isEmpty() 
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())
            : ResponseEntity.ok(aircrafts);
    }
}
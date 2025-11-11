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

//import com.thetechprepper.emcommtools.api.model.PredictionHour;
import com.thetechprepper.emcommtools.api.service.voacap.PredictionHour;
import com.thetechprepper.emcommtools.api.service.voacap.VoacapOutputParser;

@RestController
@RequestMapping("/api")
public class VoacapController {

    private static final Logger LOG = LoggerFactory.getLogger(VoacapController.class);

    @Autowired
    private VoacapOutputParser parser;

    @GetMapping(value = "/voacap", produces = "application/json")
    public ResponseEntity<List<PredictionHour>> getPrediction() {

        final String path = "/home/gaston/itshfbc/run/voacapx.out";

        try {
            List<PredictionHour> hours = parser.parse(path);
            LOG.info("Parsed {} prediction hours", hours.size());
            return ResponseEntity.ok(hours);
        } catch (Exception e) {
            LOG.error("Failed to parse VOACAP output file: {}", path, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
       }

    }
}

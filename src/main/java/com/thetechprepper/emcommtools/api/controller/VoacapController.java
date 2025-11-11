package com.thetechprepper.emcommtools.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thetechprepper.emcommtools.api.model.VoacapRequest;
// TODO import com.thetechprepper.emcommtools.api.model.PredictionHour;
import com.thetechprepper.emcommtools.api.service.voacap.PredictionHour;
import com.thetechprepper.emcommtools.api.service.voacap.VoacapOutputParser;

@RestController
@RequestMapping("/api")
public class VoacapController {

    private static final Logger LOG = LoggerFactory.getLogger(VoacapController.class);

    @Autowired
    private VoacapOutputParser parser;

    @PostMapping(value = "/voacap", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<PredictionHour>> postPrediction(@RequestBody VoacapRequest request) {

        LOG.info("Received VOACAP request: tx={}, rx={}, power={}, mode={}",
                request.getTxLatLon(), request.getRxLatLon(), request.getPower(), request.getMode());

	// TODO add validation
    
	// Build command
        List<String> command = List.of(
                "/opt/emcomm-tools/bin/et-voacap",
                "--tx-latlon", request.getTxLatLon(),
                "--rx-latlon", request.getRxLatLon(),
                "-p", String.valueOf(request.getPower()),
                "-m", request.getMode()
        );

        LOG.info("Executing command: {}", String.join(" ", command));

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); 
            Process process = pb.start();

            // TODO: decided what to do with output
            StringBuilder output = new StringBuilder();
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            LOG.info("et-voacap process exited with code: {}", exitCode);

            if (exitCode != 0) {
                LOG.error("et-voacap failed with exit status: {}", exitCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
            }

        } catch (Exception e) {
            LOG.error("Failed to run et-vocacap", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }

	// Assume that the run was successful and read the output
	// TODO push into properties and use HOME env var
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

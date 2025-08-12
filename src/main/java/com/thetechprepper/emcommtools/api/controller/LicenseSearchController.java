package com.thetechprepper.emcommtools.api.controller;

import com.thetechprepper.emcommtools.api.model.Licensee;
import com.thetechprepper.emcommtools.api.model.Zip2Geo;
import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.model.position.GpsPosition;
import com.thetechprepper.emcommtools.api.model.position.Position;
import com.thetechprepper.emcommtools.api.service.GpsService;
import com.thetechprepper.emcommtools.api.service.search.LicenseSearchService;
import com.thetechprepper.emcommtools.api.service.search.Zip2GeoService;
import com.thetechprepper.emcommtools.api.util.GeoUtils;
import com.thetechprepper.emcommtools.api.util.GridSquareUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LicenseSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseSearchService.class);

    @Autowired
    private LicenseSearchService licenseSearchService;

    @Autowired
    private Zip2GeoService zip2GeoService;

    @Autowired
    private GpsService gpsService;

    @GetMapping(value = "/license", produces = "application/json")
    public ResponseEntity<Licensee> getLicense(@RequestParam String callsign) {

        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        List<Licensee> licensees = licenseSearchService.findByCall(callsign);
        Licensee licensee = Licensee.newInstance().withCallsign("n/a");
        if (licensees.size() > 0) {
           licensee = licensees.get(0);

           String zip = licensee.getZip();
           if (zip != null) {
              Zip2Geo zip2Geo = zip2GeoService.findByZip(zip);
              if (zip2Geo != null) {
                  String grid = GridSquareUtils.toMaidenhead(zip2Geo.getLat(), zip2Geo.getLon());
                  licensee.setGrid(grid);

                  if (gpsService.isGpsReady()) {
                      PositionResponseStatus gpsResponseStatus = gpsService.currentPosition();
                      Position myPosition = gpsResponseStatus.getPosition();
                      Double distanceInMiles = GeoUtils.distance(myPosition.getLat(), myPosition.getLon(),
                              zip2Geo.getLat(), zip2Geo.getLon(), "M");
                      Integer bearing = GeoUtils.bearing(myPosition.getLat(), myPosition.getLon(),
                              zip2Geo.getLat(), zip2Geo.getLon());
                      licensee.setDistance(distanceInMiles);
                      licensee.setBearing(bearing);
                  }

                  licensee.setLat(zip2Geo.getLat());
                  licensee.setLon(zip2Geo.getLon());
                  licensee.setAlt(zip2Geo.getAlt());
                  httpStatus = HttpStatus.OK;
              }
           } else {
               LOG.error("Can't find zip code: '{}' for call sign: '{}'", zip, callsign);
           }
        }
        return new ResponseEntity<Licensee>(licensee, httpStatus);
    }
}

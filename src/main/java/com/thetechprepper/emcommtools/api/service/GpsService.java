package com.thetechprepper.emcommtools.api.service;

import com.thetechprepper.emcommtools.api.model.http.PositionResponseStatus;
import com.thetechprepper.emcommtools.api.model.position.GpsPosition;
import com.thetechprepper.emcommtools.api.util.GeoUtils;
import com.thetechprepper.emcommtools.api.util.GridSquareUtils;
import com.ivkos.gpsd4j.client.GpsdClient;
import com.ivkos.gpsd4j.client.GpsdClientOptions;
import com.ivkos.gpsd4j.messages.reports.TPVReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
public class GpsService {

    private static final Logger LOG = LoggerFactory.getLogger(GpsService.class);
    private static final long MAX_GPS_WAIT_MS = 3000;

    private GpsdClient gpsdClient;

    private volatile GpsPosition position;

    private volatile boolean gpsReady = false;
    private volatile boolean stale = true;

    private String host;
    private Integer port;

    @Autowired
    public GpsService(@Value("${gpsd.host:localhost}") String gpsdHost,
                      @Value("${gpsd.port:2947}") Integer gpsdPort) {
        this.host = gpsdHost;
        this.port = gpsdPort;

        init(gpsdHost, gpsdPort);
    }

    private void init(final String gpsdHost, final Integer gpsdPort) {

        LOG.info("Initializing gpsd at 'tcp://{}:{}'", gpsdHost, gpsdPort);

        GpsdClientOptions options = new GpsdClientOptions()
                .setReconnectOnDisconnect(true)
                .setConnectTimeout(1500) // ms
                .setIdleTimeout(30) // seconds
                .setReconnectAttempts(2)
                .setReconnectInterval(1000); // ms

        try {
            gpsdClient = new GpsdClient(gpsdHost, gpsdPort, options)
                    .addHandler(TPVReport.class, tpv -> {
                        GpsPosition gpsPosition = new GpsPosition()
                                .withLat(tpv.getLatitude())
                                .withLon(tpv.getLongitude())
                                .withAlt(tpv.getAltitude())
                                .withSpeed(tpv.getSpeed())
                                .withTime(tpv.getTime())
                                .withMode(tpv.getMode().name())
                                .withGridSquare(GridSquareUtils.toMaidenhead(tpv.getLatitude(), tpv.getLongitude()));
                        gpsReady = gpsPosition.getMode().equals("ThreeDimensional") ||
                                gpsPosition.getMode().equals("TwoDimensional");
                        LOG.debug("Acquired GPS: {}", gpsPosition);
                        setPosition(gpsPosition);
                        stale = false;
                    })
                    .start();
        } catch (Exception e) {
            LOG.error("Error initializing gpsd client", e);
            gpsReady = false;
        }
    }

    public PositionResponseStatus currentPosition() {

        PositionResponseStatus status = new PositionResponseStatus();

        try {
            gpsdClient.watch();
        } catch (Exception e) {
            LOG.error("Error watching gpsd", e);
            return gpsNotAvailable();
        }

        final long start = System.currentTimeMillis();

        while (true) {
            long waitPeriod = (System.currentTimeMillis() - start);
            if (waitPeriod > MAX_GPS_WAIT_MS) {
                LOG.info("GPS timed out after {} milliseconds", MAX_GPS_WAIT_MS);
                break;
            }
            if (!stale) {
                LOG.debug("GPS refreshed after {} milliseconds", waitPeriod);
                stale = true;
                break;
            }
            try {
                Thread.sleep(50); // avoid busy spinning
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                LOG.warn("Sleep interrupted while waiting for GPS update", ie);
                break;
            }
        }

        gpsdClient.watch(false, true);

        if (!gpsReady) {
            LOG.warn("Can't get GPS position. GPS not available");
            return gpsNotAvailable();
        } else {
            status.setHttpStatus(isGpsReady() ? HttpStatus.OK.value() : HttpStatus.SERVICE_UNAVAILABLE.value());
            status.setReady(isGpsReady());
            status.setPosition(getPosition());

            return status;
        }
    }

    private PositionResponseStatus gpsNotAvailable() {

        PositionResponseStatus status = new PositionResponseStatus();
        status.setReady(false);
        status.setPosition(GpsPosition.NO_GPS);
        status.setHttpStatus(HttpStatus.SERVICE_UNAVAILABLE.value());

        // reinitialize client
        init(host, port);
        return status;
    }

    public GpsPosition getPosition() {
        return position;
    }

    public void setPosition(GpsPosition position) {
        this.position = position;
    }

    public boolean isGpsReady() {
        return gpsReady;
    }

    public void setGpsReady(boolean gpsReady) {
        this.gpsReady = gpsReady;
    }
}

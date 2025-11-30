package com.thetechprepper.emcommtools.api.listener;

import com.thetechprepper.emcommtools.api.service.search.FaaSearchService;
import com.thetechprepper.emcommtools.api.service.search.LicenseSearchService;
import com.thetechprepper.emcommtools.api.service.search.WinlinkSearchService;
import com.thetechprepper.emcommtools.api.service.search.Zip2GeoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes the application on startup. Builds missing indexes.
 */
@Component
public class StartupListener {

    private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

    @Autowired
    private FaaSearchService faaSearchService;

    @Autowired
    private LicenseSearchService licenseSearchService;

    @Autowired
    private WinlinkSearchService winlinkSearchService;

    @Autowired
    private Zip2GeoService zip2GeoService;

    @EventListener
    public void registerCommands(ContextRefreshedEvent event) {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void buildIndexes()
    {
        LOG.info("Initializing search indexes");
        buildZip2GeoIndex();
        buildLicenseIndex();
        buildFaaIndex();
	buildWinlinkIndex();
    }

    public void buildFaaIndex() {
        faaSearchService.createIndex();
    }

    public void buildLicenseIndex() {
        licenseSearchService.createIndex();
    }

    public void buildWinlinkIndex() {
        winlinkSearchService.createIndex();
    }

    public void buildZip2GeoIndex() {
        zip2GeoService.createIndex();
    }
}

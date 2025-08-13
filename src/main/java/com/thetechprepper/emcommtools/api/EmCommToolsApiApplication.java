package com.thetechprepper.emcommtools.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmCommToolsApiApplication {

    public static void main(String[] args) {
	// ensure that we initialize the Vert.x cache for the gpsd library
	// before Spring starts
        System.setProperty("vertx.cacheDirBase", "/tmp");

        SpringApplication.run(EmCommToolsApiApplication.class, args);
    }
}

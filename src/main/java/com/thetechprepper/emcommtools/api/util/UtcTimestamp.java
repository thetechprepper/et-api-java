package com.thetechprepper.emcommtools.api.util;

import java.time.Instant;

public final class UtcTimestamp {

    private UtcTimestamp() {
    }

    public static String now() {
        return Instant.now().toString();
    }
}


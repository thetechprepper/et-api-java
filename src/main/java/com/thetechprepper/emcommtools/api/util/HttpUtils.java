package com.thetechprepper.emcommtools.api.util;

public class HttpUtils {

    public static boolean hasValidStatus(final int httpStatueCode) {
        return httpStatueCode == 200 || httpStatueCode == 201;
    }
}

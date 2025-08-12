package com.thetechprepper.emcommtools.api.util;

import java.util.regex.Pattern;

public class FaaUtils {

    private static final Pattern ICAO24_PATTERN = Pattern.compile("^[0-9A-Fa-f]{6}$");

    public static boolean isValidIcao24(String code) {
        return (code == null) || ICAO24_PATTERN.matcher(code.trim()).matches();
    }

    public static boolean isNotValidIcao24(String code) {
        return !isValidIcao24(code);
    }
}
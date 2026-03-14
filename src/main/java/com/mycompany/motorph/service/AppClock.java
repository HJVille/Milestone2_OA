package com.mycompany.motorph.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class AppClock {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Manila");

    private AppClock() {
    }

    public static ZoneId zone() {
        return APP_ZONE;
    }

    public static LocalDate today() {
        return LocalDate.now(APP_ZONE);
    }

    public static LocalTime timeNow() {
        return LocalTime.now(APP_ZONE);
    }

    public static LocalDateTime dateTimeNow() {
        return LocalDateTime.now(APP_ZONE);
    }
}

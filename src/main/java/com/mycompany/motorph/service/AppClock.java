package com.mycompany.motorph.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class AppClock {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Manila");
    private static final Clock APP_CLOCK = Clock.system(APP_ZONE);

    private AppClock() {
    }

    public static ZoneId zone() {
        return APP_ZONE;
    }

    public static Clock clock() {
        return APP_CLOCK;
    }

    public static LocalDate today() {
        return LocalDate.now(APP_CLOCK);
    }

    public static LocalTime timeNow() {
        return LocalTime.now(APP_CLOCK);
    }

    public static LocalDateTime dateTimeNow() {
        return LocalDateTime.now(APP_CLOCK);
    }
}

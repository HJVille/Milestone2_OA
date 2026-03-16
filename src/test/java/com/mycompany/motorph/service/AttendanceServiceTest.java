package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.AttendanceDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.support.TestDataFactory;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void recordsTimeInAndTimeOutUsingInjectedClock() {
        Path attendanceFile = tempDir.resolve("attendance.csv");
        Employee employee = TestDataFactory.employee(10001, "Alice", "Dela Cruz");

        AttendanceService timeInService = new AttendanceService(
                new AttendanceDAO(attendanceFile),
                fixedClock(2026, 3, 16, 8, 15)
        );

        assertEquals("Time in recorded successfully.", timeInService.timeIn(employee));
        assertEquals("You are already timed in for today.", timeInService.timeIn(employee));
        assertEquals("Timed in at 8:15", timeInService.getTodayStatus(employee));

        AttendanceService timeOutService = new AttendanceService(
                new AttendanceDAO(attendanceFile),
                fixedClock(2026, 3, 16, 17, 45)
        );

        assertEquals("Time out recorded successfully.", timeOutService.timeOut(employee));
        assertEquals("You are already timed out for today.", timeOutService.timeOut(employee));
        assertEquals("Timed out at 17:45", timeOutService.getTodayStatus(employee));

        List<String[]> rows = new AttendanceDAO(attendanceFile).loadAttendanceRows(employee.getEmployeeNumber());
        assertEquals(1, rows.size());
        assertEquals("8:15", rows.get(0)[4]);
        assertEquals("17:45", rows.get(0)[5]);
    }

    private Clock fixedClock(int year, int month, int day, int hour, int minute) {
        return Clock.fixed(
                ZonedDateTime.of(year, month, day, hour, minute, 0, 0, AppClock.zone()).toInstant(),
                AppClock.zone()
        );
    }
}

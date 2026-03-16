package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.AttendanceDAO;
import com.mycompany.motorph.model.Employee;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AttendanceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private final AttendanceDAO attendanceDAO;
    private final Clock clock;

    public AttendanceService() {
        this(new AttendanceDAO(), AppClock.clock());
    }

    public AttendanceService(AttendanceDAO attendanceDAO, Clock clock) {
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<String[]> getAttendanceHistory(int employeeNumber) {

        List<String[]> rows = new ArrayList<>(attendanceDAO.loadAttendanceRows(employeeNumber));
        rows.sort(Comparator.comparing((String[] row) -> parseDate(row[3])).reversed());
        return rows;
    }

    public List<String[]> getRecentAttendance(int employeeNumber, int limit) {

        List<String[]> rows = getAttendanceHistory(employeeNumber);
        if (rows.size() <= limit) {
            return rows;
        }

        return new ArrayList<>(rows.subList(0, limit));
    }

    public List<String[]> getAllAttendanceRows() {

        return new ArrayList<>(attendanceDAO.loadAttendanceRows());
    }

    public String timeIn(Employee employee) {

        if (employee == null) {
            return "Employee record not found.";
        }

        LocalDate today = LocalDate.now(clock);
        String todayText = DATE_FORMAT.format(today);
        List<String[]> rows = attendanceDAO.loadAttendanceRows();

        for (String[] row : rows) {
            if (matchesRow(row, employee.getEmployeeNumber(), todayText)) {
                if (row.length >= 6 && !row[5].trim().isEmpty()) {
                    return "Attendance for today is already complete.";
                }
                return "You are already timed in for today.";
            }
        }

        rows.add(new String[]{
            String.valueOf(employee.getEmployeeNumber()),
            employee.getLastName(),
            employee.getFirstName(),
            todayText,
            TIME_FORMAT.format(LocalTime.now(clock)),
            ""
        });
        attendanceDAO.saveAttendanceRows(rows);

        return "Time in recorded successfully.";
    }

    public String timeOut(Employee employee) {

        if (employee == null) {
            return "Employee record not found.";
        }

        LocalDate today = LocalDate.now(clock);
        String todayText = DATE_FORMAT.format(today);
        List<String[]> rows = attendanceDAO.loadAttendanceRows();

        for (int i = rows.size() - 1; i >= 0; i--) {
            String[] row = rows.get(i);
            if (!matchesRow(row, employee.getEmployeeNumber(), todayText)) {
                continue;
            }

            if (row.length < 6) {
                continue;
            }

            if (!row[5].trim().isEmpty()) {
                return "You are already timed out for today.";
            }

            row[5] = TIME_FORMAT.format(LocalTime.now(clock));
            attendanceDAO.saveAttendanceRows(rows);
            return "Time out recorded successfully.";
        }

        return "No open attendance record found for today.";
    }

    public String getTodayStatus(Employee employee) {

        if (employee == null) {
            return "Employee record unavailable.";
        }

        String todayText = DATE_FORMAT.format(LocalDate.now(clock));

        for (String[] row : attendanceDAO.loadAttendanceRows(employee.getEmployeeNumber())) {
            if (!matchesRow(row, employee.getEmployeeNumber(), todayText)) {
                continue;
            }

            if (row.length >= 6 && row[5] != null && !row[5].trim().isEmpty()) {
                return "Timed out at " + row[5].trim();
            }

            return "Timed in at " + row[4].trim();
        }

        return "No attendance activity recorded today.";
    }

    private boolean matchesRow(String[] row, int employeeNumber, String dateText) {

        return row.length >= 6
                && row[0].trim().equals(String.valueOf(employeeNumber))
                && row[3].trim().equals(dateText);
    }

    private LocalDate parseDate(String value) {

        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception e) {
            return LocalDate.MIN;
        }
    }
}

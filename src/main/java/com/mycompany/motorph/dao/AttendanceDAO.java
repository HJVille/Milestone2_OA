package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Attendance;
import com.mycompany.motorph.model.AttendanceInterface;
import com.mycompany.motorph.model.Employee;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceDAO implements AttendanceInterface {

    private static final String HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Logger LOGGER = Logger.getLogger(AttendanceDAO.class.getName());

    public void loadAttendance(List<Employee> employees,
                               LocalDate startDate,
                               LocalDate endDate) {

        loadAttendance(CsvFilePaths.ATTENDANCE.toString(), employees, startDate, endDate);
    }

    @Override
    public void loadAttendance(String filePath,
                               List<Employee> employees,
                               LocalDate startDate,
                               LocalDate endDate) {

        for (Employee emp : employees) {
            emp.clearAttendance();
        }

        try (BufferedReader br = Files.newBufferedReader(CsvFilePaths.resolve(filePath))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length < 6) {
                    continue;
                }

                int employeeNumber;
                try {
                    employeeNumber = Integer.parseInt(data[0].trim());
                } catch (Exception e) {
                    continue;
                }

                String dateStr = data[3].trim();
                String logInStr = data[4].trim();
                String logOutStr = data[5].trim();

                if (logInStr.isEmpty() || logOutStr.isEmpty()) {
                    continue;
                }

                LocalDate attendanceDate;
                try {
                    attendanceDate = LocalDate.parse(dateStr, DATE_FORMAT);
                } catch (Exception e) {
                    continue;
                }

                if (attendanceDate.isBefore(startDate) || attendanceDate.isAfter(endDate)) {
                    continue;
                }

                double logIn;
                double logOut;

                try {
                    logIn = convertTimeToDouble(logInStr);
                    logOut = convertTimeToDouble(logOutStr);
                } catch (Exception e) {
                    continue;
                }

                Attendance attendance = new Attendance(dateStr, logIn, logOut, 1.0);
                double hoursWorked = Math.min(12, attendance.calculateHoursWorked());

                for (Employee emp : employees) {

                    if (emp.getEmployeeNumber() == employeeNumber) {

                        emp.addAttendance(attendance);
                        emp.addWorkedHours(hoursWorked);
                        break;

                    }

                }

            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load attendance records.", e);
        }
    }

    public List<String[]> loadAttendanceRows() {

        List<String[]> rows = new ArrayList<>();
        Path filePath = CsvFilePaths.ATTENDANCE;

        if (!Files.exists(filePath)) {
            return rows;
        }

        try (BufferedReader br = Files.newBufferedReader(filePath)) {

            String line = br.readLine();
            if (line == null) {
                return rows;
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                rows.add(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load attendance rows.", e);
        }

        return rows;
    }

    public List<String[]> loadAttendanceRows(int employeeNumber) {

        List<String[]> matches = new ArrayList<>();

        for (String[] row : loadAttendanceRows()) {
            if (row.length >= 6 && row[0].trim().equals(String.valueOf(employeeNumber))) {
                matches.add(row);
            }
        }

        return matches;
    }

    public void saveAttendanceRows(List<String[]> rows) {

        Path filePath = CsvFilePaths.ATTENDANCE;

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to prepare attendance file.", e);
            return;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            bw.write(HEADER);
            bw.newLine();

            for (String[] row : rows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to save attendance rows.", e);
        }
    }

    private double convertTimeToDouble(String time) {

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return Math.round((hours + minutes / 60.0) * 100.0) / 100.0;
    }
}

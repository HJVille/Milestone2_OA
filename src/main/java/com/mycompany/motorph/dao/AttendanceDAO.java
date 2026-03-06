package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Attendance;
import com.mycompany.motorph.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceDAO {

    public void loadAttendance(String filePath,
                               List<Employee> employees,
                               LocalDate startDate,
                               LocalDate endDate) {

        for (Employee emp : employees) {
            emp.setHoursWorked(0);
            emp.getAttendanceRecords().clear();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length < 6) continue;

                int employeeNumber;

                try {
                    employeeNumber = Integer.parseInt(data[0].trim());
                } catch (Exception e) {
                    continue;
                }

                String dateStr = data[3].trim();
                String logInStr = data[4].trim();
                String logOutStr = data[5].trim();

                if (logInStr.isEmpty() || logOutStr.isEmpty()) continue;

                LocalDate attendanceDate;

                try {
                    attendanceDate = LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    continue;
                }

                // FILTER DATE RANGE
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

                double hoursWorked = logOut - logIn;

                hoursWorked -= 1.0; // lunch break

                if (hoursWorked < 0) hoursWorked = 0;
                if (hoursWorked > 12) hoursWorked = 12;

                hoursWorked = Math.round(hoursWorked * 100.0) / 100.0;

                Attendance attendance = new Attendance(dateStr, logIn, logOut);

                for (Employee emp : employees) {

                    if (emp.getEmployeeNumber() == employeeNumber) {

                        emp.addAttendance(attendance);
                        emp.addWorkedHours(hoursWorked);
                        break;

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double convertTimeToDouble(String time) {

        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return Math.round((hours + minutes / 60.0) * 100.0) / 100.0;

    }
}
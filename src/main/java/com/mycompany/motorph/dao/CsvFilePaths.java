package com.mycompany.motorph.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CsvFilePaths {

    public static final Path USERS = resolve("users.csv");
    public static final Path EMPLOYEES = resolve("employees.csv");
    public static final Path ATTENDANCE = resolve("attendance.csv");
    public static final Path LEAVE_REQUESTS = resolve("leave_requests.csv");
    public static final Path PAYROLL_RECORDS = resolve("payroll_records.csv");
    public static final Path NOTIFICATIONS = resolve("notifications.csv");

    private CsvFilePaths() {
    }

    public static Path resolve(String fileName) {

        Path csvFolderPath = Paths.get("CSVs", fileName);
        if (Files.exists(csvFolderPath)) {
            return csvFolderPath;
        }

        return Paths.get(fileName);
    }
}

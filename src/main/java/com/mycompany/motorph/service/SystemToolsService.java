package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.CsvFilePaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SystemToolsService {

    private static final Path PASSWORD_AUDIT_PATH = CsvFilePaths.resolve("password_audit.csv");

    public List<String[]> getCsvStatusRows() {
        List<String[]> rows = new ArrayList<>();
        addStatusRow(rows, "Users", CsvFilePaths.USERS);
        addStatusRow(rows, "Employees", CsvFilePaths.EMPLOYEES);
        addStatusRow(rows, "Attendance", CsvFilePaths.ATTENDANCE);
        addStatusRow(rows, "Leave Requests", CsvFilePaths.LEAVE_REQUESTS);
        addStatusRow(rows, "Payroll Records", CsvFilePaths.PAYROLL_RECORDS);
        addStatusRow(rows, "Password Audit", PASSWORD_AUDIT_PATH);
        return rows;
    }

    public List<String[]> getPasswordAuditRows() {
        List<String[]> rows = new ArrayList<>();

        if (!Files.exists(PASSWORD_AUDIT_PATH)) {
            return rows;
        }

        try {
            boolean firstLine = true;
            for (String line : Files.readAllLines(PASSWORD_AUDIT_PATH)) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length >= 3) {
                    rows.add(new String[]{data[0], data[1], data[2]});
                }
            }
        } catch (Exception e) {
            rows.add(new String[]{"Status", "Unable to read audit log", e.getMessage()});
        }

        return rows;
    }

    private void addStatusRow(List<String[]> rows, String label, Path path) {
        rows.add(new String[]{
                label,
                path.toAbsolutePath().toString(),
                Files.exists(path) ? "Yes" : "No"
        });
    }
}

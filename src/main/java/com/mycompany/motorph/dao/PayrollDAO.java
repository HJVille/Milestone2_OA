package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.PayrollInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PayrollDAO implements PayrollInterface {

    private static final String HEADER =
            "employeeNumber,employeeName,periodStart,periodEnd,basicSalary,riceSubsidy,phoneAllowance,clothingAllowance,grossSalary,sss,philhealth,pagibig,tax,netSalary";
    private static final Logger LOGGER = Logger.getLogger(PayrollDAO.class.getName());

    private final Path filePath;

    public PayrollDAO() {
        this(CsvFilePaths.PAYROLL_RECORDS);
    }

    public PayrollDAO(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void savePayroll(Payslip payslip) {
        savePayrolls(List.of(payslip));
    }

    public void savePayrolls(List<Payslip> payslips) {

        if (payslips == null || payslips.isEmpty()) {
            return;
        }

        Map<String, String[]> mergedRows = new LinkedHashMap<>();
        for (String[] row : loadAllRows()) {
            if (row.length >= 14) {
                mergedRows.put(buildKey(row[0], row[2], row[3]), row);
            }
        }

        for (Payslip payslip : payslips) {
            String[] row = toRow(payslip);
            mergedRows.put(buildKey(row[0], row[2], row[3]), row);
        }

        writeAllRows(new ArrayList<>(mergedRows.values()));
    }

    @Override
    public List<String[]> getPayrollHistory(int employeeNumber) {

        List<String[]> history = new ArrayList<>();

        for (String[] row : loadAllRows()) {
            if (row.length == 0) {
                continue;
            }

            try {
                int empNum = Integer.parseInt(row[0].trim());
                if (employeeNumber == 0 || empNum == employeeNumber) {
                    history.add(row);
                }
            } catch (Exception e) {
                if (employeeNumber == 0) {
                    history.add(row);
                }
            }
        }

        return history;
    }

    private List<String[]> loadAllRows() {

        List<String[]> rows = new ArrayList<>();

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
            LOGGER.log(Level.WARNING, "Unable to load payroll records.", e);
        }

        return rows;
    }

    private void writeAllRows(List<String[]> rows) {

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to prepare payroll records file.", e);
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
            LOGGER.log(Level.WARNING, "Unable to save payroll records.", e);
        }
    }

    private String[] toRow(Payslip payslip) {

        return new String[]{
            String.valueOf(payslip.getEmployeeNumber()),
            escapeCsv(payslip.getEmployeeName()),
            payslip.getPeriodStart(),
            payslip.getPeriodEnd(),
            String.valueOf(payslip.getBasicSalary()),
            String.valueOf(payslip.getRiceSubsidy()),
            String.valueOf(payslip.getPhoneAllowance()),
            String.valueOf(payslip.getClothingAllowance()),
            String.valueOf(payslip.getGrossSalary()),
            String.valueOf(payslip.getSss()),
            String.valueOf(payslip.getPhilhealth()),
            String.valueOf(payslip.getPagibig()),
            String.valueOf(payslip.getTax()),
            String.valueOf(payslip.getNetSalary())
        };
    }

    private String buildKey(String employeeNumber, String periodStart, String periodEnd) {
        return employeeNumber + "|" + periodStart + "|" + periodEnd;
    }

    private String escapeCsv(String value) {

        if (value == null) {
            return "";
        }

        String cleaned = value.trim();
        if (cleaned.contains(",") || cleaned.contains("\"")) {
            return "\"" + cleaned.replace("\"", "\"\"") + "\"";
        }

        return cleaned;
    }
}

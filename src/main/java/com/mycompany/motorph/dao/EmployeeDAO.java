package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class EmployeeDAO implements EmployeeInterface {

    private static final double WORK_DAYS_PER_MONTH = 21.0;
    private static final double HOURS_PER_DAY = 8.0;
    private static final String HEADER =
            "EmployeeNumber,LastName,FirstName,BirthDate,Address,Phone,SSS,Philhealth,TIN,Pagibig,Status,Position,Supervisor,BasicSalary,RiceSubsidy,PhoneAllowance,ClothingAllowance,GrossSemiMonthlyRate,HourlyRate";
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());

    @Override
    public List<Employee> loadEmployees(String filePath) {

        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                Employee employee = parseEmployee(line);
                if (employee != null) {
                    employees.add(employee);
                }

            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load employees from " + filePath, e);
        }

        return employees;
    }

    @Override
    public void addEmployee(Employee employee, String filePath) {

        List<Employee> employees = loadEmployees(filePath);
        employees.add(employee);
        saveEmployees(employees, filePath);

    }

    @Override
    public void updateEmployee(int employeeNumber,
                               Employee updatedEmployee,
                               List<Employee> employees,
                               String filePath) {

        for (int i = 0; i < employees.size(); i++) {

            if (employees.get(i).getEmployeeNumber() == employeeNumber) {

                employees.set(i, updatedEmployee);
                break;

            }

        }

        saveEmployees(employees, filePath);

    }

    @Override
    public void deleteEmployee(int employeeNumber,
                               List<Employee> employees,
                               String filePath) {

        employees.removeIf(emp -> emp.getEmployeeNumber() == employeeNumber);
        saveEmployees(employees, filePath);

    }

    @Override
    public void saveEmployees(List<Employee> employees, String filePath) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            bw.write(HEADER);

            for (Employee emp : employees) {
                bw.write("\n" + toCSV(emp));
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to save employees to " + filePath, e);
        }

    }

    @Override
    public String toCSV(Employee emp) {

        return joinCsv(
                String.valueOf(emp.getEmployeeNumber()),
                emp.getLastName(),
                emp.getFirstName(),
                emp.getBirthDate(),
                emp.getAddress(),
                emp.getPhone(),
                emp.getSss(),
                emp.getPhilhealth(),
                emp.getTin(),
                emp.getPagibig(),
                emp.getStatus(),
                emp.getPosition(),
                emp.getSupervisor(),
                String.valueOf(emp.getBasicSalary()),
                String.valueOf(emp.getRiceSubsidy()),
                String.valueOf(emp.getPhoneAllowance()),
                String.valueOf(emp.getClothingAllowance()),
                String.valueOf(emp.getGrossSemiMonthlyRate()),
                String.valueOf(emp.getHourlyRate())
        );

    }

    @Override
    public String clean(String value) {

        if (value == null) {
            return "";
        }

        String cleaned = value.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned.replace("\"\"", "\"");

    }

    private Employee parseEmployee(String line) {

        String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (data.length < 10) {
            return null;
        }

        if (isStructuredRow(data)) {
            return parseStructuredEmployee(data);
        }

        ParsedEmployeeRow parsed = parseEmployeeRow(data);
        double grossSemiMonthlyRate = round(parsed.basicSalary / 2.0);
        double hourlyRate = parsed.basicSalary > 0 ? round(parsed.basicSalary / WORK_DAYS_PER_MONTH / HOURS_PER_DAY) : 0.0;

        return new Employee(
                parsed.employeeNumber,
                parsed.lastName,
                parsed.firstName,
                parsed.birthDate,
                parsed.address,
                parsed.phone,
                parsed.sss,
                parsed.philhealth,
                parsed.tin,
                parsed.pagibig,
                parsed.status,
                parsed.position,
                parsed.supervisor,
                parsed.basicSalary,
                parsed.riceSubsidy,
                parsed.phoneAllowance,
                parsed.clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );
    }

    private boolean isStructuredRow(String[] data) {
        return data.length >= 19
                && PHONE_PATTERN.matcher(clean(safeGet(data, 5))).matches()
                && !clean(safeGet(data, 10)).isBlank()
                && parseDoubleSafe(clean(safeGet(data, 13))) > 0.0;
    }

    private Employee parseStructuredEmployee(String[] data) {
        int employeeNumber = parseInt(clean(safeGet(data, 0)));
        String lastName = clean(safeGet(data, 1));
        String firstName = clean(safeGet(data, 2));
        String birthDate = clean(safeGet(data, 3));
        String address = clean(safeGet(data, 4));
        String phone = clean(safeGet(data, 5));
        String sss = clean(safeGet(data, 6));
        String philhealth = clean(safeGet(data, 7));
        String tin = clean(safeGet(data, 8));
        String pagibig = clean(safeGet(data, 9));
        String status = clean(safeGet(data, 10));
        String position = clean(safeGet(data, 11));
        String supervisor = clean(safeGet(data, 12));
        double basicSalary = parseDoubleSafe(clean(safeGet(data, 13)));
        double riceSubsidy = parseDoubleSafe(clean(safeGet(data, 14)));
        double phoneAllowance = parseDoubleSafe(clean(safeGet(data, 15)));
        double clothingAllowance = parseDoubleSafe(clean(safeGet(data, 16)));
        double grossSemiMonthlyRate = parseDoubleSafe(clean(safeGet(data, 17)));
        double hourlyRate = parseDoubleSafe(clean(safeGet(data, 18)));

        if (grossSemiMonthlyRate <= 0.0 && basicSalary > 0.0) {
            grossSemiMonthlyRate = round(basicSalary / 2.0);
        }
        if (hourlyRate <= 0.0 && basicSalary > 0.0) {
            hourlyRate = round(basicSalary / WORK_DAYS_PER_MONTH / HOURS_PER_DAY);
        }

        return new Employee(
                employeeNumber,
                lastName,
                firstName,
                birthDate,
                address,
                phone,
                sss,
                philhealth,
                tin,
                pagibig,
                status,
                position,
                supervisor,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );
    }

    private ParsedEmployeeRow parseEmployeeRow(String[] data) {

        ParsedEmployeeRow row = new ParsedEmployeeRow();
        row.employeeNumber = parseInt(clean(safeGet(data, 0)));
        row.lastName = clean(safeGet(data, 1));
        row.firstName = clean(safeGet(data, 2));
        row.birthDate = clean(safeGet(data, 3));

        int phoneIndex = findFirstIndex(data, 4, PHONE_PATTERN);
        if (phoneIndex < 0) {
            phoneIndex = Math.min(6, data.length - 1);
        }

        row.address = join(data, 4, Math.max(4, phoneIndex - 1));
        row.phone = clean(safeGet(data, phoneIndex));
        row.sss = clean(safeGet(data, phoneIndex + 1));
        row.philhealth = clean(safeGet(data, phoneIndex + 2));
        row.tin = clean(safeGet(data, phoneIndex + 3));
        row.pagibig = clean(safeGet(data, phoneIndex + 4));

        int cursor = phoneIndex + 5;
        row.status = clean(safeGet(data, cursor));
        cursor++;

        List<String> textTail = new ArrayList<>();
        while (cursor < data.length && !isNumericToken(data[cursor])) {
            textTail.add(clean(data[cursor]));
            cursor++;
        }

        row.position = textTail.isEmpty() ? "" : textTail.get(0);
        row.supervisor = textTail.size() > 1
                ? String.join(", ", textTail.subList(1, textTail.size()))
                : "";

        List<Double> numericTail = new ArrayList<>();
        for (int i = cursor; i < data.length; i++) {
            numericTail.add(parseDoubleSafe(clean(data[i])));
        }
        applyCompensationValues(row, numericTail);

        return row;
    }

    private void applyCompensationValues(ParsedEmployeeRow row, List<Double> numericTail) {

        int basicIndex = -1;
        for (int i = numericTail.size() - 1; i >= 0; i--) {
            if (numericTail.get(i) >= 10000) {
                basicIndex = i;
                break;
            }
        }

        if (basicIndex < 0 && !numericTail.isEmpty()) {
            basicIndex = numericTail.size() - 1;
        }

        row.basicSalary = basicIndex >= 0 ? numericTail.get(basicIndex) : 0.0;

        List<Double> allowances = new ArrayList<>();
        for (int i = basicIndex + 1; i < numericTail.size(); i++) {
            allowances.add(numericTail.get(i));
        }

        row.riceSubsidy = allowances.size() > 0 ? allowances.get(0) : 0.0;
        row.phoneAllowance = allowances.size() > 1 ? allowances.get(1) : 0.0;
        row.clothingAllowance = allowances.size() > 2 ? allowances.get(2) : 0.0;

        // Some source rows omit clothing allowance even when it matches phone allowance.
        if (allowances.size() == 2 && row.phoneAllowance > 0.0 && row.clothingAllowance == 0.0) {
            row.clothingAllowance = row.phoneAllowance;
        }
    }

    private int findFirstIndex(String[] data, int startInclusive, Pattern pattern) {

        for (int i = startInclusive; i < data.length; i++) {
            if (pattern.matcher(clean(data[i])).matches()) {
                return i;
            }
        }

        return -1;
    }

    private boolean isNumericToken(String value) {

        try {
            Double.parseDouble(clean(value));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private String safeGet(String[] data, int index) {

        if (index < 0 || index >= data.length) {
            return "";
        }

        return data[index];
    }

    private String join(String[] values, int startInclusive, int endInclusive) {

        StringBuilder builder = new StringBuilder();

        for (int i = startInclusive; i <= endInclusive && i < values.length; i++) {
            if (i > startInclusive) {
                builder.append(", ");
            }
            builder.append(clean(values[i]));
        }

        return builder.toString();
    }

    private int parseInt(String value) {

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }

    }

    private double parseDoubleSafe(String value) {

        if (value == null) {
            return 0.0;
        }

        String cleaned = value.trim();

        if (cleaned.equalsIgnoreCase("N/A")
                || cleaned.equals("-")
                || cleaned.isEmpty()) {

            return 0.0;

        }

        try {

            return Double.parseDouble(cleaned.replace(",", ""));

        } catch (Exception e) {

            return 0.0;

        }

    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String joinCsv(String... values) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(escapeCsv(values[i]));
        }

        return builder.toString();
    }

    private String escapeCsv(String value) {

        String cleaned = value == null ? "" : value.trim();
        boolean needsQuotes = cleaned.contains(",")
                || cleaned.contains("\"")
                || cleaned.contains("\n");

        if (needsQuotes) {
            return "\"" + cleaned.replace("\"", "\"\"") + "\"";
        }

        return cleaned;
    }

    private static class ParsedEmployeeRow {
        private int employeeNumber;
        private String lastName;
        private String firstName;
        private String birthDate;
        private String address;
        private String phone;
        private String sss;
        private String philhealth;
        private String tin;
        private String pagibig;
        private String status;
        private String position;
        private String supervisor;
        private double basicSalary;
        private double riceSubsidy;
        private double phoneAllowance;
        private double clothingAllowance;
    }
}

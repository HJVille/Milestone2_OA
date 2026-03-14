package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.CsvFilePaths;
import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Attendance;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.PagibigDeduction;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.PhilHealthDeduction;
import com.mycompany.motorph.model.SSSDeduction;
import com.mycompany.motorph.model.TaxDeduction;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class EmployeePortalService {

    private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
    private static final Logger LOGGER = Logger.getLogger(EmployeePortalService.class.getName());
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Employee getEmployeeByNumber(int employeeNumber) {
        for (Employee employee : loadEmployees()) {
            if (employee.getEmployeeNumber() == employeeNumber) {
                return employee;
            }
        }
        return null;
    }

    public List<PayrollPeriodOption> getAvailablePayrollPeriods(int employeeNumber) {
        Map<YearMonth, List<Attendance>> byMonth = loadAttendanceByMonth(employeeNumber);
        List<PayrollPeriodOption> periods = new ArrayList<>();

        for (Map.Entry<YearMonth, List<Attendance>> entry : byMonth.entrySet()) {
            YearMonth month = entry.getKey();
            List<Attendance> records = entry.getValue();

            boolean hasFirstHalf = records.stream().anyMatch(att -> parseAttendanceDate(att).getDayOfMonth() <= 15);
            boolean hasSecondHalf = records.stream().anyMatch(att -> parseAttendanceDate(att).getDayOfMonth() > 15);

            if (hasFirstHalf) {
                periods.add(createSemiMonthlyOption(month, 1, 15));
            }
            if (hasSecondHalf) {
                periods.add(createSemiMonthlyOption(month, 16, month.lengthOfMonth()));
            }

            periods.add(createMonthlyOption(month));
        }

        periods.sort(Comparator.comparing(PayrollPeriodOption::getEndDate).reversed()
                .thenComparing(option -> option.getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0));
        return periods;
    }

    public List<PayrollPeriodOption> getAvailablePayrollPeriods() {
        Map<YearMonth, boolean[]> availability = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(CsvFilePaths.ATTENDANCE)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 6) {
                    continue;
                }

                LocalDate date = LocalDate.parse(clean(data[3]), ATTENDANCE_DATE_FORMAT);
                boolean[] flags = availability.computeIfAbsent(YearMonth.from(date), ignored -> new boolean[2]);
                if (date.getDayOfMonth() <= 15) {
                    flags[0] = true;
                } else {
                    flags[1] = true;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to derive payroll periods from attendance data.", e);
        }

        List<PayrollPeriodOption> periods = new ArrayList<>();
        for (Map.Entry<YearMonth, boolean[]> entry : availability.entrySet()) {
            YearMonth month = entry.getKey();
            if (entry.getValue()[0]) {
                periods.add(createSemiMonthlyOption(month, 1, 15));
            }
            if (entry.getValue()[1]) {
                periods.add(createSemiMonthlyOption(month, 16, month.lengthOfMonth()));
            }
            periods.add(createMonthlyOption(month));
        }

        periods.sort(Comparator.comparing(PayrollPeriodOption::getEndDate).reversed()
                .thenComparing(option -> option.getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0));
        return periods;
    }

    public EmployeePayrollSummary getPayrollSummary(int employeeNumber, String periodKey) {
        Employee employee = getEmployeeByNumber(employeeNumber);
        if (employee == null) {
            return null;
        }

        PayrollPeriodOption selectedPeriod = null;
        for (PayrollPeriodOption option : getAvailablePayrollPeriods(employeeNumber)) {
            if (option.getKey().equals(periodKey)) {
                selectedPeriod = option;
                break;
            }
        }

        if (selectedPeriod == null) {
            return null;
        }

        return computeSummary(employee, selectedPeriod, filterAttendance(employeeNumber, selectedPeriod));
    }

    public List<EmployeePayrollSummary> getPayrollHistory(int employeeNumber) {
        Employee employee = getEmployeeByNumber(employeeNumber);
        if (employee == null) {
            return List.of();
        }

        List<EmployeePayrollSummary> history = new ArrayList<>();
        for (PayrollPeriodOption option : getAvailablePayrollPeriods(employeeNumber)) {
            history.add(computeSummary(employee, option, filterAttendance(employeeNumber, option)));
        }
        history.sort(Comparator
                .comparing(EmployeePayrollSummary::getPeriod, Comparator.comparing(PayrollPeriodOption::getEndDate).reversed())
                .thenComparing(summary -> summary.getPeriod().getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0));
        return history;
    }

    public List<Employee> getAllEmployees() {
        return loadEmployees();
    }

    public List<EmployeePayrollSummary> getPayrollSummariesForPeriod(String periodKey) {

        PayrollPeriodOption selectedPeriod = findPeriod(periodKey, getAvailablePayrollPeriods());
        if (selectedPeriod == null) {
            return List.of();
        }

        List<EmployeePayrollSummary> summaries = new ArrayList<>();
        for (Employee employee : loadEmployees()) {
            summaries.add(computeSummary(employee, selectedPeriod, filterAttendance(employee.getEmployeeNumber(), selectedPeriod)));
        }

        summaries.sort(Comparator.comparingInt(EmployeePayrollSummary::getEmployeeNumber));
        return summaries;
    }

    public List<Payslip> buildPayslipsForPeriod(String periodKey) {

        List<Payslip> payslips = new ArrayList<>();

        for (EmployeePayrollSummary summary : getPayrollSummariesForPeriod(periodKey)) {
            payslips.add(new Payslip(
                    summary.getEmployeeNumber(),
                    summary.getEmployeeName(),
                    summary.getPeriod().getStartDate().toString(),
                    summary.getPeriod().getEndDate().toString(),
                    summary.getBasicSalary(),
                    summary.getRiceSubsidy(),
                    summary.getPhoneAllowance(),
                    summary.getClothingAllowance(),
                    summary.getGrossSalary(),
                    summary.getSss(),
                    summary.getPhilhealth(),
                    summary.getPagibig(),
                    summary.getWithholdingTax(),
                    summary.getNetSalary()
            ));
        }

        return payslips;
    }

    private List<Employee> loadEmployees() {
        return employeeDAO.loadEmployees(CsvFilePaths.EMPLOYEES.toString());
    }

    private Map<YearMonth, List<Attendance>> loadAttendanceByMonth(int employeeNumber) {
        Map<YearMonth, List<Attendance>> byMonth = new LinkedHashMap<>();

        for (Attendance attendance : loadAttendance(employeeNumber)) {
            LocalDate date = parseAttendanceDate(attendance);
            byMonth.computeIfAbsent(YearMonth.from(date), ignored -> new ArrayList<>()).add(attendance);
        }

        return byMonth;
    }

    private List<Attendance> loadAttendance(int employeeNumber) {
        List<Attendance> records = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(CsvFilePaths.ATTENDANCE)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 6 || parseInt(data[0]) != employeeNumber) {
                    continue;
                }

                String date = clean(data[3]);
                String login = clean(data[4]);
                String logout = clean(data[5]);
                if (date.isEmpty() || login.isEmpty() || logout.isEmpty()) {
                    continue;
                }

                double logInTime = parseTimeAsDecimal(login);
                double logOutTime = parseTimeAsDecimal(logout);
                records.add(new Attendance(date, logInTime, logOutTime, 1.0));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load employee attendance records.", e);
        }

        records.sort(Comparator.comparing(this::parseAttendanceDate));
        return records;
    }

    private PayrollPeriodOption findPeriod(String periodKey, List<PayrollPeriodOption> periods) {
        for (PayrollPeriodOption option : periods) {
            if (option.getKey().equals(periodKey)) {
                return option;
            }
        }
        return null;
    }

    private List<Attendance> filterAttendance(int employeeNumber, PayrollPeriodOption period) {
        List<Attendance> matches = new ArrayList<>();
        for (Attendance attendance : loadAttendance(employeeNumber)) {
            LocalDate date = parseAttendanceDate(attendance);
            if ((date.isEqual(period.getStartDate()) || date.isAfter(period.getStartDate()))
                    && (date.isEqual(period.getEndDate()) || date.isBefore(period.getEndDate()))) {
                matches.add(attendance);
            }
        }
        return matches;
    }

    private EmployeePayrollSummary computeSummary(Employee employee,
                                                  PayrollPeriodOption period,
                                                  List<Attendance> attendanceRows) {
        boolean monthlyPeriod = period.getType() == PayrollPeriodOption.Type.MONTHLY;
        boolean secondCutoff = isSecondSemiMonthlyCutoff(period);
        boolean includeAllowances = monthlyPeriod || secondCutoff;
        boolean includeDeductions = monthlyPeriod || secondCutoff;

        double basicSalary = monthlyPeriod
                ? round(employee.getBasicSalary())
                : round(employee.getBasicSalary() / 2.0);
        double riceSubsidy = includeAllowances ? round(employee.getRiceSubsidy()) : 0.0;
        double phoneAllowance = includeAllowances ? round(employee.getPhoneAllowance()) : 0.0;
        double clothingAllowance = includeAllowances ? round(employee.getClothingAllowance()) : 0.0;
        double grossSalary = round(basicSalary + riceSubsidy + phoneAllowance + clothingAllowance);

        double monthlySalary = employee.getBasicSalary();
        double fullSss = includeDeductions ? round(new SSSDeduction().compute(monthlySalary)) : 0.0;
        double fullPhilhealth = includeDeductions ? round(new PhilHealthDeduction().compute(monthlySalary)) : 0.0;
        double fullPagibig = includeDeductions ? round(new PagibigDeduction().compute(monthlySalary)) : 0.0;
        double taxableIncome = monthlySalary - fullSss - fullPhilhealth - fullPagibig;
        double fullTax = includeDeductions ? round(new TaxDeduction().compute(taxableIncome)) : 0.0;

        double sss = fullSss;
        double philhealth = fullPhilhealth;
        double pagibig = fullPagibig;
        double withholdingTax = fullTax;
        double totalDeductions = round(sss + philhealth + pagibig + withholdingTax);
        double netSalary = round(grossSalary - totalDeductions);

        Set<LocalDate> daysWorked = new LinkedHashSet<>();
        double attendanceHours = 0.0;
        for (Attendance attendance : attendanceRows) {
            daysWorked.add(parseAttendanceDate(attendance));
            attendanceHours += attendance.getHoursWorked();
        }

        return new EmployeePayrollSummary(
                employee.getEmployeeNumber(),
                employee.getEmployeeName(),
                period,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSalary,
                sss,
                philhealth,
                pagibig,
                withholdingTax,
                totalDeductions,
                netSalary,
                daysWorked.size(),
                round(attendanceHours)
        );
    }

    private boolean isSecondSemiMonthlyCutoff(PayrollPeriodOption period) {
        return period.getType() == PayrollPeriodOption.Type.SEMI_MONTHLY
                && period.getStartDate().getDayOfMonth() > 15;
    }

    private PayrollPeriodOption createSemiMonthlyOption(YearMonth month, int startDay, int endDay) {
        LocalDate start = month.atDay(startDay);
        LocalDate end = month.atDay(endDay);
        String key = "SEMI:" + start;
        String label = String.format(Locale.ENGLISH, "%s | %d-%d", month, startDay, endDay);
        return new PayrollPeriodOption(key, label, PayrollPeriodOption.Type.SEMI_MONTHLY, start, end);
    }

    private PayrollPeriodOption createMonthlyOption(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        String key = "MONTHLY:" + month;
        String label = month + " | Monthly Summary";
        return new PayrollPeriodOption(key, label, PayrollPeriodOption.Type.MONTHLY, start, end);
    }

    private LocalDate parseAttendanceDate(Attendance attendance) {
        return LocalDate.parse(attendance.getDate(), ATTENDANCE_DATE_FORMAT);
    }

    private double parseTimeAsDecimal(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].trim());
        return round(hours + minutes / 60.0);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(clean(value));
        } catch (Exception e) {
            return -1;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(clean(value));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replace("\"", "");
    }

    private String join(String[] values, int startInclusive, int endInclusive) {
        StringBuilder builder = new StringBuilder();
        for (int i = startInclusive; i <= endInclusive; i++) {
            if (i > startInclusive) {
                builder.append(", ");
            }
            builder.append(clean(values[i]));
        }
        return builder.toString();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ParsedEmployeeRow parseEmployeeRow(String[] data) {
        ParsedEmployeeRow row = new ParsedEmployeeRow();
        row.employeeNumber = parseInt(data[0]);
        row.lastName = clean(data[1]);
        row.firstName = clean(data[2]);
        row.birthDate = clean(data[3]);

        int phoneIndex = findFirstIndex(data, 4, PHONE_PATTERN);
        if (phoneIndex < 0) {
            phoneIndex = Math.min(6, data.length - 1);
        }

        row.address = join(data, 4, Math.max(4, phoneIndex - 1));
        row.phone = safeGet(data, phoneIndex);
        row.sss = safeGet(data, phoneIndex + 1);
        row.philhealth = safeGet(data, phoneIndex + 2);
        row.tin = safeGet(data, phoneIndex + 3);
        row.pagibig = safeGet(data, phoneIndex + 4);

        int cursor = phoneIndex + 5;
        row.status = safeGet(data, cursor);
        cursor++;

        List<String> textTail = new ArrayList<>();
        while (cursor < data.length && !isNumericToken(data[cursor])) {
            textTail.add(clean(data[cursor]));
            cursor++;
        }

        row.position = textTail.isEmpty() ? "" : textTail.get(0);
        row.supervisor = textTail.size() > 1 ? String.join(", ", textTail.subList(1, textTail.size())) : "";

        List<Double> numericTail = new ArrayList<>();
        for (int i = cursor; i < data.length; i++) {
            numericTail.add(parseDouble(data[i]));
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
        return clean(data[index]);
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

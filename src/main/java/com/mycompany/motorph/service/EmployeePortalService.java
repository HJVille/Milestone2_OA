package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.AttendanceDAO;
import com.mycompany.motorph.dao.CsvFilePaths;
import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Attendance;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.PayrollPeriodOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;

public class EmployeePortalService {

    private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final EmployeeDAO employeeDAO;
    private final AttendanceDAO attendanceDAO;
    private final String employeeFilePath;
    private final PayrollComputationService payrollComputationService;
    private final PayrollRecordService payrollRecordService;

    public EmployeePortalService() {
        this(
                new EmployeeDAO(),
                CsvFilePaths.EMPLOYEES.toString(),
                new AttendanceDAO(CsvFilePaths.ATTENDANCE),
                new PayrollComputationService(),
                new PayrollRecordService()
        );
    }

    public EmployeePortalService(EmployeeDAO employeeDAO,
                                 String employeeFilePath,
                                 Path attendancePath,
                                 PayrollComputationService payrollComputationService) {
        this(
                employeeDAO,
                employeeFilePath,
                new AttendanceDAO(attendancePath),
                payrollComputationService,
                new PayrollRecordService()
        );
    }

    public EmployeePortalService(EmployeeDAO employeeDAO,
                                 String employeeFilePath,
                                 Path attendancePath,
                                 PayrollComputationService payrollComputationService,
                                 PayrollRecordService payrollRecordService) {
        this(
                employeeDAO,
                employeeFilePath,
                new AttendanceDAO(attendancePath),
                payrollComputationService,
                payrollRecordService
        );
    }

    public EmployeePortalService(EmployeeDAO employeeDAO,
                                 String employeeFilePath,
                                 AttendanceDAO attendanceDAO,
                                 PayrollComputationService payrollComputationService) {
        this(
                employeeDAO,
                employeeFilePath,
                attendanceDAO,
                payrollComputationService,
                new PayrollRecordService()
        );
    }

    public EmployeePortalService(EmployeeDAO employeeDAO,
                                 String employeeFilePath,
                                 AttendanceDAO attendanceDAO,
                                 PayrollComputationService payrollComputationService,
                                 PayrollRecordService payrollRecordService) {
        this.employeeDAO = Objects.requireNonNull(employeeDAO, "employeeDAO");
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO");
        this.employeeFilePath = Objects.requireNonNull(employeeFilePath, "employeeFilePath");
        this.payrollComputationService = Objects.requireNonNull(payrollComputationService, "payrollComputationService");
        this.payrollRecordService = Objects.requireNonNull(payrollRecordService, "payrollRecordService");
    }

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
        Map<YearMonth, boolean[]> availability = new LinkedHashMap<>();

        for (Map.Entry<YearMonth, List<Attendance>> entry : byMonth.entrySet()) {
            boolean[] flags = availability.computeIfAbsent(entry.getKey(), ignored -> new boolean[2]);
            for (Attendance attendance : entry.getValue()) {
                LocalDate date = parseAttendanceDate(attendance);
                if (date.getDayOfMonth() <= 15) {
                    flags[0] = true;
                } else {
                    flags[1] = true;
                }
            }
        }

        return buildPeriodOptions(availability);
    }

    public List<PayrollPeriodOption> getAvailablePayrollPeriods() {
        Map<YearMonth, boolean[]> availability = new LinkedHashMap<>();

        for (Attendance attendance : attendanceDAO.loadCompletedAttendanceRecords()) {
            LocalDate date = parseAttendanceDate(attendance);
            boolean[] flags = availability.computeIfAbsent(YearMonth.from(date), ignored -> new boolean[2]);
            if (date.getDayOfMonth() <= 15) {
                flags[0] = true;
            } else {
                flags[1] = true;
            }
        }

        return buildPeriodOptions(availability);
    }

    public EmployeePayrollSummary getPayrollSummary(int employeeNumber, String periodKey) {
        Employee employee = getEmployeeByNumber(employeeNumber);
        if (employee == null) {
            return null;
        }

        PayrollPeriodOption selectedPeriod = findPeriod(periodKey, getAvailablePayrollPeriods(employeeNumber));
        if (selectedPeriod == null) {
            return null;
        }

        return payrollComputationService.computeSummary(
                employee,
                selectedPeriod,
                filterAttendance(employeeNumber, selectedPeriod)
        );
    }

    public List<EmployeePayrollSummary> getPayrollHistory(int employeeNumber) {
        Employee employee = getEmployeeByNumber(employeeNumber);
        if (employee == null) {
            return List.of();
        }

        List<EmployeePayrollSummary> history = new ArrayList<>();
        for (PayrollPeriodOption option : getAvailablePayrollPeriods(employeeNumber)) {
            history.add(payrollComputationService.computeSummary(
                    employee,
                    option,
                    filterAttendance(employeeNumber, option)
            ));
        }
        history.sort(summaryComparator());
        return history;
    }

    public List<PayrollPeriodOption> getSavedPayrollPeriods(int employeeNumber) {
        Map<String, PayrollPeriodOption> periods = new LinkedHashMap<>();
        for (String[] row : payrollRecordService.getPayrollHistory(employeeNumber)) {
            if (row.length < 4) {
                continue;
            }
            PayrollPeriodOption period = createPeriodFromStoredRow(row[2], row[3]);
            if (period != null) {
                periods.put(period.getKey(), period);
            }
        }

        List<PayrollPeriodOption> savedPeriods = new ArrayList<>(periods.values());
        savedPeriods.sort(periodComparator());
        return savedPeriods;
    }

    public EmployeePayrollSummary getSavedPayrollSummary(int employeeNumber, String periodKey) {
        for (EmployeePayrollSummary summary : getSavedPayrollHistory(employeeNumber)) {
            if (summary.getPeriod().getKey().equals(periodKey)) {
                return summary;
            }
        }
        return null;
    }

    public List<EmployeePayrollSummary> getSavedPayrollHistory(int employeeNumber) {
        Employee employee = getEmployeeByNumber(employeeNumber);
        if (employee == null) {
            return List.of();
        }

        List<EmployeePayrollSummary> history = new ArrayList<>();
        for (String[] row : payrollRecordService.getPayrollHistory(employeeNumber)) {
            EmployeePayrollSummary summary = createSummaryFromStoredRow(employee, row);
            if (summary != null) {
                history.add(summary);
            }
        }
        history.sort(summaryComparator());
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
            List<Attendance> attendanceRows = filterAttendance(employee.getEmployeeNumber(), selectedPeriod);
            if (attendanceRows.isEmpty()) {
                continue;
            }
            summaries.add(payrollComputationService.computeSummary(
                    employee,
                    selectedPeriod,
                    attendanceRows
            ));
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
        return employeeDAO.loadEmployees(employeeFilePath);
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
        return new ArrayList<>(attendanceDAO.loadCompletedAttendanceRecords(employeeNumber));
    }

    private EmployeePayrollSummary createSummaryFromStoredRow(Employee employee, String[] row) {
        if (employee == null || row == null || row.length < 14) {
            return null;
        }

        PayrollPeriodOption period = createPeriodFromStoredRow(row[2], row[3]);
        if (period == null) {
            return null;
        }

        List<Attendance> attendanceRows = filterAttendance(employee.getEmployeeNumber(), period);
        Set<LocalDate> attendanceDays = new LinkedHashSet<>();
        double attendanceHours = 0.0;
        for (Attendance attendance : attendanceRows) {
            attendanceDays.add(parseAttendanceDate(attendance));
            attendanceHours += attendance.getHoursWorked();
        }

        double basicSalary = parseAmount(row[4]);
        double riceSubsidy = parseAmount(row[5]);
        double phoneAllowance = parseAmount(row[6]);
        double clothingAllowance = parseAmount(row[7]);
        double grossSalary = parseAmount(row[8]);
        double sss = parseAmount(row[9]);
        double philhealth = parseAmount(row[10]);
        double pagibig = parseAmount(row[11]);
        double withholdingTax = parseAmount(row[12]);
        double netSalary = parseAmount(row[13]);
        double totalDeductions = round(sss + philhealth + pagibig + withholdingTax);

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
                attendanceDays.size(),
                round(attendanceHours)
        );
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

    private List<PayrollPeriodOption> buildPeriodOptions(Map<YearMonth, boolean[]> availability) {
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

        periods.sort(periodComparator());
        return periods;
    }

    private PayrollPeriodOption createPeriodFromStoredRow(String storedStart, String storedEnd) {
        try {
            LocalDate start = LocalDate.parse(storedStart.trim());
            LocalDate end = LocalDate.parse(storedEnd.trim());
            YearMonth month = YearMonth.from(start);

            if (start.getDayOfMonth() == 1 && end.equals(month.atEndOfMonth())) {
                return createMonthlyOption(month);
            }

            return createSemiMonthlyOption(month, start.getDayOfMonth(), end.getDayOfMonth());
        } catch (Exception e) {
            return null;
        }
    }

    private Comparator<PayrollPeriodOption> periodComparator() {
        return Comparator.comparing(PayrollPeriodOption::getEndDate).reversed()
                .thenComparing(option -> option.getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0);
    }

    private Comparator<EmployeePayrollSummary> summaryComparator() {
        return Comparator.comparing(
                        EmployeePayrollSummary::getPeriod,
                        Comparator.comparing(PayrollPeriodOption::getEndDate).reversed()
                                .thenComparing(option -> option.getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0)
                );
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

    private double parseAmount(String rawValue) {
        try {
            return round(Double.parseDouble(rawValue.trim()));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}

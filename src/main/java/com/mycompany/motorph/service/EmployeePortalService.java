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

public class EmployeePortalService {

    private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final EmployeeDAO employeeDAO;
    private final AttendanceDAO attendanceDAO;
    private final String employeeFilePath;
    private final PayrollComputationService payrollComputationService;

    public EmployeePortalService() {
        this(
                new EmployeeDAO(),
                CsvFilePaths.EMPLOYEES.toString(),
                new AttendanceDAO(CsvFilePaths.ATTENDANCE),
                new PayrollComputationService()
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
                payrollComputationService
        );
    }

    public EmployeePortalService(EmployeeDAO employeeDAO,
                                 String employeeFilePath,
                                 AttendanceDAO attendanceDAO,
                                 PayrollComputationService payrollComputationService) {
        this.employeeDAO = Objects.requireNonNull(employeeDAO, "employeeDAO");
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO");
        this.employeeFilePath = Objects.requireNonNull(employeeFilePath, "employeeFilePath");
        this.payrollComputationService = Objects.requireNonNull(payrollComputationService, "payrollComputationService");
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
            summaries.add(payrollComputationService.computeSummary(
                    employee,
                    selectedPeriod,
                    filterAttendance(employee.getEmployeeNumber(), selectedPeriod)
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

}

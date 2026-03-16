package com.mycompany.motorph.service;

import com.mycompany.motorph.model.Attendance;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.PagibigDeduction;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.PhilHealthDeduction;
import com.mycompany.motorph.model.SSSDeduction;
import com.mycompany.motorph.model.TaxDeduction;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PayrollComputationService {

    private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final SSSDeduction sssDeduction;
    private final PhilHealthDeduction philHealthDeduction;
    private final PagibigDeduction pagibigDeduction;
    private final TaxDeduction taxDeduction;

    public PayrollComputationService() {
        this(new SSSDeduction(), new PhilHealthDeduction(), new PagibigDeduction(), new TaxDeduction());
    }

    public PayrollComputationService(SSSDeduction sssDeduction,
                                     PhilHealthDeduction philHealthDeduction,
                                     PagibigDeduction pagibigDeduction,
                                     TaxDeduction taxDeduction) {
        this.sssDeduction = Objects.requireNonNull(sssDeduction, "sssDeduction");
        this.philHealthDeduction = Objects.requireNonNull(philHealthDeduction, "philHealthDeduction");
        this.pagibigDeduction = Objects.requireNonNull(pagibigDeduction, "pagibigDeduction");
        this.taxDeduction = Objects.requireNonNull(taxDeduction, "taxDeduction");
    }

    public EmployeePayrollSummary computeSummary(Employee employee,
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
        double fullSss = includeDeductions ? round(sssDeduction.compute(monthlySalary)) : 0.0;
        double fullPhilhealth = includeDeductions ? round(philHealthDeduction.compute(monthlySalary)) : 0.0;
        double fullPagibig = includeDeductions ? round(pagibigDeduction.compute(monthlySalary)) : 0.0;
        double taxableIncome = monthlySalary - fullSss - fullPhilhealth - fullPagibig;
        double fullTax = includeDeductions ? round(taxDeduction.compute(taxableIncome)) : 0.0;

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

    private LocalDate parseAttendanceDate(Attendance attendance) {
        return LocalDate.parse(attendance.getDate(), ATTENDANCE_DATE_FORMAT);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

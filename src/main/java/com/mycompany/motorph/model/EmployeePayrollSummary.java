package com.mycompany.motorph.model;

public class EmployeePayrollSummary {

    private final int employeeNumber;
    private final String employeeName;
    private final PayrollPeriodOption period;
    private final double basicSalary;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;
    private final double grossSalary;
    private final double sss;
    private final double philhealth;
    private final double pagibig;
    private final double withholdingTax;
    private final double totalDeductions;
    private final double netSalary;
    private final int attendanceDays;
    private final double attendanceHours;

    public EmployeePayrollSummary(int employeeNumber,
                                  String employeeName,
                                  PayrollPeriodOption period,
                                  double basicSalary,
                                  double riceSubsidy,
                                  double phoneAllowance,
                                  double clothingAllowance,
                                  double grossSalary,
                                  double sss,
                                  double philhealth,
                                  double pagibig,
                                  double withholdingTax,
                                  double totalDeductions,
                                  double netSalary,
                                  int attendanceDays,
                                  double attendanceHours) {
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.period = period;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSalary = grossSalary;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.withholdingTax = withholdingTax;
        this.totalDeductions = totalDeductions;
        this.netSalary = netSalary;
        this.attendanceDays = attendanceDays;
        this.attendanceHours = attendanceHours;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public PayrollPeriodOption getPeriod() {
        return period;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public double getSss() {
        return sss;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public double getPagibig() {
        return pagibig;
    }

    public double getWithholdingTax() {
        return withholdingTax;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public int getAttendanceDays() {
        return attendanceDays;
    }

    public double getAttendanceHours() {
        return attendanceHours;
    }
}

package com.mycompany.motorph.model;

public class Payslip extends PayrollComponent {

    private final String periodStart;
    private final String periodEnd;

    private final double basicSalary;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;

    private final double grossSalary;
    private final double sss;
    private final double philhealth;
    private final double pagibig;
    private final double tax;
    private final double netSalary;

    public Payslip(
            int employeeNumber,
            String employeeName,
            String periodStart,
            String periodEnd,
            double basicSalary,
            double riceSubsidy,
            double phoneAllowance,
            double clothingAllowance,
            double grossSalary,
            double sss,
            double philhealth,
            double pagibig,
            double tax,
            double netSalary) {

        super(employeeNumber, employeeName);
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSalary = grossSalary;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
        this.netSalary = netSalary;
    }

    public String getPeriodStart() { return periodStart; }
    public String getPeriodEnd() { return periodEnd; }

    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }

    public double getGrossSalary() { return grossSalary; }
    public double getSss() { return sss; }
    public double getPhilhealth() { return philhealth; }
    public double getPagibig() { return pagibig; }
    public double getTax() { return tax; }
    public double getNetSalary() { return netSalary; }

    @Override
    public void display() {
        System.out.println(
                getEmployeeNumber() + " | "
                + getEmployeeName() + " | "
                + periodStart + " to " + periodEnd + " | Net: " + netSalary
        );
    }
}

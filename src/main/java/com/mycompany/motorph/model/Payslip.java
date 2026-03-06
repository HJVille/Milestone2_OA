/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class Payslip {

    private int employeeNumber;
    private String employeeName;

    private String periodStart;
    private String periodEnd;

    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;

    private double grossSalary;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;
    private double netSalary;

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

        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;

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

    public int getEmployeeNumber() { return employeeNumber; }
    public String getEmployeeName() { return employeeName; }

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
}
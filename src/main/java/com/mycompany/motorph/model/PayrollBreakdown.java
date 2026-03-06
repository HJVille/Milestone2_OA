/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class PayrollBreakdown {

    private double grossSalary;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;
    private double netSalary;

    public PayrollBreakdown(double grossSalary,
                            double sss,
                            double philhealth,
                            double pagibig,
                            double tax,
                            double netSalary) {

        this.grossSalary = grossSalary;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
        this.netSalary = netSalary;
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

    public double getTax() {
        return tax;
    }

    public double getNetSalary() {
        return netSalary;
    }
}
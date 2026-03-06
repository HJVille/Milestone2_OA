package com.mycompany.motorph.service;

import com.mycompany.motorph.model.Employee;

public class SalaryCalculator implements PayrollCalculator {

    // =====================================================
    // GROSS SALARY
    // =====================================================

    @Override
    public double computeGrossSalary(Employee emp, double hoursWorked, String payrollType) {

        double base;

        if (payrollType.equalsIgnoreCase("SEMI")) {
            base = emp.getGrossSemiMonthlyRate();
        } 
        else {
            base = emp.getBasicSalary();
        }

        double allowances = computeAllowances(emp, payrollType);

        return round(base + allowances);
    }

    // =====================================================
    // ALLOWANCES
    // =====================================================

    @Override
    public double computeAllowances(Employee emp, String payrollType) {

        double totalAllowances =
                emp.getRiceSubsidy()
                + emp.getPhoneAllowance()
                + emp.getClothingAllowance();

        if (payrollType.equalsIgnoreCase("SEMI")) {
            return totalAllowances / 2;
        }

        return totalAllowances;
    }

    // =====================================================
    // NET SALARY
    // =====================================================

    @Override
    public double computeNetSalary(double gross,
                                   double sss,
                                   double philhealth,
                                   double pagibig,
                                   double tax) {

        double net = gross - (sss + philhealth + pagibig + tax);

        return round(net);
    }

    // =====================================================
    // ROUNDING
    // =====================================================

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;

    }
}
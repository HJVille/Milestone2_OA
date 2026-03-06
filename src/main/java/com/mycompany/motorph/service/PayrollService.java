/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.PayrollBreakdown;

import com.mycompany.motorph.model.AbstractDeduction;
import com.mycompany.motorph.model.SSSDeduction;
import com.mycompany.motorph.model.PhilHealthDeduction;
import com.mycompany.motorph.model.PagibigDeduction;
import com.mycompany.motorph.model.TaxDeduction;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class PayrollService {

    private SalaryCalculator calculator = new SalaryCalculator();

    // =====================================================
    // PROCESS PAYROLL
    // =====================================================

    public Payslip processPayroll(Employee employee) {

        double hoursWorked =
                getHoursWorkedFromAttendance(employee.getEmployeeNumber());

        PayrollBreakdown breakdown =
                computePayroll(employee, hoursWorked);

        String[] period =
                getPayrollPeriod();

        return generatePayslip(
                employee,
                breakdown,
                period[0],
                period[1]
        );
    }

    // =====================================================
    // SEMI MONTHLY PERIOD
    // =====================================================

    private String[] getPayrollPeriod() {

        LocalDate today = LocalDate.now();

        LocalDate start;
        LocalDate end;

        if (today.getDayOfMonth() <= 15) {

            start = today.withDayOfMonth(1);
            end = today.withDayOfMonth(15);

        } else {

            start = today.withDayOfMonth(16);
            end = today.withDayOfMonth(today.lengthOfMonth());

        }

        return new String[]{
                start.toString(),
                end.toString()
        };
    }

    // =====================================================
    // READ ATTENDANCE CSV
    // =====================================================

    private double getHoursWorkedFromAttendance(int employeeNumber) {

        double totalHours = 0;

        DateTimeFormatter timeFormat =
                DateTimeFormatter.ofPattern("H:mm");

        try {

            BufferedReader br =
                    new BufferedReader(new FileReader("attendance.csv"));

            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 6) continue;

                int empNum = Integer.parseInt(data[0].trim());

                if (empNum != employeeNumber) continue;

                LocalTime logIn =
                        LocalTime.parse(data[4].trim(), timeFormat);

                LocalTime logOut =
                        LocalTime.parse(data[5].trim(), timeFormat);

                Duration duration =
                        Duration.between(logIn, logOut);

                double hours =
                        duration.toMinutes() / 60.0;

                totalHours += hours;
            }

            br.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return totalHours;
    }

    // =====================================================
    // PAYROLL COMPUTATION
    // =====================================================

    private PayrollBreakdown computePayroll(Employee employee,
                                            double hoursWorked) {

        double gross = calculator.computeGrossSalary(
                employee,
                hoursWorked,
                "SEMI"
        );

        double monthlySalary = employee.getBasicSalary();

        List<AbstractDeduction> deductions = new ArrayList<>();

        deductions.add(new SSSDeduction());
        deductions.add(new PhilHealthDeduction());
        deductions.add(new PagibigDeduction());

        double monthlySSS = 0;
        double monthlyPhilhealth = 0;
        double monthlyPagibig = 0;

        for (AbstractDeduction d : deductions) {

            double value = d.compute(monthlySalary);

            if (d instanceof SSSDeduction) {
                monthlySSS = value;
            }

            else if (d instanceof PhilHealthDeduction) {
                monthlyPhilhealth = value;
            }

            else if (d instanceof PagibigDeduction) {
                monthlyPagibig = value;
            }
        }

        double sss = monthlySSS / 2;
        double philhealth = monthlyPhilhealth / 2;
        double pagibig = monthlyPagibig / 2;

        double monthlyTaxable =
                monthlySalary
                - monthlySSS
                - monthlyPhilhealth
                - monthlyPagibig;

        TaxDeduction taxDeduction = new TaxDeduction();

        double monthlyTax = taxDeduction.compute(monthlyTaxable);

        double tax = monthlyTax / 2;

        double net = calculator.computeNetSalary(
                gross,
                sss,
                philhealth,
                pagibig,
                tax
        );

        return new PayrollBreakdown(
                gross,
                sss,
                philhealth,
                pagibig,
                tax,
                net
        );
    }

    // =====================================================
    // GENERATE PAYSLIP
    // =====================================================

    private Payslip generatePayslip(Employee employee,
                                    PayrollBreakdown breakdown,
                                    String startDate,
                                    String endDate) {

        return new Payslip(
                employee.getEmployeeNumber(),
                employee.getEmployeeName(),
                startDate,
                endDate,
                employee.getBasicSalary(),
                employee.getRiceSubsidy(),
                employee.getPhoneAllowance(),
                employee.getClothingAllowance(),
                breakdown.getGrossSalary(),
                breakdown.getSss(),
                breakdown.getPhilhealth(),
                breakdown.getPagibig(),
                breakdown.getTax(),
                breakdown.getNetSalary()
        );
    }
}
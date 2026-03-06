/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import com.mycompany.motorph.model.Employee;

public interface PayrollCalculator {

    double computeGrossSalary(Employee emp, double hoursWorked, String payrollType);

    double computeAllowances(Employee emp, String payrollType);

    double computeNetSalary(double gross,
                            double sss,
                            double philhealth,
                            double pagibig,
                            double tax);
}
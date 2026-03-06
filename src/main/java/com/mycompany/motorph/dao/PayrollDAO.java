/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Payslip;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    private final String filePath = "payroll_records.csv";

    public void savePayroll(Payslip payslip) {

        try {

            File file = new File(filePath);
            boolean fileExists = file.exists();

            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));

            if (!fileExists) {

                bw.write("employeeNumber,employeeName,gross,sss,philhealth,pagibig,tax,net");
                bw.newLine();

            }

            bw.write(
                    payslip.getEmployeeNumber() + "," +
                    payslip.getEmployeeName() + "," +
                    payslip.getGrossSalary() + "," +
                    payslip.getSss() + "," +
                    payslip.getPhilhealth() + "," +
                    payslip.getPagibig() + "," +
                    payslip.getTax() + "," +
                    payslip.getNetSalary()
            );

            bw.newLine();
            bw.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public List<String[]> getPayrollHistory(int employeeNumber) {

        List<String[]> history = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            br.readLine(); // skip header row

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                int empNum = Integer.parseInt(data[0]);

                if (empNum == employeeNumber) {

                    history.add(data);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return history;

    }

}
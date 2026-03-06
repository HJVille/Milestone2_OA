/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class PayrollReportService {

    private final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    public void exportReport(){

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            FileWriter writer = new FileWriter("MotorPH_Payroll_Report.txt");

            String line;

            br.readLine();

            writer.write("========================================\n");
            writer.write("           MOTORPH PAYROLL REPORT\n");
            writer.write("========================================\n\n");

            double totalGross = 0;
            double totalNet = 0;
            double totalDeductions = 0;

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                String empNum = data[0];
                String name = data[1];

                String start = data[2];
                String end = data[3];

                double gross = Double.parseDouble(data[8]);

                double sss = Double.parseDouble(data[9]);
                double phil = Double.parseDouble(data[10]);
                double pagibig = Double.parseDouble(data[11]);
                double tax = Double.parseDouble(data[12]);

                double net = Double.parseDouble(data[13]);

                double deductions = sss + phil + pagibig + tax;

                writer.write("Employee #: " + empNum + "\n");
                writer.write("Employee: " + name + "\n");
                writer.write("Period: " + start + " to " + end + "\n");

                writer.write("Gross: " + peso.format(gross) + "\n");
                writer.write("Deductions: " + peso.format(deductions) + "\n");
                writer.write("Net: " + peso.format(net) + "\n");

                writer.write("----------------------------------------\n");

                totalGross += gross;
                totalNet += net;
                totalDeductions += deductions;

            }

            writer.write("\n=========== PAYROLL SUMMARY ===========\n");

            writer.write("Total Gross Payroll: " + peso.format(totalGross) + "\n");
            writer.write("Total Deductions: " + peso.format(totalDeductions) + "\n");
            writer.write("Total Net Payroll: " + peso.format(totalNet) + "\n");

            writer.close();
            br.close();

        }

        catch(Exception e){

            e.printStackTrace();

        }

    }

}
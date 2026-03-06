/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

public class HRPayslipFrame extends JFrame {

    private final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    public HRPayslipFrame(String empNum,String name,String periodStart,String periodEnd){

        setTitle("Payslip Breakdown");
        setSize(420,420);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();

        area.setFont(new Font("Monospaced",Font.PLAIN,14));
        area.setEditable(false);

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            String line;

            br.readLine();

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                if(data[0].equals(empNum)
                        && data[2].equals(periodStart)
                        && data[3].equals(periodEnd)){

                    double basic = Double.parseDouble(data[4]);
                    double rice = Double.parseDouble(data[5]);
                    double phone = Double.parseDouble(data[6]);
                    double clothing = Double.parseDouble(data[7]);

                    double gross = Double.parseDouble(data[8]);

                    double sss = Double.parseDouble(data[9]);
                    double phil = Double.parseDouble(data[10]);
                    double pagibig = Double.parseDouble(data[11]);
                    double tax = Double.parseDouble(data[12]);

                    double net = Double.parseDouble(data[13]);

                    area.append("=================================\n");
                    area.append("           MOTORPH PAYSLIP\n");
                    area.append("=================================\n\n");

                    area.append("Employee: "+name+"\n");
                    area.append("Employee #: "+empNum+"\n");

                    area.append("Period: "+periodStart+" to "+periodEnd+"\n\n");

                    area.append("----------- EARNINGS ------------\n");

                    area.append(String.format("%-20s %10s\n",
                            "Basic Salary",peso.format(basic/2)));

                    area.append(String.format("%-20s %10s\n",
                            "Rice Subsidy",peso.format(rice/2)));

                    area.append(String.format("%-20s %10s\n",
                            "Phone Allowance",peso.format(phone/2)));

                    area.append(String.format("%-20s %10s\n",
                            "Clothing Allowance",peso.format(clothing/2)));

                    area.append("\n----------- GROSS PAY ------------\n");

                    area.append(String.format("%-20s %10s\n",
                            "Gross Salary",peso.format(gross)));

                    area.append("\n----------- DEDUCTIONS -----------\n");

                    area.append(String.format("%-20s %10s\n",
                            "SSS",peso.format(sss)));

                    area.append(String.format("%-20s %10s\n",
                            "PhilHealth",peso.format(phil)));

                    area.append(String.format("%-20s %10s\n",
                            "PagIBIG",peso.format(pagibig)));

                    area.append(String.format("%-20s %10s\n",
                            "Tax",peso.format(tax)));

                    area.append("\n----------------------------------\n");

                    area.append(String.format("%-20s %10s\n",
                            "NET PAY",peso.format(net)));

                    area.append("----------------------------------\n");

                    break;

                }

            }

            br.close();

        }catch(Exception e){

            area.append("Payslip not found.");

        }

        add(new JScrollPane(area));

    }

}
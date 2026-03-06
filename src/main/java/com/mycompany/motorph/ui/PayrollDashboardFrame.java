/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.PayrollService;
import com.mycompany.motorph.service.PayrollReportService;
import com.mycompany.motorph.service.CSVExportService;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class PayrollDashboardFrame extends JFrame {

    private User loggedUser;

    public PayrollDashboardFrame(User user) {

        this.loggedUser = user;

        setTitle("MotorPH Payroll Dashboard");
        setSize(420,420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7,1,10,10));

        JLabel title = new JLabel("MotorPH Payroll Dashboard", SwingConstants.CENTER);

        JButton processPayrollBtn = new JButton("Process Payroll");
        JButton viewRecordsBtn = new JButton("View Payroll Records");
        JButton analyticsBtn = new JButton("Payroll Analytics");
        JButton exportReportBtn = new JButton("Export Payroll Report");
        JButton exportCSVBtn = new JButton("Export Payroll (CSV)");
        JButton logoutBtn = new JButton("Logout");

        panel.add(title);
        panel.add(processPayrollBtn);
        panel.add(viewRecordsBtn);
        panel.add(analyticsBtn);
        panel.add(exportReportBtn);
        panel.add(exportCSVBtn);
        panel.add(logoutBtn);

        add(panel);

        processPayrollBtn.addActionListener(e -> processPayroll());

        viewRecordsBtn.addActionListener(e ->
                new PayrollRecordsFrame().setVisible(true));

        analyticsBtn.addActionListener(e ->
                new PayrollAnalyticsFrame().setVisible(true));

        exportReportBtn.addActionListener(e -> exportReport());

        exportCSVBtn.addActionListener(e -> exportCSV());

        logoutBtn.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

    }

    private void processPayroll() {

        try {

            EmployeeDAO dao = new EmployeeDAO();

            List<Employee> employees = dao.loadEmployees("employees.csv");

            if(employees.isEmpty()){

                JOptionPane.showMessageDialog(this,
                        "No employees loaded.");

                return;

            }

            PayrollService payrollService = new PayrollService();

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter("payroll_records.csv"));

            writer.write("EmployeeNumber,EmployeeName,PeriodStart,PeriodEnd,BasicSalary,RiceSubsidy,PhoneAllowance,ClothingAllowance,GrossSalary,SSS,PhilHealth,Pagibig,Tax,NetSalary");

            for (Employee emp : employees) {

                Payslip payslip = payrollService.processPayroll(emp);

                writer.write("\n"
                        + payslip.getEmployeeNumber() + ","
                        + payslip.getEmployeeName() + ","
                        + payslip.getPeriodStart() + ","
                        + payslip.getPeriodEnd() + ","
                        + payslip.getBasicSalary() + ","
                        + payslip.getRiceSubsidy() + ","
                        + payslip.getPhoneAllowance() + ","
                        + payslip.getClothingAllowance() + ","
                        + payslip.getGrossSalary() + ","
                        + payslip.getSss() + ","
                        + payslip.getPhilhealth() + ","
                        + payslip.getPagibig() + ","
                        + payslip.getTax() + ","
                        + payslip.getNetSalary());

            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Payroll processed successfully.");

        }

        catch(Exception ex){

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Payroll processing failed.");

        }

    }

    private void exportReport(){

        PayrollReportService reportService = new PayrollReportService();

        reportService.exportReport();

        JOptionPane.showMessageDialog(this,
                "Payroll report exported.\nMotorPH_Payroll_Report.txt");

    }

    private void exportCSV(){

        CSVExportService csv = new CSVExportService();

        csv.exportPayroll();

        JOptionPane.showMessageDialog(this,
                "CSV exported successfully.\nMotorPH_Payroll_Export.csv");

    }

}
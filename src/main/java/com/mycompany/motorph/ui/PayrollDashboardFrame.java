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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class PayrollDashboardFrame extends JFrame {

    private User loggedUser;

    private final Color PRIMARY_BLUE = new Color(44,74,115);
    private final Color BACKGROUND = new Color(244,247,251);
    private final Color PANEL_BORDER = new Color(217,226,236);
    private final Color TEXT_COLOR = new Color(36,52,71);

    public PayrollDashboardFrame(User user) {

        this.loggedUser = user;

        setTitle("MotorPH Payroll Dashboard");
        setSize(900,550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(new EmptyBorder(20,30,20,30));

        JLabel title = new JLabel("MotorPH Payroll Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel dashboard = new JPanel(new GridLayout(2,3,25,25));
        dashboard.setBorder(new EmptyBorder(40,40,40,40));
        dashboard.setBackground(BACKGROUND);

        JButton processPayrollBtn = createCardButton(
                "Process Payroll",
                "Run payroll computation"
        );

        JButton viewRecordsBtn = createCardButton(
                "View Payroll Records",
                "Browse payroll history"
        );

        JButton analyticsBtn = createCardButton(
                "Payroll Analytics",
                "View payroll statistics"
        );

        JButton exportReportBtn = createCardButton(
                "Export Payroll Report",
                "Generate payroll summary"
        );

        JButton exportCSVBtn = createCardButton(
                "Export CSV",
                "Export payroll dataset"
        );

        JButton logoutBtn = createCardButton(
                "Logout",
                "Return to login"
        );

        dashboard.add(processPayrollBtn);
        dashboard.add(viewRecordsBtn);
        dashboard.add(analyticsBtn);
        dashboard.add(exportReportBtn);
        dashboard.add(exportCSVBtn);
        dashboard.add(logoutBtn);

        add(dashboard, BorderLayout.CENTER);

        processPayrollBtn.addActionListener(e -> processPayroll());
        viewRecordsBtn.addActionListener(e -> new PayrollRecordsFrame().setVisible(true));
        analyticsBtn.addActionListener(e -> new PayrollAnalyticsFrame().setVisible(true));
        exportReportBtn.addActionListener(e -> exportReport());
        exportCSVBtn.addActionListener(e -> exportCSV());

        logoutBtn.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

    }

    private JButton createCardButton(String title, String subtitle){

        JButton button = new JButton(
                "<html><center>"
                        + "<div style='font-size:16px;font-weight:bold;'>"
                        + title
                        + "</div>"
                        + "<div style='font-size:11px;'>"
                        + subtitle
                        + "</div>"
                        + "</center></html>"
        );

        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PANEL_BORDER),
                new EmptyBorder(25,20,25,20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(236,241,248));
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(Color.WHITE);
            }

        });

        return button;

    }

    private void processPayroll() {

        try {

            EmployeeDAO dao = new EmployeeDAO();
            List<Employee> employees = dao.loadEmployees("employees.csv");

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

            JOptionPane.showMessageDialog(this,
                    "Payroll processing failed.");

        }

    }

    private void exportReport(){

        PayrollReportService reportService = new PayrollReportService();
        reportService.exportReport();

        JOptionPane.showMessageDialog(this,
                "Payroll report exported.");

    }

    private void exportCSV(){

        CSVExportService csv = new CSVExportService();
        csv.exportPayroll();

        JOptionPane.showMessageDialog(this,
                "CSV exported successfully.");

    }

}
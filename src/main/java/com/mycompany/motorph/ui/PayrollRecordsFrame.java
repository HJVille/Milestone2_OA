/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

public class PayrollRecordsFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;
    private JComboBox<String> monthFilter;

    private JLabel totalEmployeesLabel;
    private JLabel totalGrossLabel;
    private JLabel totalDeductionsLabel;
    private JLabel totalNetLabel;

    private final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    public PayrollRecordsFrame(){

        setTitle("MotorPH Payroll Records");
        setSize(1200,520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====

        JPanel topPanel = new JPanel();

        searchField = new JTextField(15);

        String[] months = {
                "All",
                "01","02","03","04","05","06",
                "07","08","09","10","11","12"
        };

        monthFilter = new JComboBox<>(months);

        JButton filterBtn = new JButton("Filter");

        topPanel.add(new JLabel("Search Employee:"));
        topPanel.add(searchField);

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthFilter);

        topPanel.add(filterBtn);

        add(topPanel,BorderLayout.NORTH);

        // ===== TABLE =====

        model = new DefaultTableModel();

        model.addColumn("Employee #");
        model.addColumn("Employee Name");
        model.addColumn("Period Start");
        model.addColumn("Period End");

        model.addColumn("Basic Salary");
        model.addColumn("Rice Subsidy");
        model.addColumn("Phone Allowance");
        model.addColumn("Clothing Allowance");

        model.addColumn("Gross Salary");

        model.addColumn("SSS");
        model.addColumn("PhilHealth");
        model.addColumn("Pagibig");
        model.addColumn("Tax");

        model.addColumn("Net Salary");

        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        add(scroll,BorderLayout.CENTER);

        // ===== SUMMARY =====

        JPanel summaryPanel = new JPanel(new GridLayout(4,1));

        totalEmployeesLabel = new JLabel("Total Employees: 0");
        totalGrossLabel = new JLabel("Total Gross Payroll: ₱0.00");
        totalDeductionsLabel = new JLabel("Total Deductions: ₱0.00");
        totalNetLabel = new JLabel("Total Net Payroll: ₱0.00");

        summaryPanel.add(totalEmployeesLabel);
        summaryPanel.add(totalGrossLabel);
        summaryPanel.add(totalDeductionsLabel);
        summaryPanel.add(totalNetLabel);

        add(summaryPanel,BorderLayout.SOUTH);

        // ===== EVENTS =====

        filterBtn.addActionListener(e -> loadPayrollRecords());

        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e){

                if(e.getClickCount() == 2){

                    openSelectedPayslip();

                }

            }

        });

        loadPayrollRecords();
    }

    private void loadPayrollRecords(){

        model.setRowCount(0);

        String search = searchField.getText().toLowerCase();
        String month = monthFilter.getSelectedItem().toString();

        int employees = 0;

        double totalGross = 0;
        double totalNet = 0;
        double totalDeductions = 0;

        try{

            BufferedReader br =
                    new BufferedReader(new FileReader("payroll_records.csv"));

            String line;

            br.readLine();

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                String empNum = data[0];
                String name = data[1];

                String start = data[2];
                String end = data[3];

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

                double deductions = sss + phil + pagibig + tax;

                boolean matchSearch =
                        empNum.contains(search) ||
                        name.toLowerCase().contains(search);

                boolean matchMonth = month.equals("All")
                        || start.substring(5,7).equals(month);

                if(matchSearch && matchMonth){

                    model.addRow(new Object[]{
                            empNum,
                            name,
                            start,
                            end,
                            peso.format(basic),
                            peso.format(rice),
                            peso.format(phone),
                            peso.format(clothing),
                            peso.format(gross),
                            peso.format(sss),
                            peso.format(phil),
                            peso.format(pagibig),
                            peso.format(tax),
                            peso.format(net)
                    });

                    employees++;

                    totalGross += gross;
                    totalNet += net;
                    totalDeductions += deductions;

                }

            }

            br.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "No payroll records found.");

        }

        totalEmployeesLabel.setText("Total Employees Processed: " + employees);
        totalGrossLabel.setText("Total Gross Payroll: " + peso.format(totalGross));
        totalDeductionsLabel.setText("Total Deductions: " + peso.format(totalDeductions));
        totalNetLabel.setText("Total Net Payroll: " + peso.format(totalNet));
    }

    private void openSelectedPayslip(){

        int row = table.getSelectedRow();

        if(row == -1){

            JOptionPane.showMessageDialog(this,
                    "Select a payroll record first.");

            return;

        }

        String empNum = table.getValueAt(row,0).toString();
        String name = table.getValueAt(row,1).toString();

        String start = table.getValueAt(row,2).toString();
        String end = table.getValueAt(row,3).toString();

        new HRPayslipFrame(empNum,name,start,end).setVisible(true);

    }

}
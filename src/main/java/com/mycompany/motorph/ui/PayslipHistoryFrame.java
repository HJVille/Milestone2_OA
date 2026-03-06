/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

public class PayslipHistoryFrame extends JFrame {

    private final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    private JTable table;
    private DefaultTableModel model;

    private JLabel totalLabel;

    private User user;

    public PayslipHistoryFrame(User user){

        this.user = user;

        setTitle("Payslip History");
        setSize(650,420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====

        JPanel topPanel = new JPanel();

        JLabel durationLabel = new JLabel("Select Duration:");

        String[] durationOptions = {
                "All Records",
                "Last 1 Month",
                "Last 3 Months",
                "Last 6 Months"
        };

        JComboBox<String> durationBox = new JComboBox<>(durationOptions);

        JButton loadBtn = new JButton("Load");

        topPanel.add(durationLabel);
        topPanel.add(durationBox);
        topPanel.add(loadBtn);

        add(topPanel,BorderLayout.NORTH);

        // ===== TABLE =====

        model = new DefaultTableModel();

        model.addColumn("Period Start");
        model.addColumn("Period End");
        model.addColumn("Gross Salary");
        model.addColumn("Net Salary");

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane,BorderLayout.CENTER);

        // ===== BOTTOM =====

        JPanel bottom = new JPanel();

        totalLabel = new JLabel("Total Net: ₱0.00");

        JButton viewBtn = new JButton("View Payslip");

        bottom.add(totalLabel);
        bottom.add(viewBtn);

        add(bottom,BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====

        loadBtn.addActionListener(e -> loadHistory());

        viewBtn.addActionListener(e -> openSelectedPayslip());

        loadHistory();
    }

    private void loadHistory(){

        model.setRowCount(0);

        double total = 0;

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            String line;

            br.readLine();

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                int empNum = Integer.parseInt(data[0]);

                if(empNum == user.getEmployeeNumber()){

                    String periodStart = data[2];
                    String periodEnd = data[3];

                    double gross = Double.parseDouble(data[8]);
                    double net = Double.parseDouble(data[13]);

                    model.addRow(new Object[]{
                            periodStart,
                            periodEnd,
                            peso.format(gross),
                            peso.format(net)
                    });

                    total += net;

                }

            }

            br.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "No payroll history found.");

        }

        totalLabel.setText("Total Net: " + peso.format(total));
    }

    private void openSelectedPayslip(){

        int row = table.getSelectedRow();

        if(row == -1){

            JOptionPane.showMessageDialog(this,
                    "Select a payslip first.");

            return;
        }

        String periodStart = model.getValueAt(row,0).toString();
        String periodEnd = model.getValueAt(row,1).toString();

        new PayslipFrame(user,periodStart,periodEnd).setVisible(true);
    }

}
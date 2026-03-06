/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import javax.swing.*;
import java.awt.*;

public class HRDashboardFrame extends JFrame {

    public HRDashboardFrame() {

        setTitle("MotorPH - HR Dashboard");
        setSize(600,350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ===== TITLE =====
        JLabel title = new JLabel("HR Management Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        mainPanel.add(title, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(3,2,20,20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));

        JButton viewEmployeesBtn = new JButton("View Employees");
        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton updateEmployeeBtn = new JButton("Update Employee");
        JButton deleteEmployeeBtn = new JButton("Delete Employee");
        JButton approveLeaveBtn = new JButton("Approve Leave");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewEmployeesBtn);
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(updateEmployeeBtn);
        buttonPanel.add(deleteEmployeeBtn);
        buttonPanel.add(approveLeaveBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // ===== BUTTON ACTIONS =====

        viewEmployeesBtn.addActionListener(e ->
                new EmployeeListFrame().setVisible(true)
        );

        addEmployeeBtn.addActionListener(e ->
                new AddEmployeeFrame().setVisible(true)
        );

        updateEmployeeBtn.addActionListener(e ->
                new UpdateEmployeeFrame().setVisible(true)
        );

        deleteEmployeeBtn.addActionListener(e ->
                new DeleteEmployeeFrame().setVisible(true)
        );

        approveLeaveBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Leave approval module will be added next.")
        );

        logoutBtn.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

    }

}
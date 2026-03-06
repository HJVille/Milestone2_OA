/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Employee> employees;

    public EmployeeListFrame() {

        setTitle("MotorPH - Employee List");
        setSize(850,450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== TABLE =====
        tableModel = new DefaultTableModel();

        tableModel.addColumn("Employee #");
        tableModel.addColumn("Full Name");
        tableModel.addColumn("Position");
        tableModel.addColumn("Status");
        tableModel.addColumn("Basic Salary");

        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel();

        JLabel searchLabel = new JLabel("Search Employee:");

        searchField = new JTextField(20);

        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel();

        JButton closeButton = new JButton("Close");

        bottomPanel.add(closeButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        EmployeeDAO dao = new EmployeeDAO();
        employees = dao.loadEmployees("employees.csv");

        loadEmployees();

        // ===== LIVE SEARCH =====
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                filterEmployees();
            }

            public void removeUpdate(DocumentEvent e) {
                filterEmployees();
            }

            public void changedUpdate(DocumentEvent e) {
                filterEmployees();
            }

        });

        refreshButton.addActionListener(e -> {

            EmployeeDAO daoRefresh = new EmployeeDAO();
            employees = daoRefresh.loadEmployees("employees.csv");

            searchField.setText("");
            loadEmployees();

        });

        closeButton.addActionListener(e -> dispose());

    }

    private void loadEmployees() {

        tableModel.setRowCount(0);

        for (Employee emp : employees) {

            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getFirstName() + " " + emp.getLastName(),
                    emp.getPosition(),
                    emp.getStatus(),
                    emp.getBasicSalary()
            });

        }

    }

    private void filterEmployees() {

        String keyword = searchField.getText().toLowerCase();

        tableModel.setRowCount(0);

        for (Employee emp : employees) {

            String id = String.valueOf(emp.getEmployeeNumber());
            String name = (emp.getFirstName() + " " + emp.getLastName()).toLowerCase();

            if (id.contains(keyword) || name.contains(keyword)) {

                tableModel.addRow(new Object[]{
                        emp.getEmployeeNumber(),
                        emp.getFirstName() + " " + emp.getLastName(),
                        emp.getPosition(),
                        emp.getStatus(),
                        emp.getBasicSalary()
                });

            }

        }

    }

}
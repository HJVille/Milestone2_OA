/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeleteEmployeeFrame extends JFrame {

    private JTextField employeeIdField;

    public DeleteEmployeeFrame() {

        setTitle("Delete Employee");
        setSize(350,150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2,2,10,10));

        panel.add(new JLabel("Employee Number:"));
        employeeIdField = new JTextField();
        panel.add(employeeIdField);

        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        panel.add(deleteButton);
        panel.add(cancelButton);

        add(panel);

        deleteButton.addActionListener(e -> deleteEmployee());
        cancelButton.addActionListener(e -> dispose());

    }

    private void deleteEmployee() {

        try {

            int id = Integer.parseInt(employeeIdField.getText());

            EmployeeDAO dao = new EmployeeDAO();
            List<Employee> employees = dao.loadEmployees("employees.csv");

            dao.deleteEmployee(id, employees, "employees.csv");

            JOptionPane.showMessageDialog(this,"Employee deleted successfully");
            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,"Invalid employee number");

        }

    }

}
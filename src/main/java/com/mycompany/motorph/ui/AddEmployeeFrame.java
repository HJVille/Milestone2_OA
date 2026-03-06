/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;

import javax.swing.*;
import java.awt.*;

public class AddEmployeeFrame extends JFrame {

    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField positionField;
    private JTextField salaryField;

    public AddEmployeeFrame() {

        setTitle("Add Employee");
        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6,2,10,10));

        panel.add(new JLabel("Employee Number:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Position:"));
        positionField = new JTextField();
        panel.add(positionField);

        panel.add(new JLabel("Basic Salary:"));
        salaryField = new JTextField();
        panel.add(salaryField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        panel.add(saveButton);
        panel.add(cancelButton);

        add(panel);

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

    }

    private void saveEmployee() {

        try {

            int id = Integer.parseInt(idField.getText());
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();
            String position = positionField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText());

            Employee emp = new Employee(
                    id,
                    last,
                    first,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "Regular",
                    position,
                    "",
                    salary,
                    0,
                    0,
                    0,
                    salary / 2,
                    salary / 160
            );

            EmployeeDAO dao = new EmployeeDAO();
            dao.addEmployee(emp, "employees.csv");

            JOptionPane.showMessageDialog(this,"Employee added successfully");
            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,"Invalid input");

        }

    }

}
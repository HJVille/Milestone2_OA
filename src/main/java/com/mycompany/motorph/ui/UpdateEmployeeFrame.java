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

public class UpdateEmployeeFrame extends JFrame {

    private JTextField empNumberField;
    private JTextField positionField;
    private JTextField salaryField;

    public UpdateEmployeeFrame() {

        setTitle("Update Employee");
        setSize(400,260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));

        JLabel empLabel = new JLabel("Employee Number:");
        JLabel posLabel = new JLabel("New Position:");
        JLabel salLabel = new JLabel("New Salary:");

        empNumberField = new JTextField();
        positionField = new JTextField();
        salaryField = new JTextField();

        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(empLabel);
        panel.add(empNumberField);

        panel.add(posLabel);
        panel.add(positionField);

        panel.add(salLabel);
        panel.add(salaryField);

        panel.add(updateBtn);
        panel.add(cancelBtn);

        add(panel);

        updateBtn.addActionListener(e -> updateEmployee());
        cancelBtn.addActionListener(e -> dispose());

    }

    private void updateEmployee() {

        try {

            int empNumber = Integer.parseInt(empNumberField.getText().trim());
            String newPosition = positionField.getText().trim();
            double newSalary = Double.parseDouble(salaryField.getText().trim());

            String filePath = "employees.csv";

            EmployeeDAO dao = new EmployeeDAO();
            List<Employee> employees = dao.loadEmployees(filePath);

            Employee target = null;

            for (Employee emp : employees) {

                if (emp.getEmployeeNumber() == empNumber) {
                    target = emp;
                    break;
                }

            }

            if (target == null) {

                JOptionPane.showMessageDialog(this,"Employee not found");
                return;

            }

            Employee updatedEmployee = new Employee(
                    target.getEmployeeNumber(),
                    target.getLastName(),
                    target.getFirstName(),
                    target.getBirthDate(),
                    target.getAddress(),
                    target.getPhone(),
                    target.getSss(),
                    target.getPhilhealth(),
                    target.getTin(),
                    target.getPagibig(),
                    target.getStatus(),
                    newPosition,
                    target.getSupervisor(),
                    newSalary,
                    target.getRiceSubsidy(),
                    target.getPhoneAllowance(),
                    target.getClothingAllowance(),
                    newSalary / 2,
                    newSalary / 160
            );

            dao.updateEmployee(empNumber, updatedEmployee, employees, filePath);

            JOptionPane.showMessageDialog(this,"Employee updated successfully");

            dispose();

        }
        catch(Exception e){

            JOptionPane.showMessageDialog(this,"Invalid input");

        }

    }

}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewProfileFrame extends JFrame {

    public ViewProfileFrame(User user) {

        setTitle("Employee Profile");
        setSize(420,350);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> employees = dao.loadEmployees("employees.csv");

        for(Employee emp : employees){

            if(emp.getEmployeeNumber() == user.getEmployeeNumber()){

                area.append("Employee Number: " + emp.getEmployeeNumber() + "\n");
                area.append("Name: " + emp.getEmployeeName() + "\n");
                area.append("Position: " + emp.getPosition() + "\n");
                area.append("Status: " + emp.getStatus() + "\n");
                area.append("Basic Salary: " + emp.getBasicSalary() + "\n");
                area.append("Address: " + emp.getAddress() + "\n");
                area.append("Phone: " + emp.getPhone() + "\n");
                area.append("Supervisor: " + emp.getSupervisor() + "\n");

                break;

            }

        }

        add(new JScrollPane(area));

    }

}
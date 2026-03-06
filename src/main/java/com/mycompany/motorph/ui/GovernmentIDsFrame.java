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

public class GovernmentIDsFrame extends JFrame {

    public GovernmentIDsFrame(User user){

        setTitle("Government IDs");
        setSize(350,250);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> employees = dao.loadEmployees("employees.csv");

        for(Employee emp : employees){

            if(emp.getEmployeeNumber() == user.getEmployeeNumber()){

                area.append("SSS Number: " + emp.getSss() + "\n");
                area.append("PhilHealth Number: " + emp.getPhilhealth() + "\n");
                area.append("TIN Number: " + emp.getTin() + "\n");
                area.append("PagIBIG Number: " + emp.getPagibig());

                break;

            }

        }

        add(new JScrollPane(area));

    }

}
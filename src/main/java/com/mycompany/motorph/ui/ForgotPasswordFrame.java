/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ForgotPasswordFrame extends JFrame {

    private JTextField usernameField;
    private JTextField fullNameField;
    private JPasswordField newPassField;

    public ForgotPasswordFrame(){

        setTitle("Forgot Password");
        setSize(350,220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));

        usernameField = new JTextField();
        fullNameField = new JTextField();
        newPassField = new JPasswordField();

        JButton resetBtn = new JButton("Reset Password");

        panel.add(new JLabel("Username"));
        panel.add(usernameField);

        panel.add(new JLabel("Full Name"));
        panel.add(fullNameField);

        panel.add(new JLabel("New Password"));
        panel.add(newPassField);

        panel.add(new JLabel());
        panel.add(resetBtn);

        add(panel);

        resetBtn.addActionListener(e -> resetPassword());

    }

    private void resetPassword(){

        String username = usernameField.getText().trim();
        String fullname = fullNameField.getText().trim();
        String newPass = new String(newPassField.getPassword());

        UserDAO userDAO = new UserDAO();
        EmployeeDAO empDAO = new EmployeeDAO();

        List<User> users = userDAO.loadUsers();
        List<Employee> employees = empDAO.loadEmployees("employees.csv");

        AuthService service = new AuthService();

        boolean success = service.resetPassword(
                username,
                fullname,
                newPass,
                users,
                employees
        );

        if(success){

            JOptionPane.showMessageDialog(this,"Password reset successful");
            dispose();

        }else{

            JOptionPane.showMessageDialog(this,"Verification failed");

        }

    }

}
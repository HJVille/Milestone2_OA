/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("MotorPH Login");
        setSize(350,220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton forgotButton = new JButton("Forgot Password");

        panel.add(userLabel);
        panel.add(usernameField);

        panel.add(passLabel);
        panel.add(passwordField);

        panel.add(loginButton);
        panel.add(forgotButton);

        add(panel);

        loginButton.addActionListener(e -> login());
        forgotButton.addActionListener(e -> openForgotPassword());

    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.loadUsers();

        AuthService authService = new AuthService();

        User user = authService.login(username, password, users, 1);

        if (user == null) {

            JOptionPane.showMessageDialog(this,"Invalid username or password");
            return;

        }

        String role = user.getRole().toUpperCase();

        switch (role) {

            case "HR":
                new HRDashboardFrame().setVisible(true);
                dispose();
                break;

            case "EMPLOYEE":
                new EmployeeDashboardFrame(user).setVisible(true);
                dispose();
                break;

            case "PAYROLL":
                new PayrollDashboardFrame(user).setVisible(true);
                dispose();
                break;

            case "ADMIN":
                new HRDashboardFrame().setVisible(true);
                dispose();
                break;

            default:
                JOptionPane.showMessageDialog(this,"Unknown role: " + role);

        }

    }

    private void openForgotPassword() {

        new ForgotPasswordFrame().setVisible(true);

    }

}
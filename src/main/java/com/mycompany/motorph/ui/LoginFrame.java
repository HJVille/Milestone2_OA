/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final Color HEADER_BLUE = new Color(44,74,115);
    private final Color BUTTON_BLUE = new Color(59,95,146);
    private final Color BACKGROUND = new Color(247,249,252);
    private final Color DARK_TEXT = new Color(31,58,95);
    private final Color BORDER = new Color(217,225,236);

    public LoginFrame() {

        setTitle("MotorPH Payroll System");
        setSize(420,320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        JPanel header = new JPanel();
        header.setBackground(HEADER_BLUE);
        header.setPreferredSize(new Dimension(400,70));

        JLabel title = new JLabel("MotorPH Payroll System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title);

        JPanel panel = new JPanel(new GridLayout(4,1,10,10));
        panel.setBorder(new EmptyBorder(20,40,20,40));
        panel.setBackground(Color.WHITE);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        styleField(usernameField);
        styleField(passwordField);

        JPanel userPanel = createField("Username:", usernameField);
        JPanel passPanel = createField("Password:", passwordField);

        JButton loginButton = createPrimaryButton("Login");
        JButton forgotButton = createSecondaryButton("Forgot Password");

        JPanel buttonPanel = new JPanel(new GridLayout(1,2,10,10));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(forgotButton);

        panel.add(userPanel);
        panel.add(passPanel);
        panel.add(buttonPanel);

        add(header, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> login());
        forgotButton.addActionListener(e -> openForgotPassword());
    }

    private JPanel createField(String labelText, JTextField field){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setForeground(DARK_TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void styleField(JTextField field){

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(DARK_TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private JButton createPrimaryButton(String text){

        JButton btn = new JButton(text);

        btn.setBackground(BUTTON_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JButton createSecondaryButton(String text){

        JButton btn = new JButton(text);

        btn.setBackground(new Color(234,240,248));
        btn.setForeground(DARK_TEXT);
        btn.setBorder(BorderFactory.createLineBorder(BORDER));

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.loadUsers();

        AuthService authService = new AuthService();

        User user = authService.login(username, password, users, 1);

        if (user == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "MotorPH Login",
                    JOptionPane.ERROR_MESSAGE
            );
            return;

        }

        String role = user.getRole().toUpperCase();

        switch (role) {

            case "HR":
                new HRDashboardFrame(user).setVisible(true);
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
                new HRDashboardFrame(user).setVisible(true);
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
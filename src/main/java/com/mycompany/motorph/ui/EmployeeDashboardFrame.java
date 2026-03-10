/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EmployeeDashboardFrame extends JFrame {

    private User loggedUser;

    private final Color PRIMARY_BLUE = new Color(44,74,115);
    private final Color BACKGROUND = new Color(244,247,251);
    private final Color PANEL_BORDER = new Color(217,226,236);
    private final Color TEXT_COLOR = new Color(36,52,71);

    public EmployeeDashboardFrame(User user){

        this.loggedUser = user;

        setTitle("MotorPH Employee Dashboard");
        setSize(900,550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        /* HEADER */

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(new EmptyBorder(20,30,20,30));

        JLabel title = new JLabel("MotorPH Employee Portal");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        /* DASHBOARD */

        JPanel dashboard = new JPanel(new GridLayout(2,3,25,25));
        dashboard.setBorder(new EmptyBorder(40,40,40,40));
        dashboard.setBackground(BACKGROUND);

        JButton profileBtn = createCardButton(
                "View Profile",
                "View employee information"
        );

        JButton govBtn = createCardButton(
                "Government IDs",
                "SSS, PhilHealth, PagIBIG"
        );

        JButton payslipBtn = createCardButton(
                "View Payslip",
                "View latest payslip"
        );

        JButton historyBtn = createCardButton(
                "Payslip History",
                "View payroll history"
        );

        JButton changePassBtn = createCardButton(
                "Change Password",
                "Update login password"
        );

        JButton logoutBtn = createCardButton(
                "Logout",
                "Return to login"
        );

        dashboard.add(profileBtn);
        dashboard.add(govBtn);
        dashboard.add(payslipBtn);
        dashboard.add(historyBtn);
        dashboard.add(changePassBtn);
        dashboard.add(logoutBtn);

        add(dashboard, BorderLayout.CENTER);

        /* ACTIONS */

        profileBtn.addActionListener(e ->
                new ViewProfileFrame(loggedUser).setVisible(true));

        govBtn.addActionListener(e ->
                new GovernmentIDsFrame(loggedUser).setVisible(true));

        payslipBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Open payslip from Payslip History"));

        historyBtn.addActionListener(e ->
                new PayslipHistoryFrame(loggedUser).setVisible(true));

        changePassBtn.addActionListener(e ->
                new ChangePasswordFrame(loggedUser).setVisible(true));

        logoutBtn.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

    }

    private JButton createCardButton(String title,String subtitle){

        JButton button = new JButton(
                "<html><center>"
                        +"<div style='font-size:16px;font-weight:bold;'>"
                        +title+
                        "</div>"
                        +"<div style='font-size:11px;'>"
                        +subtitle+
                        "</div>"
                        +"</center></html>"
        );

        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);

        button.setFont(new Font("Segoe UI",Font.BOLD,14));
        button.setFocusPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PANEL_BORDER),
                new EmptyBorder(25,20,25,20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(236,241,248));
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(Color.WHITE);
            }

        });

        return button;

    }

}
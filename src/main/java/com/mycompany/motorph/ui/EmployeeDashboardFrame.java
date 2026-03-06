/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class EmployeeDashboardFrame extends JFrame {

    private User loggedUser;

    public EmployeeDashboardFrame(User user) {

        this.loggedUser = user;

        setTitle("MotorPH Employee Dashboard");
        setSize(350,320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6,1,10,10));

        JButton profileBtn = new JButton("View Profile");
        JButton govBtn = new JButton("Government IDs");
        JButton payslipBtn = new JButton("View Payslip");
        JButton historyBtn = new JButton("Payslip History");
        JButton changePassBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        panel.add(profileBtn);
        panel.add(govBtn);
        panel.add(payslipBtn);
        panel.add(historyBtn);
        panel.add(changePassBtn);
        panel.add(logoutBtn);

        add(panel);

        profileBtn.addActionListener(e ->
                new ViewProfileFrame(loggedUser).setVisible(true));

        govBtn.addActionListener(e ->
                new GovernmentIDsFrame(loggedUser).setVisible(true));

        // VIEW LATEST PAYSLIP
        payslipBtn.addActionListener(e -> openLatestPayslip());

        historyBtn.addActionListener(e ->
                new PayslipHistoryFrame(loggedUser).setVisible(true));

        changePassBtn.addActionListener(e ->
                new ChangePasswordFrame(loggedUser).setVisible(true));

        logoutBtn.addActionListener(e -> {

            new LoginFrame().setVisible(true);
            dispose();

        });

    }

    private void openLatestPayslip(){

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            String line;
            String lastPeriodStart = null;
            String lastPeriodEnd = null;

            br.readLine();

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                int empNum = Integer.parseInt(data[0]);

                if(empNum == loggedUser.getEmployeeNumber()){

                    lastPeriodStart = data[2];
                    lastPeriodEnd = data[3];

                }

            }

            br.close();

            if(lastPeriodStart == null){

                JOptionPane.showMessageDialog(this,
                        "No payslip found.");

                return;
            }

            new PayslipFrame(
                    loggedUser,
                    lastPeriodStart,
                    lastPeriodEnd
            ).setVisible(true);

        }catch(Exception ex){

            JOptionPane.showMessageDialog(this,
                    "Unable to load payslip.");

        }

    }

}
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

public class ChangePasswordFrame extends JFrame {

    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private User loggedUser;

    public ChangePasswordFrame(User user){

        this.loggedUser = user;

        setTitle("Change Password");
        setSize(320,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3,2,10,10));

        JLabel oldLabel = new JLabel("Current Password:");
        JLabel newLabel = new JLabel("New Password:");

        oldPassField = new JPasswordField();
        newPassField = new JPasswordField();

        JButton changeBtn = new JButton("Change Password");

        panel.add(oldLabel);
        panel.add(oldPassField);

        panel.add(newLabel);
        panel.add(newPassField);

        panel.add(new JLabel());
        panel.add(changeBtn);

        add(panel);

        changeBtn.addActionListener(e -> changePassword());

    }

    private void changePassword(){

        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());

        UserDAO dao = new UserDAO();
        List<User> users = dao.loadUsers();

        AuthService service = new AuthService();

        boolean success = service.changePassword(
                loggedUser,
                oldPass,
                newPass,
                users
        );

        if(success){

            JOptionPane.showMessageDialog(this,"Password updated successfully");
            dispose();

        }else{

            JOptionPane.showMessageDialog(this,"Password update failed");

        }

    }

}
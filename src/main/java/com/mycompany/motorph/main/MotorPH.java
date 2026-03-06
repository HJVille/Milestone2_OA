package com.mycompany.motorph.main;

import com.mycompany.motorph.ui.LoginFrame;

public class MotorPH {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            LoginFrame login = new LoginFrame();
            login.setVisible(true);

        });

    }

}
package com.mycompany.motorph.main;

import com.mycompany.motorph.ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class MotorPH {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {

            try {

                // MotorPH UI Theme
                UIManager.put("Panel.background", Color.WHITE);

                UIManager.put("Button.background", new Color(220,38,38));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));

                UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));

                UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
                UIManager.put("Table.rowHeight", 28);

            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginFrame login = new LoginFrame();
            login.setVisible(true);

        });

    }

}
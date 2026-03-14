package com.mycompany.motorph.main;

import com.mycompany.motorph.ui.LoginForm;
import com.mycompany.motorph.ui.BrandTheme;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class MotorPH {

    private static final Logger LOGGER = Logger.getLogger(MotorPH.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                BrandTheme.installGlobalTheme();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unable to install the application theme.", e);
            }
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}

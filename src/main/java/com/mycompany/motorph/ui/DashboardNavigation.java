package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.NotificationService;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JButton;

public final class DashboardNavigation {

    private static final NotificationService NOTIFICATION_SERVICE = new NotificationService();

    private DashboardNavigation() {
    }

    public static JButton createLogoutButton(Component owner) {
        return createLogoutButton(owner, null);
    }

    public static JButton createLogoutButton(Component owner, User user) {
        JButton button = new JButton("Logout");
        BrandTheme.styleSecondaryButton(button);
        button.addActionListener(evt -> logout(owner, user));
        return button;
    }

    public static void logout(Component owner) {
        logout(owner, null);
    }

    public static void logout(Component owner, User user) {
        Window window;
        if (owner instanceof Window) {
            window = (Window) owner;
        } else {
            window = owner == null ? null : javax.swing.SwingUtilities.getWindowAncestor(owner);
        }

        NOTIFICATION_SERVICE.record(user, "LOGOUT", user == null
                ? "A user signed out of the system."
                : user.getUsername() + " signed out.");

        LoginForm loginForm = new LoginForm();
        loginForm.setLocationRelativeTo(window);
        loginForm.setVisible(true);

        if (window != null) {
            window.dispose();
        }
    }

    public static JButton createNotificationButton(Component owner) {
        JButton button = new JButton("Notifications 🔔");
        BrandTheme.styleSecondaryButton(button);
        button.addActionListener(evt -> NotificationCenterDialog.showDialog(owner));
        return button;
    }
}

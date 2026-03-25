package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AuthService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class pnlDefaultPassword extends javax.swing.JPanel {

    private final AuthService authService = new AuthService();
    private final NotificationService notificationService = new NotificationService();
    private final User user;
    private final List<User> users;
    private final Runnable onPasswordUpdated;
    private final Runnable onBack;
    private final boolean useEmployeeDashboardBranding;

    private JLabel lblLogo;
    private JLabel lblHeader;
    private JLabel lblSubtitle;
    private JLabel lblNewPassword;
    private JLabel lblConfirmPassword;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnUpdatePassword;

    public pnlDefaultPassword(User user, List<User> users, Runnable onPasswordUpdated) {
        this(user, users, onPasswordUpdated, null, false);
    }

    public pnlDefaultPassword(User user,
                              List<User> users,
                              Runnable onPasswordUpdated,
                              Runnable onBack,
                              boolean useEmployeeDashboardBranding) {
        this.user = user;
        this.users = users;
        this.onPasswordUpdated = onPasswordUpdated;
        this.onBack = onBack;
        this.useEmployeeDashboardBranding = useEmployeeDashboardBranding;
        initComponents();
        configurePanel();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BrandTheme.IVORY);
        setPreferredSize(new Dimension(880, 620));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setOpaque(false);
        shell.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(shell, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setOpaque(true);
        content.setBackground(BrandTheme.PAPER);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JPanel topActions = new JPanel(new BorderLayout());
        topActions.setOpaque(false);

        btnBack = new javax.swing.JButton("Back");
        btnBack.setVisible(onBack != null);
        btnBack.addActionListener(this::btnBackActionPerformed);
        topActions.add(btnBack, BorderLayout.WEST);

        lblLogo = new JLabel();
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        lblHeader = new JLabel("", SwingConstants.CENTER);
        lblHeader.setAlignmentX(CENTER_ALIGNMENT);

        lblSubtitle = new JLabel("For security, please change your default password to continue.", SwingConstants.CENTER);
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);

        lblNewPassword = new JLabel("New Password");
        lblNewPassword.setAlignmentX(CENTER_ALIGNMENT);

        newPasswordField = new JPasswordField();
        newPasswordField.setMaximumSize(new Dimension(450, 46));
        newPasswordField.setPreferredSize(new Dimension(450, 46));
        newPasswordField.setAlignmentX(CENTER_ALIGNMENT);

        lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setAlignmentX(CENTER_ALIGNMENT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(450, 46));
        confirmPasswordField.setPreferredSize(new Dimension(450, 46));
        confirmPasswordField.setAlignmentX(CENTER_ALIGNMENT);

        btnUpdatePassword = new javax.swing.JButton("Continue");
        btnUpdatePassword.setAlignmentX(CENTER_ALIGNMENT);
        btnUpdatePassword.setMaximumSize(new Dimension(450, 48));
        btnUpdatePassword.setPreferredSize(new Dimension(450, 48));
        btnUpdatePassword.addActionListener(this::btnUpdatePasswordActionPerformed);

        content.add(topActions);
        content.add(Box.createVerticalStrut(onBack == null ? 8 : 14));
        content.add(lblLogo);
        content.add(Box.createVerticalStrut(18));
        content.add(lblHeader);
        content.add(Box.createVerticalStrut(8));
        content.add(lblSubtitle);
        content.add(Box.createVerticalStrut(28));
        content.add(lblNewPassword);
        content.add(Box.createVerticalStrut(8));
        content.add(newPasswordField);
        content.add(Box.createVerticalStrut(16));
        content.add(lblConfirmPassword);
        content.add(Box.createVerticalStrut(8));
        content.add(confirmPasswordField);
        content.add(Box.createVerticalStrut(20));
        content.add(btnUpdatePassword);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        shell.add(scrollPane, BorderLayout.CENTER);
    }

    private void configurePanel() {
        lblLogo.setIcon(useEmployeeDashboardBranding
                ? BrandTheme.loadEmployeeDashboardLogoIcon(520, 210)
                : BrandTheme.loadLogoIcon(520, 210));

        lblHeader.setFont(BrandTheme.WELCOME_FONT.deriveFont(Font.PLAIN, 30f));
        lblHeader.setForeground(BrandTheme.TEXT);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);

        lblSubtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 17f));
        lblSubtitle.setForeground(BrandTheme.MUTED);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        lblNewPassword.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 17f));
        lblConfirmPassword.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 17f));
        lblNewPassword.setForeground(BrandTheme.TEXT);
        lblConfirmPassword.setForeground(BrandTheme.TEXT);
        lblNewPassword.setHorizontalAlignment(SwingConstants.CENTER);
        lblConfirmPassword.setHorizontalAlignment(SwingConstants.CENTER);

        BrandTheme.styleInputField(newPasswordField);
        BrandTheme.styleInputField(confirmPasswordField);
        BrandTheme.styleSecondaryButton(btnBack);
        BrandTheme.stylePrimaryButton(btnUpdatePassword);
        btnBack.setAlignmentX(LEFT_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(120, 42));
        btnBack.setPreferredSize(new Dimension(120, 42));
        btnUpdatePassword.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 20f));

        if (user != null) {
            lblHeader.setText("<html>Welcome, <b>" + user.getUsername() + "!</b></html>");
        } else {
            lblHeader.setText("Welcome!");
        }
    }

    private void btnUpdatePasswordActionPerformed(java.awt.event.ActionEvent evt) {
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter the new password in both fields.",
                    "Password Update",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this,
                    "New password and confirmation do not match.",
                    "Password Update",
                    JOptionPane.WARNING_MESSAGE
            );
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        String currentDefaultPassword = user == null ? "" : "emp" + user.getEmployeeNumber();
        boolean updated = authService.changePassword(user, currentDefaultPassword, newPassword, users);
        if (!updated) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password update failed. Choose a different password.",
                    "Password Update",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Password updated successfully.",
                "Password Update",
                JOptionPane.INFORMATION_MESSAGE
        );
        notificationService.record(
                user,
                "PASSWORD_CHANGED",
                user == null ? "An account password was updated." : user.getUsername() + " updated the account password."
        );

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }

        if (onPasswordUpdated != null) {
            onPasswordUpdated.run();
        }
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        if (onBack == null) {
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        onBack.run();
    }
}

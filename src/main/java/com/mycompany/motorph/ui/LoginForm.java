package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AuthService;
import com.mycompany.motorph.service.NotificationService;
import com.mycompany.motorph.service.UserAccountService;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginForm extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginForm.class.getName());
    private static final int MAX_ATTEMPTS = 5;

    private final AuthService authService = new AuthService();
    private final UserAccountService userAccountService = new UserAccountService();
    private final NotificationService notificationService = new NotificationService();
    private int attempts;

    private JButton btnLogin;
    private JLabel lblFogotPassword;
    private JLabel lblPassword;
    private JLabel lblUsername;
    private JLabel lblWelcome;
    private JLabel lblLogo;
    private JLabel lblSubtitle;
    private JPasswordField txtPassword;
    private JTextField txtUsername;
    private JLabel lblUsernameError;
    private JLabel lblPasswordError;
    private JButton btnPasswordVisibility;
    private JPanel logoPanel;
    private JPanel formPanel;
    private JPanel contentShell;
    private JPanel passwordFieldShell;
    private char passwordEchoChar;
    private boolean passwordVisible;

    public LoginForm() {
        BrandTheme.installGlobalTheme();
        initComponents();
        configureForm();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MotorPH Login");
        setMinimumSize(new Dimension(980, 720));
        setResizable(true);

        LoginBackgroundPanel root = new LoginBackgroundPanel();
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        contentShell = new JPanel(new GridLayout(1, 2, 18, 0));
        contentShell.setOpaque(false);
        contentShell.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
        logoPanel.add(lblLogo, BorderLayout.CENTER);

        formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        lblWelcome = new JLabel("Welcome to MotorPH");
        lblWelcome.setAlignmentX(CENTER_ALIGNMENT);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        lblSubtitle = new JLabel(BrandTheme.TAGLINE);
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        lblUsername = new JLabel("Username");
        lblUsername.setAlignmentX(CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setColumns(22);
        txtUsername.setMaximumSize(new Dimension(420, 48));
        txtUsername.setPreferredSize(new Dimension(420, 48));
        txtUsername.setAlignmentX(CENTER_ALIGNMENT);
        lblUsernameError = new JLabel(" ");
        lblUsernameError.setAlignmentX(CENTER_ALIGNMENT);

        lblPassword = new JLabel("Password");
        lblPassword.setAlignmentX(CENTER_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setColumns(22);
        txtPassword.setMaximumSize(new Dimension(420, 48));
        txtPassword.setPreferredSize(new Dimension(420, 48));
        txtPassword.setAlignmentX(CENTER_ALIGNMENT);

        btnPasswordVisibility = new JButton();
        btnPasswordVisibility.setAlignmentX(CENTER_ALIGNMENT);

        passwordFieldShell = new JPanel(new BorderLayout());
        passwordFieldShell.setOpaque(true);
        passwordFieldShell.setMaximumSize(new Dimension(420, 48));
        passwordFieldShell.setPreferredSize(new Dimension(420, 48));
        passwordFieldShell.setAlignmentX(CENTER_ALIGNMENT);
        passwordFieldShell.add(txtPassword, BorderLayout.CENTER);
        passwordFieldShell.add(btnPasswordVisibility, BorderLayout.EAST);
        lblPasswordError = new JLabel(" ");
        lblPasswordError.setAlignmentX(CENTER_ALIGNMENT);

        lblFogotPassword = new JLabel("Forgot Password?");
        lblFogotPassword.setAlignmentX(CENTER_ALIGNMENT);
        lblFogotPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnLogin = new JButton("Sign In");
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.setPreferredSize(new Dimension(420, 52));
        btnLogin.setMaximumSize(new Dimension(420, 52));
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(lblWelcome);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblSubtitle);
        formPanel.add(Box.createVerticalStrut(42));
        formPanel.add(lblUsername);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(lblUsernameError);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(lblPassword);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordFieldShell);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(lblPasswordError);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(lblFogotPassword);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalGlue());

        contentShell.add(logoPanel);
        contentShell.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(24, 24, 24, 24);
        root.add(contentShell, gbc);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveSizing();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        BrandTheme.installGlobalTheme();
        java.awt.EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }

    private void configureForm() {
        getRootPane().setDefaultButton(btnLogin);

        contentShell.setOpaque(true);
        contentShell.setBackground(BrandTheme.BACKDROP);
        contentShell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 8, 8, new Color(15, 23, 42, 24)),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)
                )
        ));
        logoPanel.setOpaque(true);
        logoPanel.setBackground(BrandTheme.SKY);
        logoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));

        formPanel.setOpaque(true);
        formPanel.setBackground(BrandTheme.PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)
        ));

        lblWelcome.setForeground(BrandTheme.TEXT);
        lblWelcome.setFont(BrandTheme.WELCOME_FONT.deriveFont(Font.BOLD, 34f));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        lblSubtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 15f));
        lblSubtitle.setForeground(BrandTheme.MUTED);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        lblUsername.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        lblPassword.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        lblUsername.setForeground(BrandTheme.TEXT);
        lblPassword.setForeground(BrandTheme.TEXT);
        lblUsernameError.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 11f));
        lblPasswordError.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 11f));
        lblUsernameError.setForeground(BrandTheme.MOTORPH_RED);
        lblPasswordError.setForeground(BrandTheme.MOTORPH_RED);
        lblUsernameError.setVisible(false);
        lblPasswordError.setVisible(false);
        lblUsernameError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        lblPasswordError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        BrandTheme.styleInputField(txtUsername);
        BrandTheme.styleInputField(txtPassword);
        txtUsername.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
        txtPassword.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
        passwordEchoChar = txtPassword.getEchoChar() == 0 ? '\u2022' : txtPassword.getEchoChar();
        txtPassword.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 6));

        passwordFieldShell.setBackground(BrandTheme.INPUT_BG);
        passwordFieldShell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        btnPasswordVisibility.setFocusable(false);
        btnPasswordVisibility.setOpaque(false);
        btnPasswordVisibility.setContentAreaFilled(false);
        btnPasswordVisibility.setBorderPainted(false);
        btnPasswordVisibility.setFocusPainted(false);
        btnPasswordVisibility.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        btnPasswordVisibility.setPreferredSize(new Dimension(44, 48));
        btnPasswordVisibility.setMinimumSize(new Dimension(44, 48));
        btnPasswordVisibility.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPasswordVisibility.addActionListener(evt -> togglePasswordVisibility());
        setPasswordVisibility(false);
        attachInlineValidation();

        BrandTheme.stylePrimaryButton(btnLogin);
        btnLogin.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));

        lblFogotPassword.setForeground(BrandTheme.PRIMARY_BLUE);
        lblFogotPassword.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        lblFogotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showForgotPasswordMessage();
            }
        });

        txtUsername.setText("");
        txtPassword.setText("");
        setPasswordVisibility(false);
        updateResponsiveSizing();
    }

    private void togglePasswordVisibility() {
        setPasswordVisibility(!passwordVisible);
    }

    private void setPasswordVisibility(boolean visible) {
        passwordVisible = visible;
        txtPassword.setEchoChar(visible ? (char) 0 : passwordEchoChar);
        btnPasswordVisibility.setIcon(new PasswordVisibilityIcon(visible));
        btnPasswordVisibility.setToolTipText(visible ? "Hide password" : "Show password");
    }

    private void updateResponsiveSizing() {
        int width = Math.max(980, getWidth());
        int height = Math.max(720, getHeight());
        int formWidth = Math.min(620, Math.max(470, width / 2 - 120));
        int logoWidth = Math.min(820, Math.max(540, width / 2));
        int logoHeight = Math.min(460, Math.max(280, height - 220));
        int shellWidth = Math.min(1320, Math.max(980, width - 80));
        int shellHeight = Math.min(640, Math.max(520, height - 140));

        txtUsername.setMaximumSize(new Dimension(formWidth, 44));
        txtUsername.setPreferredSize(new Dimension(formWidth, 44));
        txtPassword.setMaximumSize(new Dimension(formWidth - 56, 44));
        txtPassword.setPreferredSize(new Dimension(formWidth - 56, 44));
        passwordFieldShell.setMaximumSize(new Dimension(formWidth, 44));
        passwordFieldShell.setPreferredSize(new Dimension(formWidth, 44));
        btnLogin.setMaximumSize(new Dimension(formWidth, 46));
        btnLogin.setPreferredSize(new Dimension(formWidth, 46));
        contentShell.setPreferredSize(new Dimension(shellWidth, shellHeight));
        lblWelcome.setPreferredSize(new Dimension(formWidth, 62));
        lblSubtitle.setPreferredSize(new Dimension(formWidth, 28));

        Icon icon = BrandTheme.loadLogoIcon(logoWidth, logoHeight);
        if (icon != null) {
            lblLogo.setIcon(icon);
        }
        float titleSize = Math.min(34f, Math.max(24f, width / 42f));
        float subtitleSize = Math.min(16f, Math.max(13f, width / 84f));
        lblWelcome.setFont(BrandTheme.WELCOME_FONT.deriveFont(Font.BOLD, titleSize));
        lblSubtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, subtitleSize));

        revalidate();
        repaint();
    }

    private void btnLoginActionPerformed(ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        boolean valid = true;
        if (username.isEmpty()) {
            showTextFieldError(txtUsername, lblUsernameError, "This field is required.");
            valid = false;
        }
        if (password.isBlank()) {
            showPasswordFieldError("This field is required.");
            valid = false;
        }
        if (!valid) {
            if (username.isEmpty()) {
                txtUsername.requestFocusInWindow();
            } else {
                txtPassword.requestFocusInWindow();
            }
            return;
        }

        List<User> users = userAccountService.loadUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No user records were loaded. Check CSVs/users.csv.", "Login Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }

        attempts++;
        User user = authService.login(username, password, users, attempts);

        if (user == null) {
            handleFailedAttempt();
            return;
        }

        attempts = 0;
        if ("EMPLOYEE".equalsIgnoreCase(user.getRole()) && authService.isDefaultPassword(user)) {
            openPasswordUpdateScreen(user, users);
            return;
        }

        openDashboard(user);
    }

    private void handleFailedAttempt() {
        int remainingAttempts = MAX_ATTEMPTS - attempts;

        if (remainingAttempts <= 0) {
            JOptionPane.showMessageDialog(this, "Maximum login attempts reached. The application will close.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this, "Invalid username or password.\nRemaining attempts: " + remainingAttempts, "Login Failed", JOptionPane.WARNING_MESSAGE);
        txtPassword.setText("");
        txtPassword.requestFocusInWindow();
    }

    private void openDashboard(User user) {
        try {
            notificationService.record(
                    user,
                    "LOGIN",
                    user.getUsername() + " signed in to the " + user.getRole() + " dashboard."
            );

            String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
            JFrame destination;

            switch (role) {
                case "HR":
                    destination = new HRDashboard(user);
                    break;
                case "PAYROLL":
                case "FINANCE":
                    destination = new FinanceDashboardFrame(user);
                    break;
                case "IT":
                    destination = new SystemToolsFrame(user);
                    break;
                case "ADMIN":
                    destination = new AdminDashboardFrame(user);
                    break;
                case "EMPLOYEE":
                    destination = new EmployeeDashboard(user, userAccountService.loadUsers());
                    break;
                default:
                    JOptionPane.showMessageDialog(
                            this,
                            "The account role '" + user.getRole() + "' is not mapped to any dashboard.",
                            "Access Denied",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
            }

            destination.setResizable(true);
            destination.setLocationRelativeTo(this);
            destination.setVisible(true);
            dispose();
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to open dashboard", ex);
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open the dashboard.\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openPasswordUpdateScreen(User user, List<User> users) {
        JFrame frame = new JFrame("Update Password");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(new pnlDefaultPassword(user, users, () -> openDashboard(user)));
        frame.setMinimumSize(new Dimension(700, 460));
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        dispose();
    }

    private void showForgotPasswordMessage() {
        List<User> users = userAccountService.loadUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No user records were loaded. Check CSVs/users.csv.",
                    "Forgot Password",
                    JOptionPane.ERROR_MESSAGE
            );
            SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
            return;
        }

        String username = txtUsername.getText().trim();
        User matchedUser = findUserByUsername(username, users);

        if (matchedUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter your username first so the system can identify the correct recovery process.",
                    "Forgot Password",
                    JOptionPane.INFORMATION_MESSAGE
            );
            SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
            return;
        }

        String role = matchedUser.getRole() == null ? "" : matchedUser.getRole().trim().toUpperCase();
        switch (role) {
            case "EMPLOYEE":
                showEmployeeResetDialog(users);
                break;
            case "HR":
                JOptionPane.showMessageDialog(this, "Contact Administrator.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "ADMIN":
                JOptionPane.showMessageDialog(
                        this,
                        "Self-service reset is disabled for admin accounts.\nContact IT for password assistance.",
                        "Forgot Password",
                        JOptionPane.WARNING_MESSAGE
                );
                break;
            case "IT":
                JOptionPane.showMessageDialog(
                        this,
                        "Self-service reset is disabled for IT accounts.\nContact the Administrator for password assistance.",
                        "Forgot Password",
                        JOptionPane.WARNING_MESSAGE
                );
                break;
            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Contact the Administrator for password assistance.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
        }

        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
    }

    private void showEmployeeResetDialog(List<User> users) {
        JTextField employeeNumberField = new JTextField(txtUsername.getText().trim());
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField sssField = new JTextField();
        JTextField philhealthField = new JTextField();
        JTextField tinField = new JTextField();
        JTextField pagibigField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        JComponent[] fields = {
            employeeNumberField, firstNameField, lastNameField, sssField,
            philhealthField, tinField, pagibigField, newPasswordField, confirmPasswordField
        };
        for (JComponent field : fields) {
            field.setPreferredSize(new Dimension(220, 30));
            BrandTheme.styleInputField((JTextField) field);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BrandTheme.IVORY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        addResetField(panel, gbc, "Employee ID Number", employeeNumberField);
        addResetField(panel, gbc, "First Name", firstNameField);
        addResetField(panel, gbc, "Last Name", lastNameField);
        addResetField(panel, gbc, "SSS Number", sssField);
        addResetField(panel, gbc, "PhilHealth Number", philhealthField);
        addResetField(panel, gbc, "TIN", tinField);
        addResetField(panel, gbc, "Pag-IBIG Number", pagibigField);
        addResetField(panel, gbc, "New Password", newPasswordField);
        addResetField(panel, gbc, "Confirm Password", confirmPasswordField);

        boolean confirmed = DialogHelper.showFormDialog(
                this,
                "Employee Password Recovery",
                panel,
                "Reset Password",
                "Cancel"
        );

        if (!confirmed) {
            return;
        }

        String employeeNumberText = employeeNumberField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String sss = sssField.getText().trim();
        String philhealth = philhealthField.getText().trim();
        String tin = tinField.getText().trim();
        String pagibig = pagibigField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (employeeNumberText.isEmpty()
                || firstName.isEmpty()
                || lastName.isEmpty()
                || sss.isEmpty()
                || philhealth.isEmpty()
                || tin.isEmpty()
                || pagibig.isEmpty()
                || newPassword.isBlank()
                || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "Complete all verification fields before resetting the password.", "Forgot Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Forgot Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeNumber;
        try {
            employeeNumber = Integer.parseInt(employeeNumberText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID Number must be numeric.", "Forgot Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean resetSuccessful = authService.resetPasswordWithGovernmentIds(
                employeeNumber,
                firstName,
                lastName,
                sss,
                philhealth,
                tin,
                pagibig,
                newPassword,
                users
        );

        if (!resetSuccessful) {
            JOptionPane.showMessageDialog(
                    this,
                    "Identity verification failed or the password could not be updated.",
                    "Forgot Password",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        txtPassword.setText("");
        JOptionPane.showMessageDialog(
                this,
                "Password updated successfully. Your latest password has been saved.",
                "Forgot Password",
                JOptionPane.INFORMATION_MESSAGE
        );
        User resetUser = findUserByUsername(txtUsername.getText().trim(), users);
        notificationService.record(
                resetUser,
                "PASSWORD_SELF_RESET",
                "Employee password recovery completed successfully for username " + txtUsername.getText().trim() + "."
        );
    }

    private void addResetField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setForeground(BrandTheme.TEXT);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private User findUserByUsername(String username, List<User> users) {
        if (username == null || username.isBlank()) {
            return null;
        }

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return user;
            }
        }

        return null;
    }

    private static final class LoginBackgroundPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.WHITE,
                    getWidth(), getHeight(), BrandTheme.BACKDROP
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(BrandTheme.PRIMARY_BLUE.getRed(), BrandTheme.PRIMARY_BLUE.getGreen(), BrandTheme.PRIMARY_BLUE.getBlue(), 36));
            g2.fillOval(-140, -120, 420, 420);
            g2.setColor(new Color(BrandTheme.MOTORPH_RED.getRed(), BrandTheme.MOTORPH_RED.getGreen(), BrandTheme.MOTORPH_RED.getBlue(), 26));
            g2.fillOval(getWidth() - 300, getHeight() - 260, 340, 340);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRoundRect(52, 40, getWidth() - 104, getHeight() - 80, 42, 42);

            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(BrandTheme.GOLD.getRed(), BrandTheme.GOLD.getGreen(), BrandTheme.GOLD.getBlue(), 72));
            g2.drawArc(-40, getHeight() - 220, 250, 180, 18, 118);
            g2.drawArc(getWidth() - 280, 72, 220, 150, 208, 118);

            g2.dispose();
        }
    }

    private void attachInlineValidation() {
        txtUsername.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!txtUsername.getText().trim().isEmpty()) {
                    clearTextFieldError(txtUsername, lblUsernameError);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!txtUsername.getText().trim().isEmpty()) {
                    clearTextFieldError(txtUsername, lblUsernameError);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!txtUsername.getText().trim().isEmpty()) {
                    clearTextFieldError(txtUsername, lblUsernameError);
                }
            }
        });

        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (txtPassword.getPassword().length > 0) {
                    clearPasswordFieldError();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (txtPassword.getPassword().length > 0) {
                    clearPasswordFieldError();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (txtPassword.getPassword().length > 0) {
                    clearPasswordFieldError();
                }
            }
        });
    }

    private void showTextFieldError(JTextField field, JLabel errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.MOTORPH_RED, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void clearTextFieldError(JTextField field, JLabel errorLabel) {
        errorLabel.setText(" ");
        errorLabel.setVisible(false);
        BrandTheme.styleInputField(field);
        field.setFont(BrandTheme.BODY_FONT.deriveFont(18f));
    }

    private void showPasswordFieldError(String message) {
        lblPasswordError.setText(message);
        lblPasswordError.setVisible(true);
        passwordFieldShell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.MOTORPH_RED, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
    }

    private void clearPasswordFieldError() {
        lblPasswordError.setText(" ");
        lblPasswordError.setVisible(false);
        passwordFieldShell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
    }

    private static final class PasswordVisibilityIcon implements Icon {

        private final boolean visible;

        private PasswordVisibilityIcon(boolean visible) {
            this.visible = visible;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(BrandTheme.MUTED);

            g2.drawArc(x + 1, y + 4, 18, 10, 0, 180);
            g2.drawArc(x + 1, y + 0, 18, 10, 180, 180);
            g2.fillOval(x + 8, y + 4, 4, 4);

            if (!visible) {
                g2.drawLine(x + 3, y + 15, x + 17, y + 1);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}

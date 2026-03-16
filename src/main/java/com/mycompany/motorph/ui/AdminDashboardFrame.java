package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdminDashboardFrame extends JFrame {

    private static final String[] ADMIN_EMPLOYEE_FILTERS = {
            "Employee Number", "Last Name", "First Name", "Position"
    };
    private static final String[] ADMIN_ATTENDANCE_FILTERS = {
            "Employee ID", "Employee Name", "Date", "Status"
    };
    private static final String[] ADMIN_LEAVE_FILTERS = {
            "Employee ID", "Employee Name", "Leave Type", "Status"
    };

    private final User user;
    private final CardLayout contentCards = new CardLayout();
    private final pnlEmployees employeesPanel;
    private final pnlAttendance attendancePanel = new pnlAttendance(true, ADMIN_ATTENDANCE_FILTERS);
    private final pnlLeaveRequest leaveRequestPanel;
    private final PayrollDashboard payrollPanel;
    private final UserAccountsPanel userAccountsPanel;
    private final CsvStatusPanel csvStatusPanel = new CsvStatusPanel();
    private final AuditLogPanel auditLogPanel = new AuditLogPanel();

    private JPanel contentPanel;
    private JButton btnEmployees;
    private JButton btnAttendance;
    private JButton btnLeaveRequests;
    private JButton btnPayroll;
    private JButton btnUserAccounts;
    private JButton btnCsvPaths;
    private JButton btnAuditLog;
    private JButton btnLogout;

    public AdminDashboardFrame(User user) {
        BrandTheme.installGlobalTheme();
        this.user = user;
        this.employeesPanel = new pnlEmployees(user, true, ADMIN_EMPLOYEE_FILTERS);
        this.leaveRequestPanel = new pnlLeaveRequest(user, true, ADMIN_LEAVE_FILTERS);
        this.payrollPanel = new PayrollDashboard(user);
        this.userAccountsPanel = new UserAccountsPanel(user);
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 780));
        setResizable(true);
        setSize(1320, 860);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(BrandTheme.IVORY);

        getContentPane().add(buildHeader(user), BorderLayout.NORTH);
        getContentPane().add(buildNavigation(), BorderLayout.WEST);
        getContentPane().add(buildContentArea(), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(BrandTheme.GRAPHITE);
        navigation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BrandTheme.LAVENDER),
                BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));
        navigation.setPreferredSize(new Dimension(210, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = createMenuGroupLabel("Administration");
        JLabel operationsLabel = createMenuGroupLabel("Operations");
        JLabel systemLabel = createMenuGroupLabel("System Tools");

        btnEmployees = createNavigationButton("Employees");
        btnAttendance = createNavigationButton("Attendance");
        btnLeaveRequests = createNavigationButton("Leave Requests");
        btnPayroll = createNavigationButton("Payroll");
        btnUserAccounts = createNavigationButton("User Accounts");
        btnCsvPaths = createNavigationButton("CSV Paths");
        btnAuditLog = createNavigationButton("Audit Log");
        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

        btnEmployees.addActionListener(evt -> {
            employeesPanel.reloadEmployees();
            contentCards.show(contentPanel, "employees");
        });
        btnAttendance.addActionListener(evt -> {
            attendancePanel.reloadAttendance();
            contentCards.show(contentPanel, "attendance");
        });
        btnLeaveRequests.addActionListener(evt -> {
            leaveRequestPanel.reloadRequests();
            contentCards.show(contentPanel, "leave");
        });
        btnPayroll.addActionListener(evt -> {
            payrollPanel.refreshDashboard();
            contentCards.show(contentPanel, "payroll");
        });
        btnUserAccounts.addActionListener(evt -> {
            userAccountsPanel.reloadUsers();
            contentCards.show(contentPanel, "accounts");
        });
        btnCsvPaths.addActionListener(evt -> {
            csvStatusPanel.reloadPaths();
            contentCards.show(contentPanel, "paths");
        });
        btnAuditLog.addActionListener(evt -> {
            auditLogPanel.reloadAuditLog();
            contentCards.show(contentPanel, "audit");
        });

        menuButtons.add(menuLabel);
        menuButtons.add(Box.createVerticalStrut(12));
        menuButtons.add(operationsLabel);
        menuButtons.add(Box.createVerticalStrut(8));
        menuButtons.add(btnEmployees);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnAttendance);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnLeaveRequests);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnPayroll);
        menuButtons.add(Box.createVerticalStrut(16));
        menuButtons.add(systemLabel);
        menuButtons.add(Box.createVerticalStrut(8));
        menuButtons.add(btnUserAccounts);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnCsvPaths);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnAuditLog);
        menuButtons.add(Box.createVerticalGlue());

        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.add(btnLogout);

        navigation.add(menuButtons, BorderLayout.CENTER);
        navigation.add(footerPanel, BorderLayout.SOUTH);
        return navigation;
    }

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        configureNavigationButton(button);
        return button;
    }

    private JLabel createMenuGroupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        label.setForeground(BrandTheme.MUTED_INVERSE);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void configureNavigationButton(JButton button) {
        BrandTheme.styleNavigationButton(button);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.setMinimumSize(new Dimension(176, 36));
        button.setPreferredSize(new Dimension(176, 36));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JPanel buildHeader(User user) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        BrandTheme.styleDarkSurface(header);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BrandTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        header.add(new JLabel(BrandTheme.loadHeaderLogoIcon()), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        BrandTheme.styleDarkSurface(textPanel);

        JLabel title = new JLabel("Admin Workspace", SwingConstants.LEFT);
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 18f));
        title.setForeground(BrandTheme.TEXT_INVERSE);

        JLabel subtitle = new JLabel(BrandTheme.TAGLINE);
        subtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.ITALIC));
        subtitle.setForeground(BrandTheme.MUTED_INVERSE);

        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        BrandTheme.styleDarkSurface(actions);

        JButton notificationsButton = DashboardNavigation.createNotificationButton(this);
        notificationsButton.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));

        actions.add(notificationsButton);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContentArea() {
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(BrandTheme.NAVY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(employeesPanel, "employees");
        contentPanel.add(attendancePanel, "attendance");
        contentPanel.add(leaveRequestPanel, "leave");
        contentPanel.add(payrollPanel, "payroll");
        contentPanel.add(userAccountsPanel, "accounts");
        contentPanel.add(csvStatusPanel, "paths");
        contentPanel.add(auditLogPanel, "audit");
        contentCards.show(contentPanel, "employees");
        return contentPanel;
    }
}

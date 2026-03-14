package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AppClock;
import com.mycompany.motorph.service.AttendanceService;
import com.mycompany.motorph.service.EmployeePortalService;
import com.mycompany.motorph.service.LeaveService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class EmployeeDashboard extends JFrame {

    private static final DecimalFormat MONEY = new DecimalFormat("PHP #,##0.00");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MMM dd");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter CSV_SLASH_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Set<String> EMPLOYEE_BADGE_ACTIONS = Set.of(
            "LEAVE_RESPONSE",
            "LEAVE_APPROVED",
            "LEAVE_REJECTED",
            "HR_MESSAGE",
            "ATTENDANCE_FLAG"
    );

    private final User user;
    private final List<User> users;
    private final EmployeePortalService employeePortalService = new EmployeePortalService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final LeaveService leaveService = new LeaveService();
    private final NotificationService notificationService = new NotificationService();
    private final Employee employee;
    private final int displayDataYear;
    private final List<PayrollPeriodOption> payrollPeriods;
    private final List<EmployeePayrollSummary> payrollHistory;
    private final LocalDate calendarReferenceDate;

    private final CardLayout contentCards = new CardLayout();
    private JPanel contentPanel;
    private JButton btnHome;
    private JButton btnProfile;
    private JButton btnLeaveRequests;
    private JButton btnGovernmentIds;
    private JButton btnPayrollSummary;
    private JButton btnPayslipHistory;
    private JButton btnChangePassword;
    private JButton headerNotificationButton;
    private NotificationBadgeLabel notificationBadgeLabel;
    private JButton btnLogout;

    public EmployeeDashboard(User user, List<User> users) {
        this.user = user;
        this.users = users;
        this.employee = employeePortalService.getEmployeeByNumber(user.getEmployeeNumber());
        List<String[]> attendanceHistory = attendanceService.getAttendanceHistory(user.getEmployeeNumber());
        this.displayDataYear = resolveDisplayDataYear(attendanceHistory);
        this.payrollPeriods = filterPayrollPeriodsByYear(
                employeePortalService.getAvailablePayrollPeriods(user.getEmployeeNumber()),
                displayDataYear
        );
        this.payrollHistory = filterPayrollHistoryByYear(
                employeePortalService.getPayrollHistory(user.getEmployeeNumber()),
                displayDataYear
        );
        this.calendarReferenceDate = resolveCalendarReferenceDate();
        initComponents();
    }

    public EmployeeDashboard() {
        this(new com.mycompany.motorph.model.EmployeeUser("10001", "emp10001", 10001), List.of());
    }

    private void initComponents() {
        setTitle("Employee Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 760));
        setResizable(true);
        setSize(1280, 820);
        getContentPane().setBackground(BrandTheme.IVORY);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(buildNavigation(), BorderLayout.WEST);
        getContentPane().add(buildContentArea(), BorderLayout.CENTER);

        refreshNotificationBell();
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshNotificationBell();
            }
        });
        setLocationRelativeTo(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        BrandTheme.styleDarkSurface(header);

        JLabel logoLabel = new JLabel(BrandTheme.loadHeaderLogoIcon());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        BrandTheme.styleDarkSurface(titlePanel);

        JLabel titleLabel = new JLabel(getEmployeeWelcomeText());
        BrandTheme.setWelcomeText(titleLabel, getEmployeeWelcomeText(), true);
        titleLabel.setFont(BrandTheme.TITLE_FONT);

        JLabel subtitleLabel = new JLabel(BrandTheme.TAGLINE);
        subtitleLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.ITALIC));
        subtitleLabel.setForeground(BrandTheme.MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.X_AXIS));
        BrandTheme.styleDarkSurface(actionsPanel);
        actionsPanel.add(buildHeaderNotificationControl());

        header.add(logoLabel, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(actionsPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(BrandTheme.GRAPHITE);
        navigation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BrandTheme.LAVENDER),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));
        navigation.setPreferredSize(new Dimension(210, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("Workspace Menu");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 12f));
        menuLabel.setForeground(BrandTheme.MUTED);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnHome = createMenuButton("🏠  Home", "home");
        btnProfile = createMenuButton("👤  Profile", "profile");
        btnLeaveRequests = createMenuButton("📅  Leave Requests", "leave");
        btnGovernmentIds = createMenuButton("🪪  Government IDs", "govIds");
        btnPayrollSummary = createMenuButton("📄  View Payslip", "summary");
        btnPayslipHistory = createMenuButton("📜  Payslip History", "history");
        btnChangePassword = new JButton("Password");
        configureNavigationButton(btnChangePassword);
        btnChangePassword.addActionListener(evt -> openChangePasswordWindow());

        menuButtons.add(menuLabel);
        menuButtons.add(Box.createVerticalStrut(12));
        menuButtons.add(btnHome);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnProfile);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnLeaveRequests);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnGovernmentIds);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnPayrollSummary);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnPayslipHistory);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnChangePassword);
        menuButtons.add(Box.createVerticalGlue());

        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("🚪  Logout");

        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.add(btnLogout);

        navigation.add(menuButtons, BorderLayout.CENTER);
        navigation.add(footerPanel, BorderLayout.SOUTH);
        return navigation;
    }

    private JPanel buildContentArea() {
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(BrandTheme.NAVY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        contentPanel.add(new DashboardHomePanel(), "home");
        contentPanel.add(new InfoTablePanel(
                "👤 Employee Profile",
                new String[]{"Field", "Value"},
                buildProfileRows()
        ), "profile");
        contentPanel.add(new AttendancePanel(), "attendance");
        contentPanel.add(new LeaveRequestPanel(), "leave");
        contentPanel.add(new InfoTablePanel(
                "🪪 Government IDs",
                new String[]{"ID Type", "Number"},
                buildGovernmentRows()
        ), "govIds");
        contentPanel.add(new PayrollSummaryPanel(), "summary");
        contentPanel.add(new PayrollHistoryPanel(), "history");

        contentCards.show(contentPanel, "home");
        return contentPanel;
    }

    private JButton createMenuButton(String label, String cardName) {
        JButton button = new JButton(label);
        configureNavigationButton(button);
        button.addActionListener(evt -> contentCards.show(contentPanel, cardName));
        return button;
    }

    private void configureNavigationButton(JButton button) {
        BrandTheme.styleNavigationButton(button);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(BrandTheme.BUTTON_FONT);
        button.setMinimumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JLayeredPane buildHeaderNotificationControl() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setPreferredSize(new Dimension(172, 44));
        layeredPane.setMinimumSize(new Dimension(172, 44));
        layeredPane.setMaximumSize(new Dimension(172, 44));

        headerNotificationButton = new JButton("Notifications 🔔");
        configureHeaderNotificationButton(headerNotificationButton);
        headerNotificationButton.addActionListener(evt -> openEmployeeNotifications());
        headerNotificationButton.setBounds(0, 2, 164, 40);

        notificationBadgeLabel = new NotificationBadgeLabel();
        notificationBadgeLabel.setBounds(142, 0, 22, 22);
        notificationBadgeLabel.setVisible(false);

        layeredPane.add(headerNotificationButton, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(notificationBadgeLabel, JLayeredPane.PALETTE_LAYER);
        return layeredPane;
    }

    private void configureHeaderNotificationButton(JButton button) {
        BrandTheme.styleSecondaryButton(button);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setToolTipText("Open system notifications");
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 18f));
        label.setForeground(BrandTheme.TEXT);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        label.setForeground(BrandTheme.TEXT);
        return label;
    }

    private void setControlWidth(JTextField field, int width) {
        Dimension size = new Dimension(width, 34);
        field.setPreferredSize(size);
        field.setMinimumSize(size);
        field.setMaximumSize(size);
    }

    private void setControlWidth(JComboBox<?> comboBox, int width) {
        Dimension size = new Dimension(width, 34);
        comboBox.setPreferredSize(size);
        comboBox.setMinimumSize(size);
        comboBox.setMaximumSize(size);
    }

    private void setControlWidth(DatePickerField datePickerField, int width) {
        Dimension size = new Dimension(width, 34);
        datePickerField.setPreferredSize(size);
        datePickerField.setMinimumSize(size);
        datePickerField.setMaximumSize(size);
    }

    private void setButtonSize(JButton button, int width) {
        Dimension size = new Dimension(width, 38);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
    }

    private JPanel buildSectionHeader(String titleText, JPanel actionsPanel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createSectionTitle(titleText), gbc);

        if (actionsPanel != null) {
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(actionsPanel, gbc);
        }

        return panel;
    }

    private String[][] buildProfileRows() {
        if (employee == null) {
            return new String[][]{{"Status", "Employee record not found"}};
        }

        return new String[][]{
            {"Employee Number", String.valueOf(employee.getEmployeeNumber())},
            {"Full Name", employee.getEmployeeName()},
            {"Position", resolveProfilePosition()},
            {"Employment Status", employee.getStatus()},
            {"Immediate Supervisor", resolveProfileSupervisor()},
            {"Birth Date", formatDisplayDate(employee.getBirthDate())},
            {"Phone Number", employee.getPhone()},
            {"Address", employee.getAddress()},
            {"Basic Salary", MONEY.format(employee.getBasicSalary())},
            {"Rice Subsidy", MONEY.format(employee.getRiceSubsidy())},
            {"Phone Allowance", MONEY.format(employee.getPhoneAllowance())},
            {"Clothing Allowance", MONEY.format(employee.getClothingAllowance())}
        };
    }

    private String[][] buildGovernmentRows() {
        if (employee == null) {
            return new String[][]{{"Status", "Employee record not found"}};
        }

        return new String[][]{
            {"SSS", employee.getSss()},
            {"PhilHealth", employee.getPhilhealth()},
            {"TIN", employee.getTin()},
            {"Pag-IBIG", employee.getPagibig()}
        };
    }

    private void openChangePasswordWindow() {
        JFrame frame = new JFrame("Change Password");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setContentPane(new pnlDefaultPassword(user, users, frame::dispose, () -> {
        }, true));
        frame.setMinimumSize(new Dimension(700, 460));
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private String getEmployeeWelcomeText() {
        if (employee == null) {
            return "Welcome, employee " + user.getEmployeeNumber() + " | Employee Workspace";
        }
        return "Welcome, " + employee.getEmployeeName() + " | Employee Workspace";
    }

    private EmployeePayrollSummary getLatestSummary() {
        return payrollHistory.isEmpty() ? null : payrollHistory.get(0);
    }

    private LocalDate resolveCalendarReferenceDate() {
        return AppClock.today();
    }

    private int resolveDisplayDataYear(List<String[]> attendanceRows) {
        Map<Integer, Integer> counts = new HashMap<>();
        int bestYear = 0;
        int bestCount = -1;

        for (String[] row : attendanceRows) {
            if (row.length <= 3) {
                continue;
            }

            try {
                int year = LocalDate.parse(row[3].trim(), CSV_SLASH_DATE).getYear();
                int count = counts.getOrDefault(year, 0) + 1;
                counts.put(year, count);
                if (count > bestCount || (count == bestCount && (bestYear == 0 || year < bestYear))) {
                    bestYear = year;
                    bestCount = count;
                }
            } catch (Exception ignored) {
            }
        }

        if (bestYear != 0) {
            return bestYear;
        }

        return AppClock.today().getYear();
    }

    private List<PayrollPeriodOption> filterPayrollPeriodsByYear(List<PayrollPeriodOption> options, int year) {
        if (options == null || options.isEmpty() || year <= 0) {
            return options == null ? List.of() : options;
        }

        List<PayrollPeriodOption> filtered = new ArrayList<>();
        for (PayrollPeriodOption option : options) {
            if (option.getStartDate().getYear() == year || option.getEndDate().getYear() == year) {
                filtered.add(option);
            }
        }
        return filtered.isEmpty() ? options : filtered;
    }

    private List<EmployeePayrollSummary> filterPayrollHistoryByYear(List<EmployeePayrollSummary> summaries, int year) {
        if (summaries == null || summaries.isEmpty() || year <= 0) {
            return summaries == null ? List.of() : summaries;
        }

        List<EmployeePayrollSummary> filtered = new ArrayList<>();
        for (EmployeePayrollSummary summary : summaries) {
            PayrollPeriodOption period = summary.getPeriod();
            if (period.getStartDate().getYear() == year || period.getEndDate().getYear() == year) {
                filtered.add(summary);
            }
        }
        return filtered.isEmpty() ? summaries : filtered;
    }

    private String resolveProfilePosition() {
        if (employee == null) {
            return "Not Available";
        }

        String position = employee.getPosition() == null ? "" : employee.getPosition().trim();
        return position.isBlank() ? "Not Available" : position;
    }

    private String resolveProfileSupervisor() {
        if (employee == null) {
            return "Not Available";
        }

        String supervisor = employee.getSupervisor() == null ? "" : employee.getSupervisor().trim();
        return supervisor.isBlank() ? "Not Available" : supervisor;
    }

    private void refreshNotificationBell() {
        if (headerNotificationButton == null || notificationBadgeLabel == null) {
            return;
        }

        int unreadCount = 0;
        for (NotificationEntry notification : getEmployeeNotifications()) {
            if (shouldTriggerBadge(notification)) {
                unreadCount++;
            }
        }

        headerNotificationButton.setToolTipText(unreadCount > 0
                ? unreadCount + " unread notifications"
                : "Open system notifications");

        if (unreadCount > 0) {
            notificationBadgeLabel.setText(unreadCount > 99 ? "99" : String.valueOf(unreadCount));
            notificationBadgeLabel.setVisible(true);
        } else {
            notificationBadgeLabel.setVisible(false);
            notificationBadgeLabel.setText("");
        }
    }

    private void openEmployeeNotifications() {
        List<NotificationEntry> notifications = getEmployeeNotifications();
        notificationService.markAsRead(notifications);
        NotificationCenterDialog.showDialog(
                this,
                "System Notifications",
                "",
                this::getEmployeeNotifications
        );
        refreshNotificationBell();
    }

    private List<NotificationEntry> getEmployeeNotifications() {
        List<NotificationEntry> relevantNotifications = new ArrayList<>();

        for (NotificationEntry notification : notificationService.getRecentNotifications(300)) {
            if (isEmployeeRelevantNotification(notification)) {
                relevantNotifications.add(notification);
            }
        }

        return relevantNotifications;
    }

    private boolean isUnreadNotification(NotificationEntry notification) {
        return notification != null && !notification.isRead();
    }

    private boolean isEmployeeRelevantNotification(NotificationEntry notification) {
        String details = normalize(notification == null ? "" : notification.getDetails());
        String username = normalize(user == null ? "" : user.getUsername());
        String employeeNumber = normalize(user == null ? "" : String.valueOf(user.getEmployeeNumber()));

        if (isSelfNotification(notification)) {
            return true;
        }

        return details.contains("employee " + employeeNumber)
                || details.contains(employeeNumber + " -")
                || details.contains(" " + employeeNumber + ".")
                || (!username.isBlank() && details.contains(username));
    }

    private boolean shouldTriggerBadge(NotificationEntry notification) {
        if (!isUnreadNotification(notification) || !isEmployeeRelevantNotification(notification)) {
            return false;
        }

        if (isSelfNotification(notification)) {
            return false;
        }

        String action = normalize(notification.getAction()).toUpperCase();
        return EMPLOYEE_BADGE_ACTIONS.contains(action);
    }

    private boolean isSelfNotification(NotificationEntry notification) {
        String actor = normalize(notification == null ? "" : notification.getActor());
        String username = normalize(user == null ? "" : user.getUsername());
        String employeeNumber = normalize(user == null ? "" : String.valueOf(user.getEmployeeNumber()));
        return !actor.isBlank() && (actor.equals(username) || actor.equals(employeeNumber));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String formatDisplayDate(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return "Not Available";
        }

        String value = rawValue.trim();
        try {
            return DISPLAY_DATE.format(LocalDate.parse(value));
        } catch (Exception ignored) {
        }

        try {
            return DISPLAY_DATE.format(LocalDate.parse(value, CSV_SLASH_DATE));
        } catch (Exception ignored) {
        }

        return value;
    }

    private String formatPeriod(PayrollPeriodOption option) {
        if (option == null) {
            return "Not Available";
        }

        if (option.getType() == PayrollPeriodOption.Type.MONTHLY) {
            return MONTH_YEAR.format(option.getStartDate());
        }

        if (option.getStartDate().getYear() == option.getEndDate().getYear()
                && option.getStartDate().getMonth() == option.getEndDate().getMonth()) {
            return MONTH_DAY.format(option.getStartDate()) + " - " + DISPLAY_DATE.format(option.getEndDate());
        }

        return DISPLAY_DATE.format(option.getStartDate()) + " - " + DISPLAY_DATE.format(option.getEndDate());
    }

    private PayrollPeriodOption findPeriodForDate(LocalDate selectedDate) {
        if (payrollPeriods.isEmpty()) {
            return null;
        }

        if (selectedDate == null) {
            return payrollPeriods.get(0);
        }

        PayrollPeriodOption monthlyFallback = null;
        for (PayrollPeriodOption option : payrollPeriods) {
            if (selectedDate.isBefore(option.getStartDate()) || selectedDate.isAfter(option.getEndDate())) {
                continue;
            }

            if (option.getType() == PayrollPeriodOption.Type.SEMI_MONTHLY) {
                return option;
            }
            monthlyFallback = option;
        }

        return monthlyFallback;
    }

    private LocalDate resolveLatestPayrollPeriodDate() {
        if (payrollPeriods.isEmpty()) {
            return calendarReferenceDate;
        }
        return payrollPeriods.get(0).getEndDate();
    }

    private static class SurfacePanel extends JPanel {

        SurfacePanel() {
            super(new BorderLayout(0, 16));
            BrandTheme.styleSurface(this);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));
        }
    }

    private class DashboardHomePanel extends SurfacePanel {

        DashboardHomePanel() {
            JPanel top = new JPanel(new GridLayout(1, 2, 16, 16));
            top.setOpaque(false);

            top.add(buildWelcomeCard());
            top.add(buildAttendanceCard());

            JPanel body = new JPanel(new GridBagLayout());
            body.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;

            gbc.gridy = 0;
            gbc.weighty = 0.56;
            body.add(new AttendancePanel("⏱ Attendance Overview"), gbc);

            gbc.gridy = 1;
            gbc.weighty = 0.44;
            gbc.insets = new Insets(16, 0, 0, 0);
            body.add(new PayrollSummaryPanel(true), gbc);

            add(top, BorderLayout.NORTH);
            add(body, BorderLayout.CENTER);
        }

        private JPanel buildWelcomeCard() {
            JPanel panel = new JPanel();
            BrandTheme.styleCardSurface(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel title = createSectionTitle("👋 Welcome");

            JLabel body = new JLabel(employee == null
                    ? "Employee record unavailable."
                    : "<html>" + employee.getEmployeeName() + "<br/>" + resolveProfilePosition() + "<br/>" + employee.getStatus() + "</html>");
            body.setFont(BrandTheme.BODY_FONT.deriveFont(16f));
            body.setForeground(BrandTheme.TEXT);

            panel.add(title);
            panel.add(Box.createVerticalStrut(12));
            panel.add(body);
            return panel;
        }

        private JPanel buildAttendanceCard() {
            EmployeePayrollSummary latest = getLatestSummary();
            JPanel panel = new JPanel();
            BrandTheme.styleCardSurface(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel title = createSectionTitle("📊 Latest Attendance Overview");

            JLabel body = new JLabel(latest == null
                    ? "No attendance-backed payroll periods available yet."
                    : "<html>Period: " + formatPeriod(latest.getPeriod())
                    + "<br/>Days Worked: " + latest.getAttendanceDays()
                    + "<br/>Hours Worked: " + String.format("%.2f", latest.getAttendanceHours()) + "</html>");
            body.setFont(BrandTheme.BODY_FONT.deriveFont(16f));
            body.setForeground(BrandTheme.TEXT);

            panel.add(title);
            panel.add(Box.createVerticalStrut(12));
            panel.add(body);
            return panel;
        }
    }

    private class InfoTablePanel extends SurfacePanel {

        InfoTablePanel(String titleText, String[] columns, String[][] rows) {
            JLabel title = createSectionTitle(titleText);

            JTable table = new JTable(new DefaultTableModel(rows, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(table);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            add(title, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(table);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    private class AttendancePanel extends SurfacePanel {

        private final JLabel statusLabel = new JLabel(" ");
        private final JTable attendanceTable = new JTable();

        AttendancePanel() {
            this("⏱ Attendance Overview");
        }

        AttendancePanel(String titleText) {
            JButton btnTimeIn = new JButton("Time In");
            JButton btnTimeOut = new JButton("Time Out");
            JButton btnRefresh = new JButton("Refresh");
            BrandTheme.stylePrimaryButton(btnTimeIn);
            BrandTheme.styleSecondaryButton(btnTimeOut);
            BrandTheme.styleSecondaryButton(btnRefresh);
            setButtonSize(btnTimeIn, 96);
            setButtonSize(btnTimeOut, 96);
            setButtonSize(btnRefresh, 96);

            btnTimeIn.addActionListener(evt -> handleTimeIn());
            btnTimeOut.addActionListener(evt -> handleTimeOut());
            btnRefresh.addActionListener(evt -> reloadAttendance());

            JPanel actions = new JPanel();
            actions.setOpaque(false);
            actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
            actions.add(btnTimeIn);
            actions.add(Box.createHorizontalStrut(10));
            actions.add(btnTimeOut);
            actions.add(Box.createHorizontalStrut(10));
            actions.add(btnRefresh);

            statusLabel.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
            statusLabel.setForeground(BrandTheme.MUTED);

            attendanceTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Date", "Time In", "Time Out", "Status"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(attendanceTable);

            add(buildSectionHeader(titleText, actions), BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(attendanceTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.SOUTH);

            reloadAttendance();
        }

        private void handleTimeIn() {
            String result = attendanceService.timeIn(employee);
            notificationService.record(user, "ATTENDANCE_TIME_IN", result + " Employee " + user.getEmployeeNumber() + ".");
            refreshNotificationBell();
            JOptionPane.showMessageDialog(this, result, "Attendance", JOptionPane.INFORMATION_MESSAGE);
            reloadAttendance();
        }

        private void handleTimeOut() {
            String result = attendanceService.timeOut(employee);
            notificationService.record(user, "ATTENDANCE_TIME_OUT", result + " Employee " + user.getEmployeeNumber() + ".");
            refreshNotificationBell();
            JOptionPane.showMessageDialog(this, result, "Attendance", JOptionPane.INFORMATION_MESSAGE);
            reloadAttendance();
        }

        private void reloadAttendance() {

            DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
            model.setRowCount(0);

            int shown = 0;
            for (String[] row : attendanceService.getAttendanceHistory(user.getEmployeeNumber())) {
                if (row.length <= 5) {
                    continue;
                }

                String attendanceStatus = row[5] == null || row[5].trim().isEmpty() ? "Open" : "Complete";
                model.addRow(new Object[]{formatDisplayDate(row[3]), row[4], row[5], attendanceStatus});
                shown++;
                if (shown >= 12) {
                    break;
                }
            }

            statusLabel.setText(attendanceService.getTodayStatus(employee));
        }
    }

    private class LeaveRequestPanel extends SurfacePanel {

        private final JComboBox<String> leaveTypeSelector = new JComboBox<>(new String[]{
            "Vacation Leave", "Sick Leave", "Emergency Leave", "Bereavement Leave"
        });
        private final DatePickerField startDateField = new DatePickerField();
        private final DatePickerField endDateField = new DatePickerField();
        private final JTable requestsTable = new JTable();
        private final JLabel summaryLabel = new JLabel(" ");

        LeaveRequestPanel() {
            JButton btnSubmit = new JButton("Submit Leave");
            JButton btnRefresh = new JButton("Refresh");
            BrandTheme.stylePrimaryButton(btnSubmit);
            BrandTheme.styleSecondaryButton(btnRefresh);
            BrandTheme.styleComboBox(leaveTypeSelector);
            setControlWidth(leaveTypeSelector, 170);
            setControlWidth(startDateField, 184);
            setControlWidth(endDateField, 184);
            startDateField.setDefaultDate(calendarReferenceDate);
            endDateField.setDefaultDate(calendarReferenceDate);
            setButtonSize(btnSubmit, 116);
            setButtonSize(btnRefresh, 96);
            btnSubmit.addActionListener(evt -> submitLeaveRequest());
            btnRefresh.addActionListener(evt -> reloadRequests());

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 0, 10);
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0;
            form.add(createFieldLabel("Leave Type"), gbc);
            gbc.gridx = 1;
            form.add(leaveTypeSelector, gbc);
            gbc.gridx = 2;
            form.add(createFieldLabel("Start Date"), gbc);
            gbc.gridx = 3;
            form.add(startDateField, gbc);
            gbc.gridx = 4;
            form.add(createFieldLabel("End Date"), gbc);
            gbc.gridx = 5;
            form.add(endDateField, gbc);
            gbc.gridx = 6;
            form.add(btnSubmit, gbc);
            gbc.gridx = 7;
            gbc.insets = new Insets(0, 0, 0, 0);
            form.add(btnRefresh, gbc);

            JPanel north = new JPanel(new BorderLayout(0, 12));
            north.setOpaque(false);
            north.add(buildSectionHeader("📅 Leave Requests", null), BorderLayout.NORTH);
            north.add(form, BorderLayout.CENTER);

            requestsTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Leave Type", "Start Date", "End Date", "Status", "Remarks"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(requestsTable);

            summaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
            summaryLabel.setForeground(BrandTheme.MUTED);

            add(north, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(requestsTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(summaryLabel, BorderLayout.SOUTH);

            reloadRequests();
        }

        private void submitLeaveRequest() {

            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee record unavailable.");
                return;
            }

            try {
                LocalDate startDate = startDateField.getDate();
                LocalDate endDate = endDateField.getDate();

                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(this, "Select both the start date and end date.");
                    return;
                }

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(this, "End date cannot be earlier than start date.");
                    return;
                }

                LeaveRequest request = new LeaveRequest(
                        employee.getEmployeeNumber(),
                        employee.getEmployeeName(),
                        String.valueOf(leaveTypeSelector.getSelectedItem()),
                        startDate.toString(),
                        endDate.toString()
                );

                leaveService.submitLeave(request);
                notificationService.record(
                        user,
                        "LEAVE_SUBMITTED",
                        "Submitted " + request.getLeaveType() + " from " + request.getStartDate() + " to " + request.getEndDate() + "."
                );
                refreshNotificationBell();
                startDateField.setDate(null);
                endDateField.setDate(null);
                reloadRequests();
                JOptionPane.showMessageDialog(this, "Leave request submitted.");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Select valid leave dates from the calendar picker.", "Leave Request", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void reloadRequests() {

            DefaultTableModel model = (DefaultTableModel) requestsTable.getModel();
            model.setRowCount(0);

            List<LeaveRequest> employeeRequests = leaveService.getRequestsForEmployee(user.getEmployeeNumber());
            for (LeaveRequest request : employeeRequests) {
                model.addRow(new Object[]{
                    request.getLeaveType(),
                    formatDisplayDate(request.getStartDate()),
                    formatDisplayDate(request.getEndDate()),
                    request.getStatus(),
                    request.getStatusMessage()
                });
            }

            summaryLabel.setText("Showing " + employeeRequests.size() + " leave requests. Leave requests are approved by HR or Admin.");
        }
    }

    private class PayrollSummaryPanel extends SurfacePanel {

        private final DatePickerField periodSelector = new DatePickerField();
        private final JTable summaryTable = new JTable();
        private final JLabel totalPayAmountLabel = new JLabel(MONEY.format(0), SwingConstants.LEFT);
        private final boolean latestOnly;

        PayrollSummaryPanel() {
            this(false);
        }

        PayrollSummaryPanel(boolean latestOnly) {
            this.latestOnly = latestOnly;
            JPanel actionsPanel = null;

            if (!latestOnly) {
                JPanel selectorPanel = new JPanel();
                selectorPanel.setOpaque(false);
                selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));
                JLabel selectorLabel = createFieldLabel("Select Pay Period");
                periodSelector.setDefaultDate(resolveLatestPayrollPeriodDate());
                setControlWidth(periodSelector, 220);
                selectorPanel.add(selectorLabel);
                selectorPanel.add(Box.createHorizontalStrut(10));
                selectorPanel.add(periodSelector);
                actionsPanel = selectorPanel;
            }

            summaryTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Payslip Item", "Amount"}) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(summaryTable);

            add(buildSectionHeader(latestOnly ? "📄 View Payslip" : "📄 View Payslip", actionsPanel), BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(summaryTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(buildTotalPayPanel(), BorderLayout.SOUTH);

            if (!latestOnly) {
                if (!payrollPeriods.isEmpty()) {
                    periodSelector.setDate(resolveLatestPayrollPeriodDate());
                }
                periodSelector.setOnDateChange(this::refreshSummary);
            }
            refreshSummary();
        }

        private JPanel buildTotalPayPanel() {
            JPanel panel = new JPanel();
            panel.setOpaque(true);
            panel.setBackground(BrandTheme.PAPER);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BrandTheme.ROYAL, 1),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)
            ));

            JLabel totalPayHeaderLabel = new JLabel("Total Net Pay for This Period");
            totalPayHeaderLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
            totalPayHeaderLabel.setForeground(BrandTheme.TEXT);
            totalPayHeaderLabel.setAlignmentX(LEFT_ALIGNMENT);

            totalPayAmountLabel.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 34f));
            totalPayAmountLabel.setForeground(BrandTheme.GOLD);
            totalPayAmountLabel.setAlignmentX(LEFT_ALIGNMENT);

            panel.add(totalPayHeaderLabel);
            panel.add(Box.createVerticalStrut(6));
            panel.add(totalPayAmountLabel);
            return panel;
        }

        private void refreshSummary() {
            PayrollPeriodOption selected = latestOnly
                    ? (payrollPeriods.isEmpty() ? null : payrollPeriods.get(0))
                    : findPeriodForDate(periodSelector.getDate());
            DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
            model.setRowCount(0);

            if (selected == null) {
                model.addRow(new Object[]{"Status", latestOnly
                        ? "No payroll periods available."
                        : "No payslip period is available."});
                totalPayAmountLabel.setText(MONEY.format(0));
                return;
            }

            EmployeePayrollSummary summary = employeePortalService.getPayrollSummary(user.getEmployeeNumber(), selected.getKey());
            if (summary == null) {
                model.addRow(new Object[]{"Status", "No payslip data available."});
                totalPayAmountLabel.setText(MONEY.format(0));
                return;
            }

            model.addRow(new Object[]{"Pay Period", formatPeriod(summary.getPeriod())});
            model.addRow(new Object[]{"Basic Salary", MONEY.format(summary.getBasicSalary())});
            model.addRow(new Object[]{"Rice Subsidy", MONEY.format(summary.getRiceSubsidy())});
            model.addRow(new Object[]{"Phone Allowance", MONEY.format(summary.getPhoneAllowance())});
            model.addRow(new Object[]{"Clothing Allowance", MONEY.format(summary.getClothingAllowance())});
            model.addRow(new Object[]{"Gross Salary", MONEY.format(summary.getGrossSalary())});
            model.addRow(new Object[]{"SSS", MONEY.format(summary.getSss())});
            model.addRow(new Object[]{"PhilHealth", MONEY.format(summary.getPhilhealth())});
            model.addRow(new Object[]{"Pag-IBIG", MONEY.format(summary.getPagibig())});
            model.addRow(new Object[]{"Withholding Tax", MONEY.format(summary.getWithholdingTax())});
            model.addRow(new Object[]{"Total Deductions", MONEY.format(summary.getTotalDeductions())});
            model.addRow(new Object[]{"Net Pay", MONEY.format(summary.getNetSalary())});
            model.addRow(new Object[]{"Attendance Days", summary.getAttendanceDays()});
            model.addRow(new Object[]{"Attendance Hours", String.format("%.2f", summary.getAttendanceHours())});
            totalPayAmountLabel.setText(MONEY.format(summary.getNetSalary()));
        }
    }

    private class PayrollHistoryPanel extends SurfacePanel {

        private final DatePickerField filterSelector = new DatePickerField();
        private final JTable historyTable = new JTable();
        private final JTable detailTable = new JTable();
        private List<EmployeePayrollSummary> visibleRows = new ArrayList<>();

        PayrollHistoryPanel() {
            JPanel filterPanel = new JPanel();
            filterPanel.setOpaque(false);
            filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
            JLabel filterLabel = createFieldLabel("Payslip View");
            filterPanel.add(filterLabel);
            filterPanel.add(Box.createHorizontalStrut(10));
            filterSelector.setDefaultDate(resolveLatestPayrollPeriodDate());
            setControlWidth(filterSelector, 220);
            filterPanel.add(filterSelector);

            historyTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Period", "Type", "Days Worked", "Gross Salary", "Net Pay"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(historyTable);
            historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            detailTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Breakdown Item", "Value"}) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleDashboardTable(detailTable);

            JPanel center = new JPanel(new GridLayout(2, 1, 0, 16));
            center.setOpaque(false);
            JScrollPane historyScrollPane = new JScrollPane(historyTable);
            JScrollPane detailScrollPane = new JScrollPane(detailTable);
            BrandTheme.styleScrollPane(historyScrollPane);
            BrandTheme.styleScrollPane(detailScrollPane);
            center.add(historyScrollPane);
            center.add(detailScrollPane);

            add(buildSectionHeader("📜 Payslip History", filterPanel), BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);

            if (!payrollPeriods.isEmpty()) {
                filterSelector.setDate(resolveLatestPayrollPeriodDate());
            }
            filterSelector.setOnDateChange(this::refreshHistory);
            historyTable.getSelectionModel().addListSelectionListener(evt -> {
                if (!evt.getValueIsAdjusting()) {
                    refreshDetail();
                }
            });

            refreshHistory();
        }

        private void refreshHistory() {
            LocalDate selectedDate = filterSelector.getDate();
            PayrollPeriodOption selected = findPeriodForDate(selectedDate);
            visibleRows = new ArrayList<>();
            if (selectedDate != null && selected == null) {
                visibleRows.clear();
            } else if (selected != null) {
                for (EmployeePayrollSummary summary : payrollHistory) {
                    if (summary.getPeriod().getKey().equals(selected.getKey())) {
                        visibleRows.add(summary);
                    }
                }
            } else {
                visibleRows.addAll(payrollHistory);
            }

            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0);
            for (EmployeePayrollSummary summary : visibleRows) {
                model.addRow(new Object[]{
                    formatPeriod(summary.getPeriod()),
                    summary.getPeriod().getType() == PayrollPeriodOption.Type.MONTHLY ? "Monthly" : "Semi-Monthly",
                    summary.getAttendanceDays(),
                    MONEY.format(summary.getGrossSalary()),
                    MONEY.format(summary.getNetSalary())
                });
            }

            if (!visibleRows.isEmpty()) {
                historyTable.setRowSelectionInterval(0, 0);
            } else {
                refreshDetail();
            }
        }

        private void refreshDetail() {
            DefaultTableModel detailModel = (DefaultTableModel) detailTable.getModel();
            detailModel.setRowCount(0);
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow < 0 || selectedRow >= visibleRows.size()) {
                detailModel.addRow(new Object[]{"Status", "Select a record to view the payslip breakdown."});
                return;
            }

            EmployeePayrollSummary summary = visibleRows.get(selectedRow);
            detailModel.addRow(new Object[]{"Period", formatPeriod(summary.getPeriod())});
            detailModel.addRow(new Object[]{"Basic Salary", MONEY.format(summary.getBasicSalary())});
            detailModel.addRow(new Object[]{"Rice Subsidy", MONEY.format(summary.getRiceSubsidy())});
            detailModel.addRow(new Object[]{"Phone Allowance", MONEY.format(summary.getPhoneAllowance())});
            detailModel.addRow(new Object[]{"Clothing Allowance", MONEY.format(summary.getClothingAllowance())});
            detailModel.addRow(new Object[]{"Gross Salary", MONEY.format(summary.getGrossSalary())});
            detailModel.addRow(new Object[]{"SSS", MONEY.format(summary.getSss())});
            detailModel.addRow(new Object[]{"PhilHealth", MONEY.format(summary.getPhilhealth())});
            detailModel.addRow(new Object[]{"Pag-IBIG", MONEY.format(summary.getPagibig())});
            detailModel.addRow(new Object[]{"Withholding Tax", MONEY.format(summary.getWithholdingTax())});
            detailModel.addRow(new Object[]{"Total Deductions", MONEY.format(summary.getTotalDeductions())});
            detailModel.addRow(new Object[]{"Net Pay", MONEY.format(summary.getNetSalary())});
        }
    }

    private static class NotificationBadgeLabel extends JLabel {

        NotificationBadgeLabel() {
            super("", SwingConstants.CENTER);
            setForeground(BrandTheme.NAVY);
            setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 11f));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BrandTheme.GOLD);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(BrandTheme.NAVY);
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private void styleDashboardTable(JTable table) {
        BrandTheme.styleTable(table);
        table.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
        table.getTableHeader().setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
    }
}

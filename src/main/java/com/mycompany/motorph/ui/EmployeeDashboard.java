package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AppClock;
import com.mycompany.motorph.service.AuthService;
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
import javax.swing.JPasswordField;
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
        BrandTheme.installGlobalTheme();
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
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BrandTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
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
        subtitleLabel.setForeground(BrandTheme.MUTED_INVERSE);

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
                BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));
        navigation.setPreferredSize(new Dimension(198, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("Employee Workspace");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        menuLabel.setForeground(BrandTheme.MUTED_INVERSE);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnHome = createMenuButton("Dashboard", "home");
        btnProfile = createMenuButton("My Profile", "profile");
        btnPayrollSummary = createMenuButton("Payslips", "payslips");
        btnLeaveRequests = createMenuButton("Leave Requests", "leave");

        menuButtons.add(menuLabel);
        menuButtons.add(Box.createVerticalStrut(12));
        menuButtons.add(btnHome);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnProfile);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnPayrollSummary);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnLeaveRequests);
        menuButtons.add(Box.createVerticalGlue());

        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new DashboardHomePanel(), "home");
        contentPanel.add(new MyProfilePanel(), "profile");
        contentPanel.add(new LeaveRequestPanel(), "leave");
        contentPanel.add(new PayslipsWorkspacePanel(), "payslips");

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
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.setMinimumSize(new Dimension(168, 36));
        button.setPreferredSize(new Dimension(168, 36));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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
        label.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 15f));
        label.setForeground(BrandTheme.TEXT_DARK);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        label.setForeground(BrandTheme.TEXT_DARK);
        return label;
    }

    private void setControlWidth(JTextField field, int width) {
        Dimension size = new Dimension(width, 32);
        field.setPreferredSize(size);
        field.setMinimumSize(size);
        field.setMaximumSize(size);
    }

    private void setControlWidth(JComboBox<?> comboBox, int width) {
        Dimension size = new Dimension(width, 32);
        comboBox.setPreferredSize(size);
        comboBox.setMinimumSize(size);
        comboBox.setMaximumSize(size);
    }

    private void setControlWidth(DatePickerField datePickerField, int width) {
        Dimension size = new Dimension(width, 32);
        datePickerField.setPreferredSize(size);
        datePickerField.setMinimumSize(size);
        datePickerField.setMaximumSize(size);
    }

    private void setButtonSize(JButton button, int width) {
        Dimension size = new Dimension(width, 36);
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

    private String[][] buildProfileIdentityRows() {
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
            {"Address", employee.getAddress()}
        };
    }

    private String[][] buildCompensationRows() {
        if (employee == null) {
            return new String[][]{{"Status", "Employee record not found"}};
        }

        return new String[][]{
            {"Basic Salary", MONEY.format(employee.getBasicSalary())},
            {"Rice Subsidy", MONEY.format(employee.getRiceSubsidy())},
            {"Phone Allowance", MONEY.format(employee.getPhoneAllowance())},
            {"Clothing Allowance", MONEY.format(employee.getClothingAllowance())}
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

    private JPanel buildStaticInfoCard(String titleText, String[][] rows) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        BrandTheme.styleCardSurface(card);

        JLabel title = new JLabel(titleText);
        title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        title.setForeground(BrandTheme.PRIMARY_BLUE);

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);
        for (String[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            grid.add(buildStaticValueBlock(row[0], row[1]));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStaticValueBlock(String labelText, String valueText) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        label.setForeground(BrandTheme.MUTED);

        JLabel value = createValueLabel();
        value.setText(valueText == null || valueText.isBlank() ? "Not Available" : valueText);

        block.add(label);
        block.add(Box.createVerticalStrut(4));
        block.add(value);
        return block;
    }

    private JPanel buildDynamicValueBlock(String labelText, JLabel valueLabel) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        label.setForeground(BrandTheme.MUTED);

        block.add(label);
        block.add(Box.createVerticalStrut(4));
        block.add(valueLabel);
        return block;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("Not Available");
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        label.setForeground(BrandTheme.TEXT);
        return label;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel(MONEY.format(0));
        label.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 20f));
        label.setForeground(BrandTheme.PRIMARY_BLUE);
        return label;
    }

    private static class SurfacePanel extends JPanel {

        SurfacePanel() {
            super(new BorderLayout(0, 16));
            BrandTheme.styleSurface(this);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }
    }

    private class DashboardHomePanel extends SurfacePanel {

        DashboardHomePanel() {
            JPanel top = new JPanel(new GridLayout(1, 2, 16, 16));
            top.setOpaque(false);

            top.add(buildWelcomeCard());
            top.add(buildAttendanceCard());

            JPanel body = new JPanel(new GridLayout(1, 2, 12, 12));
            body.setOpaque(false);
            body.add(new AttendancePanel("Attendance Overview"));
            body.add(new PayrollSummaryPanel(true));

            add(top, BorderLayout.NORTH);
            add(body, BorderLayout.CENTER);
        }

        private JPanel buildWelcomeCard() {
            JPanel panel = new JPanel();
            BrandTheme.styleCardSurface(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel title = createSectionTitle("Welcome");

            JLabel body = new JLabel(employee == null
                    ? "Employee record unavailable."
                    : "<html>" + employee.getEmployeeName() + "<br/>" + resolveProfilePosition() + "<br/>" + employee.getStatus() + "</html>");
            body.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
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

            JLabel title = createSectionTitle("Latest Attendance");

            JLabel body = new JLabel(latest == null
                    ? "No attendance-backed payroll periods available yet."
                    : "<html>Period: " + formatPeriod(latest.getPeriod())
                    + "<br/>Days Worked: " + latest.getAttendanceDays()
                    + "<br/>Hours Worked: " + String.format("%.2f", latest.getAttendanceHours()) + "</html>");
            body.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
            body.setForeground(BrandTheme.TEXT);

            panel.add(title);
            panel.add(Box.createVerticalStrut(12));
            panel.add(body);
            return panel;
        }
    }

    private class MyProfilePanel extends SurfacePanel {

        MyProfilePanel() {
            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 16));
            topRow.setOpaque(false);
            topRow.add(buildStaticInfoCard("Employee Information", buildProfileIdentityRows()));
            topRow.add(buildStaticInfoCard("Compensation Details", buildCompensationRows()));

            JPanel bottomRow = new JPanel(new GridLayout(1, 2, 12, 12));
            bottomRow.setOpaque(false);
            bottomRow.add(buildStaticInfoCard("Government IDs", buildGovernmentRows()));
            bottomRow.add(new PasswordManagementCard());

            content.add(topRow);
            content.add(Box.createVerticalStrut(12));
            content.add(bottomRow);

            add(buildSectionHeader("My Profile", null), BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
        }
    }

    private class PasswordManagementCard extends JPanel {

        private final AuthService authService = new AuthService();
        private final JPasswordField newPasswordField = new JPasswordField();
        private final JPasswordField confirmPasswordField = new JPasswordField();

        PasswordManagementCard() {
            setLayout(new BorderLayout(0, 16));
            BrandTheme.styleCardSurface(this);

            JLabel title = new JLabel("Password");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JLabel subtitle = new JLabel("<html>Update your current account password without leaving the employee workspace.</html>");
            subtitle.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            subtitle.setForeground(BrandTheme.MUTED);

            JPanel form = new JPanel();
            form.setOpaque(false);
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

            BrandTheme.styleInputField(newPasswordField);
            BrandTheme.styleInputField(confirmPasswordField);
            newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            newPasswordField.setPreferredSize(new Dimension(320, 38));
            confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            confirmPasswordField.setPreferredSize(new Dimension(320, 38));

            JButton updateButton = new JButton("Update Password");
            BrandTheme.stylePrimaryButton(updateButton);
            updateButton.setAlignmentX(LEFT_ALIGNMENT);
            updateButton.addActionListener(evt -> handlePasswordUpdate());

            form.add(subtitle);
            form.add(Box.createVerticalStrut(16));
            form.add(buildPasswordField("New Password", newPasswordField));
            form.add(Box.createVerticalStrut(12));
            form.add(buildPasswordField("Confirm Password", confirmPasswordField));
            form.add(Box.createVerticalStrut(18));
            form.add(updateButton);

            add(title, BorderLayout.NORTH);
            add(form, BorderLayout.CENTER);
        }

        private JPanel buildPasswordField(String labelText, JPasswordField field) {
            JPanel block = new JPanel();
            block.setOpaque(false);
            block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

            JLabel label = new JLabel(labelText);
            label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 12f));
            label.setForeground(BrandTheme.MUTED);

            block.add(label);
            block.add(Box.createVerticalStrut(6));
            block.add(field);
            return block;
        }

        private void handlePasswordUpdate() {
            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Employee account details are unavailable.",
                        "Password Update",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

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

            boolean updated = authService.changePassword(user, user.getPassword(), newPassword, users);
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

            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private class PayslipsWorkspacePanel extends SurfacePanel {

        private final JComboBox<PayrollPeriodOption> periodSelector = new JComboBox<>();
        private final JTable historyTable = new JTable();
        private final JLabel historyStatusLabel = new JLabel(" ");

        private final JLabel periodValueLabel = createValueLabel();
        private final JLabel typeValueLabel = createValueLabel();
        private final JLabel daysWorkedValueLabel = createValueLabel();
        private final JLabel grossValueLabel = createMetricValueLabel();
        private final JLabel netPayValueLabel = createMetricValueLabel();
        private final JLabel attendanceHoursValueLabel = createValueLabel();

        private final JLabel basicSalaryValueLabel = createValueLabel();
        private final JLabel riceSubsidyValueLabel = createValueLabel();
        private final JLabel phoneAllowanceValueLabel = createValueLabel();
        private final JLabel clothingAllowanceValueLabel = createValueLabel();
        private final JLabel grossComponentsValueLabel = createValueLabel();

        private final JLabel sssValueLabel = createValueLabel();
        private final JLabel philhealthValueLabel = createValueLabel();
        private final JLabel pagibigValueLabel = createValueLabel();
        private final JLabel withholdingTaxValueLabel = createValueLabel();

        private final JLabel grossSummaryValueLabel = createValueLabel();
        private final JLabel totalDeductionsValueLabel = createValueLabel();
        private final JLabel netSummaryValueLabel = createMetricValueLabel();

        private List<EmployeePayrollSummary> visibleRows = new ArrayList<>();

        PayslipsWorkspacePanel() {
            JPanel filterPanel = new JPanel();
            filterPanel.setOpaque(false);
            filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));

            JLabel filterLabel = createFieldLabel("Pay Period");
            filterPanel.add(filterLabel);
            filterPanel.add(Box.createHorizontalStrut(10));
            BrandTheme.styleComboBox(periodSelector);
            periodSelector.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
            periodSelector.setRenderer(new javax.swing.DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                                      Object value,
                                                                      int index,
                                                                      boolean isSelected,
                                                                      boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof PayrollPeriodOption option) {
                        String type = option.getType() == PayrollPeriodOption.Type.MONTHLY
                                ? "Monthly"
                                : "Semi-Monthly";
                        setText(formatPeriod(option) + " (" + type + ")");
                    } else {
                        setText("Select period");
                    }
                    return this;
                }
            });
            setControlWidth(periodSelector, 280);
            filterPanel.add(periodSelector);

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            content.add(buildSummaryCard());
            content.add(Box.createVerticalStrut(16));

            JPanel detailCards = new JPanel(new GridLayout(1, 3, 16, 16));
            detailCards.setOpaque(false);
            detailCards.add(buildSalaryComponentsCard());
            detailCards.add(buildGovernmentDeductionsCard());
            detailCards.add(buildNetPaySummaryCard());
            content.add(detailCards);
            content.add(Box.createVerticalStrut(16));
            content.add(buildHistoryCard());

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
            historyTable.setAutoCreateRowSorter(true);

            historyStatusLabel.setFont(BrandTheme.BODY_FONT.deriveFont(14f));
            historyStatusLabel.setForeground(BrandTheme.MUTED);

            for (PayrollPeriodOption period : payrollPeriods) {
                periodSelector.addItem(period);
            }
            if (periodSelector.getItemCount() > 0) {
                periodSelector.setSelectedIndex(0);
            }
            periodSelector.setEnabled(periodSelector.getItemCount() > 0);
            periodSelector.addActionListener(evt -> refreshHistory());
            historyTable.getSelectionModel().addListSelectionListener(evt -> {
                if (!evt.getValueIsAdjusting()) {
                    updateSelectedSummary();
                }
            });

            add(buildSectionHeader("Payslips", filterPanel), BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);

            refreshHistory();
        }

        private JPanel buildSummaryCard() {
            JPanel card = new JPanel(new BorderLayout(0, 16));
            BrandTheme.styleCardSurface(card);

            JLabel title = new JLabel("Payslip Summary");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JPanel grid = new JPanel(new GridLayout(2, 3, 16, 14));
            grid.setOpaque(false);
            grid.add(buildDynamicValueBlock("Period", periodValueLabel));
            grid.add(buildDynamicValueBlock("Type", typeValueLabel));
            grid.add(buildDynamicValueBlock("Days Worked", daysWorkedValueLabel));
            grid.add(buildDynamicValueBlock("Gross Salary", grossValueLabel));
            grid.add(buildDynamicValueBlock("Net Pay", netPayValueLabel));
            grid.add(buildDynamicValueBlock("Attendance Hours", attendanceHoursValueLabel));

            card.add(title, BorderLayout.NORTH);
            card.add(grid, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildSalaryComponentsCard() {
            JPanel card = new JPanel(new BorderLayout(0, 16));
            BrandTheme.styleCardSurface(card);

            JLabel title = new JLabel("Salary Components");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
            grid.setOpaque(false);
            grid.add(buildDynamicValueBlock("Basic Salary", basicSalaryValueLabel));
            grid.add(buildDynamicValueBlock("Rice Subsidy", riceSubsidyValueLabel));
            grid.add(buildDynamicValueBlock("Phone Allowance", phoneAllowanceValueLabel));
            grid.add(buildDynamicValueBlock("Clothing Allowance", clothingAllowanceValueLabel));
            grid.add(buildDynamicValueBlock("Gross Salary", grossComponentsValueLabel));

            card.add(title, BorderLayout.NORTH);
            card.add(grid, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildGovernmentDeductionsCard() {
            JPanel card = new JPanel(new BorderLayout(0, 16));
            BrandTheme.styleCardSurface(card);

            JLabel title = new JLabel("Government Deductions");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
            grid.setOpaque(false);
            grid.add(buildDynamicValueBlock("SSS", sssValueLabel));
            grid.add(buildDynamicValueBlock("PhilHealth", philhealthValueLabel));
            grid.add(buildDynamicValueBlock("Pag-IBIG", pagibigValueLabel));
            grid.add(buildDynamicValueBlock("Withholding Tax", withholdingTaxValueLabel));

            card.add(title, BorderLayout.NORTH);
            card.add(grid, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildNetPaySummaryCard() {
            JPanel card = new JPanel(new BorderLayout(0, 16));
            BrandTheme.styleCardSurface(card);

            JLabel title = new JLabel("Net Pay Summary");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
            grid.setOpaque(false);
            grid.add(buildDynamicValueBlock("Gross Salary", grossSummaryValueLabel));
            grid.add(buildDynamicValueBlock("Total Deductions", totalDeductionsValueLabel));
            grid.add(buildDynamicValueBlock("Net Pay", netSummaryValueLabel));

            card.add(title, BorderLayout.NORTH);
            card.add(grid, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildHistoryCard() {
            JPanel card = new JPanel(new BorderLayout(0, 14));
            BrandTheme.styleCardSurface(card);

            JLabel title = new JLabel("Payslip History");
            title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.PRIMARY_BLUE);

            JScrollPane scrollPane = new JScrollPane(historyTable);
            BrandTheme.styleScrollPane(scrollPane);

            card.add(title, BorderLayout.NORTH);
            card.add(scrollPane, BorderLayout.CENTER);
            card.add(historyStatusLabel, BorderLayout.SOUTH);
            return card;
        }

        private void refreshHistory() {
            PayrollPeriodOption selectedPeriod = (PayrollPeriodOption) periodSelector.getSelectedItem();
            visibleRows = new ArrayList<>();

            if (selectedPeriod != null) {
                for (EmployeePayrollSummary summary : payrollHistory) {
                    if (summary.getPeriod().getKey().equals(selectedPeriod.getKey())) {
                        visibleRows.add(summary);
                    }
                }
                if (visibleRows.isEmpty()) {
                    EmployeePayrollSummary fallbackSummary = resolveFallbackSummary(selectedPeriod);
                    if (fallbackSummary != null) {
                        visibleRows.add(fallbackSummary);
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

            String periodLabel = selectedPeriod == null ? "all available periods" : formatPeriod(selectedPeriod);
            historyStatusLabel.setText("Showing " + visibleRows.size() + " payroll records for " + periodLabel + ".");

            if (!visibleRows.isEmpty()) {
                historyTable.setRowSelectionInterval(0, 0);
                updateSelectedSummary();
            } else {
                historyTable.clearSelection();
                updateSummary(resolveFallbackSummary(selectedPeriod));
            }
        }

        private EmployeePayrollSummary resolveFallbackSummary(PayrollPeriodOption selectedPeriod) {
            if (selectedPeriod == null || user == null) {
                return null;
            }
            return employeePortalService.getPayrollSummary(user.getEmployeeNumber(), selectedPeriod.getKey());
        }

        private void updateSelectedSummary() {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow < 0) {
                updateSummary(visibleRows.isEmpty()
                        ? resolveFallbackSummary((PayrollPeriodOption) periodSelector.getSelectedItem())
                        : visibleRows.get(0));
                return;
            }

            int modelRow = historyTable.convertRowIndexToModel(selectedRow);
            if (modelRow < 0 || modelRow >= visibleRows.size()) {
                updateSummary(resolveFallbackSummary((PayrollPeriodOption) periodSelector.getSelectedItem()));
                return;
            }

            updateSummary(visibleRows.get(modelRow));
        }

        private void updateSummary(EmployeePayrollSummary summary) {
            if (summary == null) {
                periodValueLabel.setText("Not Available");
                typeValueLabel.setText("Not Available");
                daysWorkedValueLabel.setText("0");
                grossValueLabel.setText(MONEY.format(0));
                netPayValueLabel.setText(MONEY.format(0));
                attendanceHoursValueLabel.setText("0.00");
                basicSalaryValueLabel.setText(MONEY.format(0));
                riceSubsidyValueLabel.setText(MONEY.format(0));
                phoneAllowanceValueLabel.setText(MONEY.format(0));
                clothingAllowanceValueLabel.setText(MONEY.format(0));
                grossComponentsValueLabel.setText(MONEY.format(0));
                sssValueLabel.setText(MONEY.format(0));
                philhealthValueLabel.setText(MONEY.format(0));
                pagibigValueLabel.setText(MONEY.format(0));
                withholdingTaxValueLabel.setText(MONEY.format(0));
                grossSummaryValueLabel.setText(MONEY.format(0));
                totalDeductionsValueLabel.setText(MONEY.format(0));
                netSummaryValueLabel.setText(MONEY.format(0));
                return;
            }

            periodValueLabel.setText(formatPeriod(summary.getPeriod()));
            typeValueLabel.setText(summary.getPeriod().getType() == PayrollPeriodOption.Type.MONTHLY ? "Monthly" : "Semi-Monthly");
            daysWorkedValueLabel.setText(String.valueOf(summary.getAttendanceDays()));
            grossValueLabel.setText(MONEY.format(summary.getGrossSalary()));
            netPayValueLabel.setText(MONEY.format(summary.getNetSalary()));
            attendanceHoursValueLabel.setText(String.format("%.2f", summary.getAttendanceHours()));

            basicSalaryValueLabel.setText(MONEY.format(summary.getBasicSalary()));
            riceSubsidyValueLabel.setText(MONEY.format(summary.getRiceSubsidy()));
            phoneAllowanceValueLabel.setText(MONEY.format(summary.getPhoneAllowance()));
            clothingAllowanceValueLabel.setText(MONEY.format(summary.getClothingAllowance()));
            grossComponentsValueLabel.setText(MONEY.format(summary.getGrossSalary()));

            sssValueLabel.setText(MONEY.format(summary.getSss()));
            philhealthValueLabel.setText(MONEY.format(summary.getPhilhealth()));
            pagibigValueLabel.setText(MONEY.format(summary.getPagibig()));
            withholdingTaxValueLabel.setText(MONEY.format(summary.getWithholdingTax()));

            grossSummaryValueLabel.setText(MONEY.format(summary.getGrossSalary()));
            totalDeductionsValueLabel.setText(MONEY.format(summary.getTotalDeductions()));
            netSummaryValueLabel.setText(MONEY.format(summary.getNetSalary()));
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
            this("Attendance Overview");
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

        private final JComboBox<String> leaveTypeSelector = new JComboBox<>(LeaveTypeCatalog.masterLeaveTypes());
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
            north.add(buildSectionHeader("Leave Requests", null), BorderLayout.NORTH);
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

            add(buildSectionHeader("View Payslip", actionsPanel), BorderLayout.NORTH);
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

            add(buildSectionHeader("Payslip History", filterPanel), BorderLayout.NORTH);
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
            setForeground(BrandTheme.TEXT);
            setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 11f));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BrandTheme.GOLD);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(BrandTheme.TEXT);
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

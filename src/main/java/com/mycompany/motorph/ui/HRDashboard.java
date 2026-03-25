package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.LeaveService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HRDashboard extends JFrame {

    private static final Set<String> HR_NOTIFICATION_ACTIONS = Set.of(
            "LEAVE_SUBMITTED",
            "LEAVE_REQUEST"
    );
    private static final Pattern LEAVE_NOTIFICATION_PATTERN = Pattern.compile(
            "employee\\s+(\\d+)\\s+from\\s+(\\d{4}-\\d{2}-\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2})",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern LEAVE_DATE_RANGE_PATTERN = Pattern.compile(
            "from\\s+(\\d{4}-\\d{2}-\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2})",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern EMPLOYEE_NUMBER_PATTERN = Pattern.compile(
            "employee\\s+(\\d+)",
            Pattern.CASE_INSENSITIVE
    );

    private final User user;
    private final NotificationService notificationService = new NotificationService();
    private final LeaveService leaveService = new LeaveService();
    private final pnlEmployees employeesPanel;
    private final pnlAttendance attendancePanel = new pnlAttendance(true);
    private final pnlLeaveRequest leaveRequestPanel;

    private JPanel contentPanel;
    private JButton btnManageEmployees;
    private JButton btnAttendance;
    private JButton btnLeaveRequest;
    private JButton btnLogout;
    private JButton headerNotificationButton;
    private NotificationBadgeLabel notificationBadgeLabel;

    public HRDashboard(User user) {
        BrandTheme.installGlobalTheme();
        this.user = user;
        this.employeesPanel = new pnlEmployees(user, true);
        this.leaveRequestPanel = new pnlLeaveRequest(user, true, null, this::refreshNotificationBell);
        initComponents();
    }

    public HRDashboard() {
        this(null);
    }

    private void initComponents() {
        setTitle("HR Dashboard");
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

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(BrandTheme.GRAPHITE);
        navigation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BrandTheme.LAVENDER),
                BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));
        navigation.setPreferredSize(new Dimension(202, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("HR Operations");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        menuLabel.setForeground(BrandTheme.MUTED_INVERSE);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnManageEmployees = createNavigationButton("Manage Employees");
        btnAttendance = createNavigationButton("Attendance");
        btnLeaveRequest = createNavigationButton("Leave Request");
        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

        btnManageEmployees.addActionListener(evt -> {
            employeesPanel.reloadEmployees();
            showPanel(employeesPanel);
        });
        btnAttendance.addActionListener(evt -> {
            attendancePanel.reloadAttendance();
            showPanel(attendancePanel);
        });
        btnLeaveRequest.addActionListener(evt -> {
            leaveRequestPanel.reloadRequests();
            showPanel(leaveRequestPanel);
        });

        menuButtons.add(menuLabel);
        menuButtons.add(Box.createVerticalStrut(12));
        menuButtons.add(btnManageEmployees);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnAttendance);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnLeaveRequest);
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

    private void configureNavigationButton(JButton button) {
        BrandTheme.styleNavigationButton(button);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.setMinimumSize(new Dimension(170, 36));
        button.setPreferredSize(new Dimension(170, 36));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JPanel buildContentArea() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BrandTheme.NAVY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        showPanel(employeesPanel);
        return contentPanel;
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

        JLabel titleLabel = new JLabel(getHrWelcomeText());
        BrandTheme.setWelcomeText(titleLabel, getHrWelcomeText(), true);
        titleLabel.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 18f));

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

    private JLayeredPane buildHeaderNotificationControl() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setPreferredSize(new Dimension(172, 44));
        layeredPane.setMinimumSize(new Dimension(172, 44));
        layeredPane.setMaximumSize(new Dimension(172, 44));

        headerNotificationButton = new JButton("Notifications 🔔");
        configureHeaderNotificationButton(headerNotificationButton);
        headerNotificationButton.addActionListener(evt -> openHrNotifications());
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
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setToolTipText("Open system notifications");
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshNotificationBell() {
        if (headerNotificationButton == null || notificationBadgeLabel == null) {
            return;
        }

        List<NotificationEntry> notifications = getHrNotifications();
        int unreadCount = 0;
        for (NotificationEntry notification : notifications) {
            if (!notification.isRead()) {
                unreadCount++;
            }
        }

        headerNotificationButton.setToolTipText(unreadCount > 0
                ? unreadCount + " unread HR notifications"
                : "Open HR notifications");
        if (unreadCount > 0) {
            notificationBadgeLabel.setText(unreadCount > 99 ? "99" : String.valueOf(unreadCount));
            notificationBadgeLabel.setVisible(true);
        } else {
            notificationBadgeLabel.setText("");
            notificationBadgeLabel.setVisible(false);
        }
    }

    private void openHrNotifications() {
        NotificationCenterDialog.showDialog(
                this,
                "System Notifications",
                "Unread leave requests remain highlighted until they are marked as read or processed.",
                this::getHrNotifications,
                new NotificationCenterDialog.NotificationActionHandler() {
                    @Override
                    public String getLabel() {
                        return "Respond to Selected";
                    }

                    @Override
                    public boolean isSupported(NotificationEntry notification) {
                        return extractLeaveNotificationTarget(notification) != null;
                    }

                    @Override
                    public boolean handle(Component owner, NotificationEntry notification) {
                        return respondToNotification(owner, notification);
                    }
                }
        );
        refreshNotificationBell();
    }

    private List<NotificationEntry> getHrNotifications() {
        List<NotificationEntry> relevant = new ArrayList<>();
        for (NotificationEntry notification : notificationService.getRecentNotifications(300)) {
            if (isHrRelevant(notification)) {
                relevant.add(notification);
            }
        }
        return relevant;
    }

    private String getHrWelcomeText() {
        return user == null
                ? "Welcome, hr | HR Workspace"
                : "Welcome, " + user.getUsername() + " | HR Workspace";
    }

    private boolean isHrRelevant(NotificationEntry notification) {
        if (notification == null) {
            return false;
        }
        String action = notification.getAction() == null ? "" : notification.getAction().trim().toUpperCase();
        return HR_NOTIFICATION_ACTIONS.contains(action);
    }

    private boolean respondToNotification(Component owner, NotificationEntry notification) {
        LeaveNotificationTarget target = extractLeaveNotificationTarget(notification);
        if (target == null) {
            JOptionPane.showMessageDialog(
                    owner,
                    "This notification does not include enough leave-request details for direct processing. Open Leave Requests to review it.",
                    "Leave Request",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return false;
        }

        LeaveRequest request = findPendingLeaveRequest(target.employeeNumber(), target.startDate(), target.endDate());
        if (request == null) {
            JOptionPane.showMessageDialog(
                    owner,
                    "No pending leave request matched this notification. Refresh the leave queue and try again.",
                    "Leave Request",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return false;
        }

        String choice = DialogHelper.showActionChoice(
                owner,
                "Leave Response",
                "Respond to the leave request for " + request.getEmployeeName() + ".",
                "Approve",
                "Reject",
                "Cancel"
        );

        if ("Approve".equals(choice)) {
            String statusMessage = promptStatusMessage(
                    owner,
                    "Approval Message",
                    "Enter an approval message:",
                    request.getStatusMessage().startsWith("Approved") ? request.getStatusMessage() : "Approved by HR.",
                    false
            );
            if (statusMessage == null) {
                return false;
            }
            boolean updated = leaveService.respondToLeave(
                    request.getEmployeeNumber(),
                    request.getStartDate(),
                    request.getEndDate(),
                    true,
                    statusMessage
            );
            if (!updated) {
                JOptionPane.showMessageDialog(
                        owner,
                        "The selected leave request is no longer pending. Refresh the queue and try again.",
                        "Leave Request",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return false;
            }
            notificationService.record(
                    user,
                    "LEAVE_APPROVED",
                    "Approved leave for employee " + request.getEmployeeNumber()
                            + " from " + request.getStartDate()
                            + " to " + request.getEndDate() + "."
            );
        } else if ("Reject".equals(choice)) {
            String statusMessage = promptStatusMessage(
                    owner,
                    "Rejection Message",
                    "Enter the reason for rejection:",
                    request.getStatusMessage().startsWith("Rejected") ? request.getStatusMessage() : "",
                    true
            );
            if (statusMessage == null) {
                return false;
            }
            boolean updated = leaveService.respondToLeave(
                    request.getEmployeeNumber(),
                    request.getStartDate(),
                    request.getEndDate(),
                    false,
                    statusMessage
            );
            if (!updated) {
                JOptionPane.showMessageDialog(
                        owner,
                        "The selected leave request is no longer pending. Refresh the queue and try again.",
                        "Leave Request",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return false;
            }
            notificationService.record(
                    user,
                    "LEAVE_REJECTED",
                    "Rejected leave for employee " + request.getEmployeeNumber()
                            + " from " + request.getStartDate()
                            + " to " + request.getEndDate() + "."
            );
        } else {
            return false;
        }

        notificationService.markAsRead(List.of(notification));
        leaveRequestPanel.reloadRequests();
        refreshNotificationBell();
        return true;
    }

    private String promptStatusMessage(Component owner,
                                       String title,
                                       String prompt,
                                       String initialValue,
                                       boolean required) {
        String message = DialogHelper.promptText(owner, title, prompt, initialValue, "Save Message");
        if (message == null) {
            return null;
        }

        String trimmed = message.trim();
        if (required && trimmed.isEmpty()) {
            JOptionPane.showMessageDialog(
                    owner,
                    "A reason is required when rejecting a leave request.",
                    title,
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        return trimmed.isEmpty() ? initialValue.trim() : trimmed;
    }

    private LeaveRequest findPendingLeaveRequest(int employeeNumber, String startDate, String endDate) {
        List<LeaveRequest> requests = leaveService.getRequests();
        for (int index = requests.size() - 1; index >= 0; index--) {
            LeaveRequest request = requests.get(index);
            if (request.getEmployeeNumber() == employeeNumber
                    && request.getStartDate().equals(startDate)
                    && request.getEndDate().equals(endDate)
                    && "PENDING".equalsIgnoreCase(request.getStatus())) {
                return request;
            }
        }
        return null;
    }

    private LeaveNotificationTarget extractLeaveNotificationTarget(NotificationEntry notification) {
        if (notification == null) {
            return null;
        }

        String action = notification.getAction() == null ? "" : notification.getAction().trim().toUpperCase();
        if (!HR_NOTIFICATION_ACTIONS.contains(action)) {
            return null;
        }

        String details = notification.getDetails() == null ? "" : notification.getDetails();
        Matcher matcher = LEAVE_NOTIFICATION_PATTERN.matcher(details);
        if (matcher.find()) {
            try {
                return new LeaveNotificationTarget(
                        Integer.parseInt(matcher.group(1)),
                        matcher.group(2),
                        matcher.group(3)
                );
            } catch (Exception e) {
                return null;
            }
        }

        Matcher dateRangeMatcher = LEAVE_DATE_RANGE_PATTERN.matcher(details);
        if (!dateRangeMatcher.find()) {
            return null;
        }

        Integer employeeNumber = extractEmployeeNumber(details, notification.getActor());
        if (employeeNumber == null) {
            return null;
        }

        return new LeaveNotificationTarget(
                employeeNumber,
                dateRangeMatcher.group(1),
                dateRangeMatcher.group(2)
        );
    }

    private Integer extractEmployeeNumber(String details, String actor) {
        Matcher employeeMatcher = EMPLOYEE_NUMBER_PATTERN.matcher(details);
        if (employeeMatcher.find()) {
            try {
                return Integer.parseInt(employeeMatcher.group(1));
            } catch (Exception e) {
                return null;
            }
        }

        try {
            return Integer.parseInt(actor == null ? "" : actor.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static class NotificationBadgeLabel extends JLabel {

        NotificationBadgeLabel() {
            super("", SwingConstants.CENTER);
            setForeground(BrandTheme.TEXT_INVERSE);
            setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 11f));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BrandTheme.MOTORPH_RED);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(new Color(0xB2, 0x1A, 0x2D));
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private record LeaveNotificationTarget(int employeeNumber, String startDate, String endDate) {
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new HRDashboard().setVisible(true));
    }
}

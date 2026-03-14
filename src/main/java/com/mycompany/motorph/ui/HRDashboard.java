package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HRDashboard extends JFrame {

    private static final Set<String> HR_NOTIFICATION_ACTIONS = Set.of(
            "LEAVE_SUBMITTED",
            "LEAVE_REQUEST"
    );

    private final User user;
    private final NotificationService notificationService = new NotificationService();
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
        this.user = user;
        this.employeesPanel = new pnlEmployees(user, true);
        this.leaveRequestPanel = new pnlLeaveRequest(user, true);
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
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));
        navigation.setPreferredSize(new Dimension(228, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("HR Menu");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 12f));
        menuLabel.setForeground(BrandTheme.MUTED);
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
        button.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        button.setMinimumSize(new Dimension(188, 40));
        button.setPreferredSize(new Dimension(188, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JPanel buildContentArea() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BrandTheme.NAVY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        showPanel(employeesPanel);
        return contentPanel;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        BrandTheme.styleDarkSurface(header);

        JLabel logoLabel = new JLabel(BrandTheme.loadHeaderLogoIcon());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        BrandTheme.styleDarkSurface(titlePanel);

        JLabel titleLabel = new JLabel(getHrWelcomeText());
        BrandTheme.setWelcomeText(titleLabel, getHrWelcomeText(), true);
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
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
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
        List<NotificationEntry> notifications = getHrNotifications();
        notificationService.markAsRead(notifications);
        NotificationCenterDialog.showDialog(
                this,
                "System Notifications",
                "",
                this::getNormalizedHrNotifications
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

    private List<NotificationEntry> getNormalizedHrNotifications() {
        List<NotificationEntry> notifications = new ArrayList<>();
        for (NotificationEntry notification : getHrNotifications()) {
            notifications.add(new NotificationEntry(
                    notification.getTimestamp(),
                    notification.getActor(),
                    notification.getRole(),
                    normalizeHrAction(notification.getAction()),
                    notification.getDetails(),
                    notification.isRead()
            ));
        }
        return notifications;
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

    private String normalizeHrAction(String action) {
        String normalized = action == null ? "" : action.trim().toUpperCase();
        return switch (normalized) {
            case "LEAVE_SUBMITTED", "LEAVE_REQUEST" -> "LEAVE_REQUEST";
            default -> normalized;
        };
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

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new HRDashboard().setVisible(true));
    }
}

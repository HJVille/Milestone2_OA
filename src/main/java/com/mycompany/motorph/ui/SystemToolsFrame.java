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

public class SystemToolsFrame extends JFrame {

    private final User user;
    private final CardLayout contentCards = new CardLayout();
    private final UserAccountsPanel userAccountsPanel;
    private final CsvStatusPanel csvStatusPanel = new CsvStatusPanel();
    private final AuditLogPanel auditLogPanel = new AuditLogPanel();

    private JPanel contentPanel;
    private JButton btnUserAccounts;
    private JButton btnCsvPaths;
    private JButton btnAuditLog;
    private JButton btnLogout;

    public SystemToolsFrame(User user) {
        this.user = user;
        this.userAccountsPanel = new UserAccountsPanel(user);
        setTitle("IT System Tools");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setResizable(true);
        setSize(1180, 780);
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
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));
        navigation.setPreferredSize(new Dimension(228, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("IT Menu");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 12f));
        menuLabel.setForeground(BrandTheme.MUTED);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnUserAccounts = createNavigationButton("User Accounts");
        btnCsvPaths = createNavigationButton("CSV Paths");
        btnAuditLog = createNavigationButton("Audit Log");
        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

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

    private void configureNavigationButton(JButton button) {
        BrandTheme.styleNavigationButton(button);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        button.setMinimumSize(new Dimension(188, 40));
        button.setPreferredSize(new Dimension(188, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JPanel buildHeader(User user) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        BrandTheme.styleDarkSurface(header);
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        header.add(new JLabel(BrandTheme.loadHeaderLogoIcon()), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        BrandTheme.styleDarkSurface(textPanel);

        JLabel title = new JLabel("IT System Tools");
        title.setFont(BrandTheme.TITLE_FONT);
        title.setForeground(BrandTheme.TEXT);

        JLabel subtitle = new JLabel(BrandTheme.TAGLINE);
        subtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.ITALIC));
        subtitle.setForeground(BrandTheme.MUTED);

        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        BrandTheme.styleDarkSurface(actions);

        JButton notificationsButton = DashboardNavigation.createNotificationButton(this);
        notificationsButton.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));

        actions.add(notificationsButton);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContentArea() {
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(BrandTheme.NAVY);
        contentPanel.add(userAccountsPanel, "accounts");
        contentPanel.add(csvStatusPanel, "paths");
        contentPanel.add(auditLogPanel, "audit");
        contentCards.show(contentPanel, "accounts");
        return contentPanel;
    }
}

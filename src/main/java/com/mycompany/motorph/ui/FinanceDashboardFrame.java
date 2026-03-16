package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FinanceDashboardFrame extends JFrame {

    private final User user;
    private final PayrollDashboard payrollDashboard;

    public FinanceDashboardFrame(User user) {
        BrandTheme.installGlobalTheme();
        this.user = user;
        this.payrollDashboard = new PayrollDashboard(user);
        setTitle("Finance Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 780));
        setResizable(true);
        setSize(1320, 860);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(BrandTheme.IVORY);

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(payrollDashboard, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        BrandTheme.styleDarkSurface(header);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BrandTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        header.add(new JLabel(BrandTheme.loadHeaderLogoIcon()), BorderLayout.WEST);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        BrandTheme.styleDarkSurface(titlePanel);

        String welcomeText = "Welcome, " + resolveFinanceName() + " | Finance Workspace";
        JLabel title = new JLabel(welcomeText);
        BrandTheme.setWelcomeText(title, welcomeText, true);
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel(BrandTheme.TAGLINE);
        subtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.ITALIC));
        subtitle.setForeground(BrandTheme.MUTED_INVERSE);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        BrandTheme.styleDarkSurface(actions);

        JButton notificationsButton = DashboardNavigation.createNotificationButton(this);
        notificationsButton.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));

        actions.add(notificationsButton);

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private String resolveFinanceName() {
        return user == null || user.getUsername() == null || user.getUsername().isBlank()
                ? "finance"
                : user.getUsername().trim();
    }
}

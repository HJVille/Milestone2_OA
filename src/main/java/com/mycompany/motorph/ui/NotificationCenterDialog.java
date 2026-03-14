package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class NotificationCenterDialog extends JDialog {

    private static final DateTimeFormatter SOURCE_TS = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_TS = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter SLASH_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b");
    private static final Pattern SLASH_DATE_PATTERN = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b");

    private final JTable table = new JTable();
    private final String headerTitle;
    private final String headerSubtitle;
    private final Supplier<List<NotificationEntry>> notificationsSupplier;

    public static void showDialog(Component owner) {
        showDialog(
                owner,
                "System Notifications",
                "Recent actions across MotorPH payroll, employee, leave, attendance, and account workflows",
                () -> new NotificationService().getRecentNotifications(300)
        );
    }

    public static void showDialog(Component owner,
                                  String title,
                                  String subtitle,
                                  Supplier<List<NotificationEntry>> notificationsSupplier) {
        Window window = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(owner);
        NotificationCenterDialog dialog = new NotificationCenterDialog(window, title, subtitle, notificationsSupplier);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private NotificationCenterDialog(Window owner,
                                     String headerTitle,
                                     String headerSubtitle,
                                     Supplier<List<NotificationEntry>> notificationsSupplier) {
        super(owner, "Notifications", Dialog.ModalityType.APPLICATION_MODAL);
        this.headerTitle = headerTitle;
        this.headerSubtitle = headerSubtitle;
        this.notificationsSupplier = notificationsSupplier;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(920, 540));
        setResizable(true);
        setLayout(new BorderLayout(0, 14));
        getContentPane().setBackground(BrandTheme.IVORY);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);

        reloadNotifications();
        pack();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(BrandTheme.GRAPHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BrandTheme.ROYAL),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(headerTitle);
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(BrandTheme.TEXT);

        textPanel.add(title);
        if (headerSubtitle != null && !headerSubtitle.isBlank()) {
            JLabel subtitle = new JLabel(headerSubtitle);
            subtitle.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 14f));
            subtitle.setForeground(BrandTheme.MUTED);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(subtitle);
        }

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");
        BrandTheme.styleSecondaryButton(refreshButton);
        BrandTheme.stylePrimaryButton(closeButton);
        refreshButton.addActionListener(evt -> reloadNotifications());
        closeButton.addActionListener(evt -> dispose());

        actions.add(refreshButton);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(closeButton);

        header.add(textPanel, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildTableSection() {
        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
            "Timestamp", "Actor", "Role", "Action", "Details"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        BrandTheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);
        table.getColumnModel().getColumn(4).setPreferredWidth(420);

        JScrollPane scrollPane = new JScrollPane(table);
        BrandTheme.styleScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18, 18, 0, 18),
                scrollPane.getBorder()
        ));
        return scrollPane;
    }

    private void reloadNotifications() {
        List<NotificationEntry> notifications = notificationsSupplier.get();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (NotificationEntry notification : notifications) {
            model.addRow(new Object[]{
                formatTimestamp(notification.getTimestamp()),
                notification.getActor(),
                notification.getRole(),
                notification.getAction(),
                formatNotificationDetails(notification.getDetails())
            });
        }
    }

    private String formatTimestamp(String timestamp) {
        try {
            return DISPLAY_TS.format(LocalDateTime.parse(timestamp, SOURCE_TS));
        } catch (Exception e) {
            return timestamp;
        }
    }

    private String formatNotificationDetails(String text) {
        return replaceDates(replaceDates(text, ISO_DATE_PATTERN, ISO_DATE), SLASH_DATE_PATTERN, SLASH_DATE);
    }

    private String replaceDates(String text, Pattern pattern, DateTimeFormatter parser) {
        if (text == null || text.isBlank()) {
            return "";
        }

        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String rawValue = matcher.group();
            String replacement = rawValue;
            try {
                replacement = DISPLAY_DATE.format(LocalDate.parse(rawValue, parser));
            } catch (Exception e) {
                replacement = rawValue;
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}

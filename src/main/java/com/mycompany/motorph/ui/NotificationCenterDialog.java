package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
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
    private final JTextArea previewTextArea = new JTextArea();
    private final JLabel previewTimestampValue = createPreviewValueLabel();
    private final JLabel previewActorValue = createPreviewValueLabel();
    private final JLabel previewRoleValue = createPreviewValueLabel();
    private final JLabel previewActionValue = createPreviewValueLabel();
    private final JLabel previewStateValue = createPreviewValueLabel();
    private final String headerTitle;
    private final String headerSubtitle;
    private final Supplier<List<NotificationEntry>> notificationsSupplier;
    private final NotificationActionHandler actionHandler;
    private final JButton actionButton = new JButton();
    private final JButton markSelectedButton = new JButton("Mark Selected as Read");
    private final JButton markReadButton = new JButton("Mark All as Read");
    private List<NotificationEntry> currentNotifications = List.of();

    public interface NotificationActionHandler {
        String getLabel();

        boolean isSupported(NotificationEntry notification);

        boolean handle(Component owner, NotificationEntry notification);
    }

    public static void showDialog(Component owner) {
        showDialog(
                owner,
                "System Notifications",
                "Recent actions across payroll, employee, leave, attendance, and account workflows",
                () -> new NotificationService().getRecentNotifications(300),
                null
        );
    }

    public static void showDialog(Component owner,
                                  String title,
                                  String subtitle,
                                  Supplier<List<NotificationEntry>> notificationsSupplier) {
        showDialog(owner, title, subtitle, notificationsSupplier, null);
    }

    public static void showDialog(Component owner,
                                  String title,
                                  String subtitle,
                                  Supplier<List<NotificationEntry>> notificationsSupplier,
                                  NotificationActionHandler actionHandler) {
        Window window = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(owner);
        NotificationCenterDialog dialog = new NotificationCenterDialog(window, title, subtitle, notificationsSupplier, actionHandler);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private NotificationCenterDialog(Window owner,
                                     String headerTitle,
                                     String headerSubtitle,
                                     Supplier<List<NotificationEntry>> notificationsSupplier,
                                     NotificationActionHandler actionHandler) {
        super(owner, "Notifications", Dialog.ModalityType.APPLICATION_MODAL);
        this.headerTitle = headerTitle;
        this.headerSubtitle = headerSubtitle;
        this.notificationsSupplier = Objects.requireNonNull(notificationsSupplier, "notificationsSupplier");
        this.actionHandler = actionHandler;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 620));
        setPreferredSize(new Dimension(1180, 700));
        setResizable(true);
        setLayout(new BorderLayout(0, 14));
        getContentPane().setBackground(BrandTheme.IVORY);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContentSplitPane(), BorderLayout.CENTER);

        reloadNotifications();
        pack();
        setSize(Math.max(getWidth(), 1180), Math.max(getHeight(), 700));
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
        BrandTheme.styleSecondaryButton(markSelectedButton);
        BrandTheme.styleSecondaryButton(markReadButton);
        if (actionHandler != null) {
            actionButton.setText(actionHandler.getLabel());
            BrandTheme.stylePrimaryButton(actionButton);
            actionButton.setEnabled(false);
            actionButton.addActionListener(evt -> runPrimaryAction());
        }
        BrandTheme.stylePrimaryButton(closeButton);
        refreshButton.addActionListener(evt -> reloadNotifications());
        markSelectedButton.addActionListener(evt -> markSelectedAsRead());
        markReadButton.addActionListener(evt -> markAllAsRead());
        closeButton.addActionListener(evt -> dispose());

        actions.add(refreshButton);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(markSelectedButton);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(markReadButton);
        actions.add(Box.createHorizontalStrut(10));
        if (actionHandler != null) {
            actions.add(actionButton);
            actions.add(Box.createHorizontalStrut(10));
        }
        actions.add(closeButton);

        header.add(textPanel, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildContentSplitPane() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildTableSection(),
                buildPreviewSection()
        );
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerLocation(620);
        splitPane.setDividerSize(10);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        return splitPane;
    }

    private JScrollPane buildTableSection() {
        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
            "Timestamp", "Actor", "Role", "Action", "Summary"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        BrandTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new NotificationTableRenderer());
        table.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                updatePreview();
                updateReadButtonState();
                updateActionButtonState();
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(96);
        table.getColumnModel().getColumn(2).setPreferredWidth(96);
        table.getColumnModel().getColumn(3).setPreferredWidth(132);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(table);
        BrandTheme.styleScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18, 18, 18, 10),
                scrollPane.getBorder()
        ));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(14);
        return scrollPane;
    }

    private JScrollPane buildPreviewSection() {
        JPanel previewPanel = new JPanel(new BorderLayout(0, 14));
        BrandTheme.styleCardSurface(previewPanel);
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18, 8, 18, 18),
                previewPanel.getBorder()
        ));

        JLabel titleLabel = new JLabel("Notification Preview");
        titleLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
        titleLabel.setForeground(BrandTheme.PRIMARY_BLUE);

        JPanel metadataGrid = new JPanel(new java.awt.GridLayout(0, 2, 12, 10));
        metadataGrid.setOpaque(false);
        metadataGrid.add(buildPreviewBlock("Timestamp", previewTimestampValue));
        metadataGrid.add(buildPreviewBlock("Status", previewStateValue));
        metadataGrid.add(buildPreviewBlock("Actor", previewActorValue));
        metadataGrid.add(buildPreviewBlock("Role", previewRoleValue));
        metadataGrid.add(buildPreviewBlock("Action", previewActionValue));
        metadataGrid.add(buildPreviewBlock("Preview", buildPreviewHintLabel()));

        previewTextArea.setEditable(false);
        previewTextArea.setLineWrap(true);
        previewTextArea.setWrapStyleWord(true);
        previewTextArea.setFont(BrandTheme.BODY_FONT.deriveFont(12.5f));
        previewTextArea.setForeground(BrandTheme.TEXT_DARK);
        previewTextArea.setBackground(BrandTheme.INPUT_BG);
        previewTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(metadataGrid, BorderLayout.CENTER);

        JScrollPane previewScrollPane = new JScrollPane(previewTextArea);
        BrandTheme.styleScrollPane(previewScrollPane);
        previewScrollPane.setBorder(BorderFactory.createEmptyBorder());
        previewScrollPane.getVerticalScrollBar().setUnitIncrement(14);

        previewPanel.add(top, BorderLayout.NORTH);
        previewPanel.add(previewScrollPane, BorderLayout.CENTER);

        JScrollPane container = new JScrollPane(previewPanel);
        BrandTheme.styleScrollPane(container);
        container.setBorder(BorderFactory.createEmptyBorder());
        container.getVerticalScrollBar().setUnitIncrement(14);
        return container;
    }

    private void reloadNotifications() {
        List<NotificationEntry> notifications = new ArrayList<>(notificationsSupplier.get());
        currentNotifications = notifications;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (NotificationEntry notification : notifications) {
            model.addRow(new Object[]{
                formatTimestamp(notification.getTimestamp()),
                notification.getActor(),
                notification.getRole(),
                formatAction(notification.getAction()),
                summarizeNotificationDetails(notification.getDetails())
            });
        }

        if (model.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            updatePreview();
            updateReadButtonState();
            updateActionButtonState();
        } else {
            updatePreview();
            updateReadButtonState();
            updateActionButtonState();
        }
        markReadButton.setEnabled(!currentNotifications.isEmpty());
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

    private String formatAction(String action) {
        if (action == null || action.isBlank()) {
            return "Notification";
        }

        String normalized = action.trim().replace('_', ' ').toLowerCase();
        String[] words = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                builder.append(word.substring(1));
            }
        }
        return builder.toString();
    }

    private void updateActionButtonState() {
        if (actionHandler == null) {
            return;
        }
        NotificationEntry notification = getSelectedNotification();
        actionButton.setEnabled(notification != null && actionHandler.isSupported(notification));
    }

    private void updateReadButtonState() {
        NotificationEntry notification = getSelectedNotification();
        markSelectedButton.setEnabled(notification != null && !notification.isRead());
    }

    private void updatePreview() {
        NotificationEntry notification = getSelectedNotification();
        if (notification == null) {
            previewTimestampValue.setText("Not Available");
            previewActorValue.setText("Not Available");
            previewRoleValue.setText("Not Available");
            previewActionValue.setText("Not Available");
            previewStateValue.setText("No selection");
            previewTextArea.setText("Select a notification to review its full details.");
            return;
        }

        previewTimestampValue.setText(formatTimestamp(notification.getTimestamp()));
        previewActorValue.setText(safeValue(notification.getActor()));
        previewRoleValue.setText(safeValue(notification.getRole()));
        previewActionValue.setText(formatAction(notification.getAction()));
        previewStateValue.setText(notification.isRead() ? "Read" : "Unread");
        previewTextArea.setText(formatNotificationDetails(notification.getDetails()));
        previewTextArea.setCaretPosition(0);
    }

    private NotificationEntry getSelectedNotification() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= currentNotifications.size()) {
            return null;
        }

        return currentNotifications.get(modelRow);
    }

    private void runPrimaryAction() {
        NotificationEntry notification = getSelectedNotification();
        if (notification == null || actionHandler == null || !actionHandler.isSupported(notification)) {
            return;
        }

        if (actionHandler.handle(this, notification)) {
            reloadNotifications();
        }
    }

    private void markAllAsRead() {
        if (currentNotifications.isEmpty()) {
            return;
        }

        new NotificationService().markAsRead(currentNotifications);
        reloadNotifications();
    }

    private void markSelectedAsRead() {
        NotificationEntry notification = getSelectedNotification();
        if (notification == null || notification.isRead()) {
            return;
        }

        new NotificationService().markAsRead(List.of(notification));
        reloadNotifications();
    }

    private JPanel buildPreviewBlock(String labelText, Component valueComponent) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        label.setForeground(BrandTheme.MUTED);

        block.add(label, BorderLayout.NORTH);
        block.add(valueComponent, BorderLayout.CENTER);
        return block;
    }

    private JLabel buildPreviewHintLabel() {
        JLabel label = new JLabel("Resize the divider to expand this preview.");
        label.setFont(BrandTheme.BODY_FONT.deriveFont(12f));
        label.setForeground(BrandTheme.MUTED);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private JLabel createPreviewValueLabel() {
        JLabel label = new JLabel("Not Available");
        label.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 12.5f));
        label.setForeground(BrandTheme.TEXT_DARK);
        return label;
    }

    private String summarizeNotificationDetails(String details) {
        String formatted = formatNotificationDetails(details);
        if (formatted.length() <= 72) {
            return formatted;
        }
        return formatted.substring(0, 69).trim() + "...";
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

    private class NotificationTableRenderer extends DefaultTableCellRenderer {

        private final Color unreadBackground = new Color(0xFF, 0xF1, 0xF3);
        private final Color unreadBorder = new Color(0xF3, 0xC2, 0xC8);

        @Override
        public Component getTableCellRendererComponent(JTable source,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component component = super.getTableCellRendererComponent(source, value, isSelected, hasFocus, row, column);
            int modelRow = source.convertRowIndexToModel(row);
            NotificationEntry notification = modelRow >= 0 && modelRow < currentNotifications.size()
                    ? currentNotifications.get(modelRow)
                    : null;

            if (isSelected) {
                component.setBackground(BrandTheme.ACCENT_BLUE);
                component.setForeground(BrandTheme.TEXT_DARK);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            } else if (notification != null && !notification.isRead()) {
                component.setBackground(unreadBackground);
                component.setForeground(BrandTheme.TEXT_DARK);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, unreadBorder));
            } else {
                component.setBackground(row % 2 == 0 ? BrandTheme.INPUT_BG : BrandTheme.TABLE_ALT);
                component.setForeground(BrandTheme.TEXT_DARK);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            }

            setFont(notification != null && !notification.isRead()
                    ? BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 12.5f)
                    : BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 12.5f));
            return component;
        }
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "Not Available" : value.trim();
    }
}

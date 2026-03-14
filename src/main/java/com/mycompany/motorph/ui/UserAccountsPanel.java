package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AccessControlService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class UserAccountsPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final NotificationService notificationService = new NotificationService();
    private final AccessControlService accessControlService = new AccessControlService();
    private final User actorUser;
    private final JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"Username", "Role", "Employee Number"});
    private final JTextField searchField = new JTextField();
    private final JTable accountsTable = new JTable();
    private final JLabel summaryLabel = new JLabel(" ");
    private final JLabel filterLabel = new JLabel("Filter By:");
    private final JLabel searchLabel = new JLabel("Search");
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnResetDefault = new JButton("Reset to Default");
    private final JButton btnSetPassword = new JButton("Set Password");
    private JPanel headerControlsPanel;
    private List<User> users = new ArrayList<>();
    private List<User> visibleUsers = new ArrayList<>();

    public UserAccountsPanel() {
        this(null);
    }

    public UserAccountsPanel(User actorUser) {
        this.actorUser = actorUser;
        setLayout(new BorderLayout(0, 14));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        BrandTheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryLabel, BorderLayout.SOUTH);

        accountsTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
            "Username", "Role", "Employee Number"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        BrandTheme.styleTable(accountsTable);
        accountsTable.setFillsViewportHeight(true);
        accountsTable.setAutoCreateRowSorter(true);
        accountsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        configureResponsiveColumns();
        BrandTheme.styleComboBox(filterComboBox);
        BrandTheme.styleInputField(searchField);
        searchField.setPreferredSize(new Dimension(210, 34));
        searchField.setMinimumSize(new Dimension(210, 34));
        searchField.setMaximumSize(new Dimension(210, 34));
        filterComboBox.setPreferredSize(new Dimension(170, 34));
        filterComboBox.setMinimumSize(new Dimension(170, 34));
        filterComboBox.setMaximumSize(new Dimension(170, 34));
        summaryLabel.setForeground(BrandTheme.MUTED);
        summaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
        filterLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        filterLabel.setForeground(BrandTheme.TEXT);
        searchLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        searchLabel.setForeground(BrandTheme.TEXT);
        btnSearch.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        btnRefresh.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        btnResetDefault.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        btnSetPassword.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        applyRolePermissions();

        reloadUsers();
    }

    private void applyRolePermissions() {
        if (actorUser == null) {
            return;
        }
        boolean canManagePasswords = accessControlService.canAccess(actorUser, "Reset account passwords");
        btnResetDefault.setEnabled(canManagePasswords);
        btnSetPassword.setEnabled(canManagePasswords);
    }

    public final void reloadUsers() {
        users = userDAO.loadUsers();
        applyFilter();
    }

    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("User Accounts");
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(BrandTheme.TEXT);
        header.add(title, BorderLayout.NORTH);

        BrandTheme.styleSecondaryButton(btnSearch);
        BrandTheme.styleSecondaryButton(btnRefresh);
        BrandTheme.styleDangerButton(btnResetDefault);
        BrandTheme.stylePrimaryButton(btnSetPassword);

        btnSearch.addActionListener(evt -> applyFilter());
        btnRefresh.addActionListener(evt -> reloadUsers());
        btnResetDefault.addActionListener(evt -> resetSelectedToDefault());
        btnSetPassword.addActionListener(evt -> setSelectedPassword());
        searchField.addActionListener(evt -> applyFilter());

        headerControlsPanel = new JPanel(new BorderLayout());
        headerControlsPanel.setOpaque(false);
        header.add(headerControlsPanel, BorderLayout.CENTER);
        updateHeaderLayout();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateHeaderLayout();
            }
        });
        return header;
    }

    private void updateHeaderLayout() {
        if (headerControlsPanel == null) {
            return;
        }

        headerControlsPanel.removeAll();
        int width = getWidth();

        if (width > 900) {
            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            controls.setOpaque(false);
            controls.add(filterLabel);
            controls.add(filterComboBox);
            controls.add(searchLabel);
            controls.add(searchField);
            controls.add(btnSearch);
            controls.add(btnRefresh);
            controls.add(btnResetDefault);
            controls.add(btnSetPassword);
            headerControlsPanel.add(controls, BorderLayout.CENTER);
        } else {
            searchField.setPreferredSize(new Dimension(170, 34));

            JPanel rowOne = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            rowOne.setOpaque(false);
            rowOne.add(filterLabel);
            rowOne.add(filterComboBox);
            rowOne.add(searchLabel);
            rowOne.add(searchField);
            rowOne.add(btnSearch);

            JPanel rowTwo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            rowTwo.setOpaque(false);
            rowTwo.add(btnRefresh);
            rowTwo.add(btnResetDefault);
            rowTwo.add(btnSetPassword);

            JPanel stacked = new JPanel(new BorderLayout(0, 8));
            stacked.setOpaque(false);
            stacked.add(rowOne, BorderLayout.NORTH);
            stacked.add(rowTwo, BorderLayout.CENTER);
            headerControlsPanel.add(stacked, BorderLayout.CENTER);
        }

        if (width > 900) {
            searchField.setPreferredSize(new Dimension(210, 34));
        }

        headerControlsPanel.revalidate();
        headerControlsPanel.repaint();
    }

    private void applyFilter() {

        String query = searchField.getText().trim().toLowerCase();
        String selectedFilter = String.valueOf(filterComboBox.getSelectedItem());
        visibleUsers = new ArrayList<>();

        for (User user : users) {
            String candidate;
            if ("Role".equals(selectedFilter)) {
                candidate = user.getRole();
            } else if ("Employee Number".equals(selectedFilter)) {
                candidate = String.valueOf(user.getEmployeeNumber());
            } else {
                candidate = user.getUsername();
            }

            String value = candidate == null ? "" : candidate.toLowerCase();
            if (query.isEmpty() || value.contains(query)) {
                visibleUsers.add(user);
            }
        }

        DefaultTableModel model = (DefaultTableModel) accountsTable.getModel();
        model.setRowCount(0);

        for (User user : visibleUsers) {
            model.addRow(new Object[]{user.getUsername(), user.getRole(), user.getEmployeeNumber()});
        }

        summaryLabel.setText("Showing " + visibleUsers.size() + " user accounts.");
    }

    private void configureResponsiveColumns() {
        if (accountsTable.getColumnModel().getColumnCount() < 3) {
            return;
        }

        accountsTable.getTableHeader().setResizingAllowed(true);
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(190);
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(170);
    }

    private User getSelectedUser() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = accountsTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= visibleUsers.size()) {
            return null;
        }
        return visibleUsers.get(modelRow);
    }

    private void resetSelectedToDefault() {

        User user = getSelectedUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Select a user account first.");
            return;
        }

        if (user.getEmployeeNumber() <= 0) {
            JOptionPane.showMessageDialog(this, "Default password reset requires an employee-linked account.");
            return;
        }

        user.setPassword("emp" + user.getEmployeeNumber());
        userDAO.saveUsers(users);
        notificationService.record(
                actorUser,
                "PASSWORD_RESET",
                "Password was reset to the default value for user " + user.getUsername() + "."
        );
        JOptionPane.showMessageDialog(this, "Password reset to the default employee password.");
        reloadUsers();
    }

    private void setSelectedPassword() {

        User user = getSelectedUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Select a user account first.");
            return;
        }

        String newPassword = DialogHelper.promptPassword(
                this,
                "Set Password",
                "Enter a new password for " + user.getUsername() + ":",
                "Save Password"
        );
        if (newPassword == null) {
            return;
        }

        if (newPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            return;
        }

        user.setPassword(newPassword.trim());
        userDAO.saveUsers(users);
        notificationService.record(
                actorUser,
                "PASSWORD_SET",
                "Password was manually updated for user " + user.getUsername() + "."
        );
        JOptionPane.showMessageDialog(this, "Password updated successfully.");
        reloadUsers();
    }
}

package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AccessControlService;
import com.mycompany.motorph.service.AppClock;
import com.mycompany.motorph.service.LeaveService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class pnlLeaveRequest extends javax.swing.JPanel {

    private static final String[] DEFAULT_SEARCH_FILTERS = {
            "Employee Name",
            "Employee ID"
    };
    private static final String FILTER_ALL = "All";
    private static final String SEARCH_SCOPE_ALL = "All Fields";
    private static final int RECENT_NOTIFICATION_LIMIT = 25;
    private static final Pattern NOTIFICATION_DATE_RANGE = Pattern.compile(
            "from\\s+(\\d{4}-\\d{2}-\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2})",
            Pattern.CASE_INSENSITIVE
    );

    private final LeaveService leaveService = new LeaveService();
    private final NotificationService notificationService = new NotificationService();
    private final AccessControlService accessControlService = new AccessControlService();
    private final User actorUser;
    private final boolean responsiveLayout;
    private final Runnable onRefreshRequested;
    private final String[] searchFilterOptions;
    private final boolean includeAllSearchScope;

    private List<LeaveRequest> requests = new ArrayList<>();
    private List<LeaveRequest> visibleRequests = new ArrayList<>();
    private JComboBox<String> cmbSearchFilter;
    private JComboBox<String> cmbStatusFilter;
    private JComboBox<String> cmbLeaveTypeFilter;
    private DatePickerField startDateFilterField;
    private DatePickerField endDateFilterField;
    private JLabel filterLabel;
    private JLabel searchLabel;
    private JLabel statusFilterLabel;
    private JLabel leaveTypeFilterLabel;
    private JLabel startDateFilterLabel;
    private JLabel endDateFilterLabel;
    private JLabel queueSummaryLabel;
    private JPanel discrepancyBanner;
    private JLabel discrepancyBannerLabel;
    private JButton btnShowAll;
    private JButton btnPendingOnly;
    private JButton btnRecentRequests;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private boolean suppressFilterEvents;

    public pnlLeaveRequest() {
        this(null);
    }

    public pnlLeaveRequest(User actorUser) {
        this(actorUser, false);
    }

    public pnlLeaveRequest(User actorUser, boolean responsiveLayout) {
        this(actorUser, responsiveLayout, null, null);
    }

    public pnlLeaveRequest(User actorUser, boolean responsiveLayout, String[] searchFilterOptions) {
        this(actorUser, responsiveLayout, searchFilterOptions, null);
    }

    public pnlLeaveRequest(User actorUser,
                           boolean responsiveLayout,
                           String[] searchFilterOptions,
                           Runnable onRefreshRequested) {
        this.actorUser = actorUser;
        this.responsiveLayout = responsiveLayout;
        this.onRefreshRequested = onRefreshRequested == null ? () -> { } : onRefreshRequested;
        this.includeAllSearchScope = searchFilterOptions != null && searchFilterOptions.length > 0;
        this.searchFilterOptions = searchFilterOptions == null || searchFilterOptions.length == 0
                ? DEFAULT_SEARCH_FILTERS.clone()
                : searchFilterOptions.clone();
        initComponents();
        BrandTheme.styleSurface(this);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        BrandTheme.setTitleWithLogo(lblLeaveRequest, "Leave Requests");
        if (responsiveLayout) {
            lblLeaveRequest.setIcon(null);
            lblLeaveRequest.setText("Leave Requests");
        }
        BrandTheme.styleTable(jTable1);
        BrandTheme.styleInputField(txtSearchLeaveRequest);
        BrandTheme.styleScrollPane(jScrollPane1);
        BrandTheme.styleSecondaryButton(btnSearchLeaveRequest);
        BrandTheme.stylePrimaryButton(btnRespond);
        BrandTheme.styleSecondaryButton(btnRefresh);
        configureTableBehavior();
        applyRolePermissions();
        if (responsiveLayout) {
            rebuildLayout();
        }
        reloadRequests();
    }

    private void applyRolePermissions() {
        if (actorUser == null) {
            return;
        }
        btnRespond.setEnabled(accessControlService.canAccess(actorUser, "Review leave requests"));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLeaveRequest = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnRespond = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        txtSearchLeaveRequest = new javax.swing.JTextField();
        btnSearchLeaveRequest = new javax.swing.JButton();

        lblLeaveRequest.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLeaveRequest.setForeground(new java.awt.Color(0, 51, 102));
        lblLeaveRequest.setText("Leave Request");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Employee Name", "Leave Type", "Start Date", "End Date", "Days", "Status", "Remarks"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        btnRespond.setText("Respond");
        btnRespond.addActionListener(this::btnRespondActionPerformed);

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        txtSearchLeaveRequest.addActionListener(this::txtSearchLeaveRequestActionPerformed);

        btnSearchLeaveRequest.setText("Search");
        btnSearchLeaveRequest.addActionListener(this::btnSearchLeaveRequestActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearchLeaveRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearchLeaveRequest))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRespond)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 719, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLeaveRequest))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(lblLeaveRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchLeaveRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchLeaveRequest))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRespond)
                    .addComponent(btnRefresh))
                .addContainerGap(116, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public final void reloadRequests() {
        requests = new ArrayList<>(leaveService.getRequests());
        populateLeaveTypeFilterOptions();
        applyCurrentFilters();
        onRefreshRequested.run();
    }

    private void rebuildLayout() {
        initFilterControls();

        removeAll();
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(buildFilterCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildQueueCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildActionCard());

        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void initFilterControls() {
        if (cmbStatusFilter == null) {
            cmbStatusFilter = createFilterCombo(new String[]{FILTER_ALL, "Pending", "Approved", "Rejected"});
        }
        if (cmbLeaveTypeFilter == null) {
            cmbLeaveTypeFilter = createFilterCombo(new String[]{FILTER_ALL});
        }
        if (cmbSearchFilter == null) {
            String[] options = includeAllSearchScope
                    ? withAllSearchScope(searchFilterOptions)
                    : searchFilterOptions.clone();
            cmbSearchFilter = createFilterCombo(options);
        }
        if (filterLabel == null) {
            filterLabel = createFilterLabel("Search In");
        }
        if (searchLabel == null) {
            searchLabel = createFilterLabel("Employee");
        }
        if (statusFilterLabel == null) {
            statusFilterLabel = createFilterLabel("Status");
        }
        if (leaveTypeFilterLabel == null) {
            leaveTypeFilterLabel = createFilterLabel("Leave Type");
        }
        if (startDateFilterLabel == null) {
            startDateFilterLabel = createFilterLabel("Start Date");
        }
        if (endDateFilterLabel == null) {
            endDateFilterLabel = createFilterLabel("End Date");
        }
        if (startDateFilterField == null) {
            startDateFilterField = new DatePickerField();
            startDateFilterField.setPreferredSize(new Dimension(170, 34));
            startDateFilterField.setMaximumSize(new Dimension(170, 34));
            startDateFilterField.setOnDateChange(() -> {
                if (!suppressFilterEvents) {
                    applyCurrentFilters();
                }
            });
        }
        if (endDateFilterField == null) {
            endDateFilterField = new DatePickerField();
            endDateFilterField.setPreferredSize(new Dimension(170, 34));
            endDateFilterField.setMaximumSize(new Dimension(170, 34));
            endDateFilterField.setOnDateChange(() -> {
                if (!suppressFilterEvents) {
                    applyCurrentFilters();
                }
            });
        }
        if (queueSummaryLabel == null) {
            queueSummaryLabel = new JLabel("Showing 0 leave requests.");
            queueSummaryLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 13f));
            queueSummaryLabel.setForeground(BrandTheme.MUTED);
        }
        if (discrepancyBanner == null) {
            discrepancyBannerLabel = new JLabel();
            discrepancyBannerLabel.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
            discrepancyBannerLabel.setForeground(new Color(0x92, 0x45, 0x00));
            discrepancyBanner = new JPanel(new BorderLayout());
            discrepancyBanner.setOpaque(true);
            discrepancyBanner.setBackground(new Color(0xFF, 0xFB, 0xEB));
            discrepancyBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xFD, 0xBA, 0x74), 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            discrepancyBanner.add(discrepancyBannerLabel, BorderLayout.CENTER);
            discrepancyBanner.setVisible(false);
        }
        if (btnShowAll == null) {
            btnShowAll = createQuickButton("Show All", evt -> applyShowAllQuickFilter());
        }
        if (btnPendingOnly == null) {
            btnPendingOnly = createQuickButton("Pending Only", evt -> applyPendingQuickFilter());
        }
        if (btnRecentRequests == null) {
            btnRecentRequests = createQuickButton("Recent Requests", evt -> applyRecentQuickFilter());
        }

        txtSearchLeaveRequest.setPreferredSize(new Dimension(220, 34));
        txtSearchLeaveRequest.setMaximumSize(new Dimension(220, 34));
        btnSearchLeaveRequest.setPreferredSize(new Dimension(96, 36));
        btnSearchLeaveRequest.setMaximumSize(new Dimension(96, 36));
        btnRefresh.setPreferredSize(new Dimension(96, 36));
        btnRefresh.setMaximumSize(new Dimension(96, 36));
        btnRespond.setPreferredSize(new Dimension(110, 36));
        btnRespond.setMaximumSize(new Dimension(110, 36));

        attachFilterListeners();
    }

    private void attachFilterListeners() {
        if (cmbStatusFilter != null && cmbStatusFilter.getActionListeners().length == 0) {
            cmbStatusFilter.addActionListener(evt -> {
                if (!suppressFilterEvents) {
                    applyCurrentFilters();
                }
            });
        }
        if (cmbLeaveTypeFilter != null && cmbLeaveTypeFilter.getActionListeners().length == 0) {
            cmbLeaveTypeFilter.addActionListener(evt -> {
                if (!suppressFilterEvents) {
                    applyCurrentFilters();
                }
            });
        }
        if (cmbSearchFilter != null && cmbSearchFilter.getActionListeners().length == 0) {
            cmbSearchFilter.addActionListener(evt -> {
                if (!suppressFilterEvents) {
                    applyCurrentFilters();
                }
            });
        }
    }

    private JPanel buildFilterCard() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        lblLeaveRequest.setAlignmentX(LEFT_ALIGNMENT);
        content.add(lblLeaveRequest);
        content.add(Box.createVerticalStrut(16));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        filterRow.setOpaque(false);
        if (cmbSearchFilter != null) {
            filterRow.add(createFilterBlock(filterLabel, cmbSearchFilter));
        }
        filterRow.add(createEmployeeSearchBlock());
        filterRow.add(createFilterBlock(statusFilterLabel, cmbStatusFilter));
        filterRow.add(createFilterBlock(leaveTypeFilterLabel, cmbLeaveTypeFilter));
        filterRow.add(createFilterBlock(startDateFilterLabel, startDateFilterField));
        filterRow.add(createFilterBlock(endDateFilterLabel, endDateFilterField));
        content.add(filterRow);

        content.add(Box.createVerticalStrut(14));

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickActions.setOpaque(false);
        quickActions.add(btnShowAll);
        quickActions.add(btnPendingOnly);
        quickActions.add(btnRecentRequests);
        quickActions.add(btnRefresh);
        content.add(quickActions);

        return wrapInCard(content);
    }

    private JPanel buildQueueCard() {
        JPanel tableCard = new JPanel(new BorderLayout(0, 14));
        BrandTheme.styleCardSurface(tableCard);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createSectionLabel("Leave Request Queue"));
        header.add(Box.createVerticalStrut(4));
        header.add(queueSummaryLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(discrepancyBanner);

        tableCard.add(header, BorderLayout.NORTH);
        tableCard.add(jScrollPane1, BorderLayout.CENTER);
        return tableCard;
    }

    private JPanel buildActionCard() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnRespond);
        return wrapInCard(actions);
    }

    private JPanel wrapInCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        BrandTheme.styleCardSurface(card);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
        label.setForeground(BrandTheme.PRIMARY_BLUE);
        return label;
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
        label.setForeground(BrandTheme.TEXT);
        return label;
    }

    private JComboBox<String> createFilterCombo(String[] values) {
        JComboBox<String> comboBox = new JComboBox<>(values);
        BrandTheme.styleComboBox(comboBox);
        comboBox.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        comboBox.setPreferredSize(new Dimension(170, 34));
        comboBox.setMaximumSize(new Dimension(170, 34));
        return comboBox;
    }

    private JButton createQuickButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        BrandTheme.styleSecondaryButton(button);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.addActionListener(listener);
        return button;
    }

    private JPanel createFilterBlock(JLabel label, Component field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        if (field instanceof javax.swing.JComponent component) {
            component.setAlignmentX(LEFT_ALIGNMENT);
        }
        panel.add(field);
        return panel;
    }

    private JPanel createEmployeeSearchBlock() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        searchLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(searchLabel);
        panel.add(Box.createVerticalStrut(6));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.add(txtSearchLeaveRequest);
        row.add(btnSearchLeaveRequest);
        row.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(row);
        return panel;
    }

    private void configureTableBehavior() {
        jTable1.setFillsViewportHeight(true);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setRowHeight(42);
        configureResponsiveColumns();
        configureColumnRenderers();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        tableSorter = new TableRowSorter<>(model);
        tableSorter.setComparator(6, Comparator.comparingInt(this::statusSortOrder));
        jTable1.setRowSorter(tableSorter);
        applyDefaultSorting();
    }

    private void configureResponsiveColumns() {
        if (jTable1.getColumnModel().getColumnCount() < 8) {
            return;
        }

        jTable1.getTableHeader().setResizingAllowed(true);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(240);
    }

    private void configureColumnRenderers() {
        if (jTable1.getColumnModel().getColumnCount() < 8) {
            return;
        }

        jTable1.getColumnModel().getColumn(0).setCellRenderer(createStandardRenderer(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(1).setCellRenderer(createStandardRenderer(SwingConstants.LEFT));
        jTable1.getColumnModel().getColumn(2).setCellRenderer(createStandardRenderer(SwingConstants.LEFT));
        jTable1.getColumnModel().getColumn(3).setCellRenderer(createStandardRenderer(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(4).setCellRenderer(createStandardRenderer(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(5).setCellRenderer(createStandardRenderer(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(6).setCellRenderer(createStatusRenderer());
        jTable1.getColumnModel().getColumn(7).setCellRenderer(createStandardRenderer(SwingConstants.LEFT));
    }

    private DefaultTableCellRenderer createStandardRenderer(int alignment) {
        return new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(alignment);
            }

            @Override
            public Component getTableCellRendererComponent(JTable source,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                super.getTableCellRendererComponent(source, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (isSelected) {
                    setBackground(BrandTheme.SKY);
                    setForeground(BrandTheme.TEXT);
                } else {
                    setBackground(row % 2 == 0 ? BrandTheme.INPUT_BG : BrandTheme.TABLE_ALT);
                    setForeground(BrandTheme.TEXT);
                }
                return this;
            }
        };
    }

    private DefaultTableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
            }

            @Override
            public Component getTableCellRendererComponent(JTable source,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                super.getTableCellRendererComponent(source, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (isSelected) {
                    setBackground(BrandTheme.SKY);
                    setForeground(BrandTheme.TEXT);
                    return this;
                }

                String status = value == null ? "" : value.toString().trim().toUpperCase();
                switch (status) {
                    case "APPROVED" -> {
                        setBackground(new Color(0xDC, 0xFC, 0xE7));
                        setForeground(new Color(0x16, 0x65, 0x34));
                    }
                    case "REJECTED" -> {
                        setBackground(new Color(0xFE, 0xE2, 0xE2));
                        setForeground(BrandTheme.MOTORPH_RED);
                    }
                    case "PENDING" -> {
                        setBackground(new Color(0xFE, 0xF3, 0xC7));
                        setForeground(new Color(0x92, 0x45, 0x00));
                    }
                    default -> {
                        setBackground(row % 2 == 0 ? BrandTheme.INPUT_BG : BrandTheme.TABLE_ALT);
                        setForeground(BrandTheme.TEXT);
                    }
                }
                return this;
            }
        };
    }

    private void populateLeaveTypeFilterOptions() {
        if (cmbLeaveTypeFilter == null) {
            return;
        }

        Object currentSelection = cmbLeaveTypeFilter.getSelectedItem();
        List<String> leaveTypes = new ArrayList<>();
        leaveTypes.add(FILTER_ALL);
        leaveTypes.addAll(LeaveTypeCatalog.filterOptionsWithExtras(
                requests.stream()
                        .map(LeaveRequest::getLeaveType)
                        .toList()
        ));

        runWithoutFilterEvents(() -> {
            cmbLeaveTypeFilter.setModel(new DefaultComboBoxModel<>(leaveTypes.toArray(String[]::new)));
            cmbLeaveTypeFilter.setSelectedItem(leaveTypes.contains(currentSelection) ? currentSelection : FILTER_ALL);
        });
    }

    private void applyCurrentFilters() {
        List<LeaveRequest> filtered = filterRequests();
        visibleRequests = new ArrayList<>(filtered);
        populateTable(filtered);
        updateQueueSummary(filtered);
        updateDiscrepancyBanner(filtered);
    }

    private List<LeaveRequest> filterRequests() {
        List<LeaveRequest> filtered = new ArrayList<>();
        String query = txtSearchLeaveRequest.getText().trim().toLowerCase();
        String searchScope = selectedText(cmbSearchFilter, SEARCH_SCOPE_ALL);
        String status = selectedText(cmbStatusFilter, FILTER_ALL);
        String leaveType = selectedText(cmbLeaveTypeFilter, FILTER_ALL);
        LocalDate startDate = startDateFilterField == null ? null : startDateFilterField.getDate();
        LocalDate endDate = endDateFilterField == null ? null : endDateFilterField.getDate();

        for (LeaveRequest request : requests) {
            if (!matchesStatus(request, status)) {
                continue;
            }
            if (!matchesLeaveType(request, leaveType)) {
                continue;
            }
            if (!matchesDateRange(request, startDate, endDate)) {
                continue;
            }
            if (!matchesSearch(request, query, searchScope)) {
                continue;
            }
            filtered.add(request);
        }
        return filtered;
    }

    private boolean matchesStatus(LeaveRequest request, String status) {
        return FILTER_ALL.equalsIgnoreCase(status)
                || request.getStatus().equalsIgnoreCase(status);
    }

    private boolean matchesLeaveType(LeaveRequest request, String leaveType) {
        return FILTER_ALL.equalsIgnoreCase(leaveType)
                || request.getLeaveType().equalsIgnoreCase(leaveType);
    }

    private boolean matchesDateRange(LeaveRequest request, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return true;
        }

        LocalDate requestStart = parseDate(request.getStartDate());
        if (requestStart == null) {
            return false;
        }

        if (startDate != null && requestStart.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && requestStart.isAfter(endDate)) {
            return false;
        }
        return true;
    }

    private boolean matchesSearch(LeaveRequest request, String query, String searchScope) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String candidate;
        if ("Employee ID".equalsIgnoreCase(searchScope) || "Employee Number".equalsIgnoreCase(searchScope)) {
            candidate = String.valueOf(request.getEmployeeNumber());
        } else if ("Employee Name".equalsIgnoreCase(searchScope)) {
            candidate = request.getEmployeeName();
        } else if ("Leave Type".equalsIgnoreCase(searchScope)) {
            candidate = request.getLeaveType();
        } else if ("Status".equalsIgnoreCase(searchScope)) {
            candidate = request.getStatus();
        } else if (cmbSearchFilter == null) {
            candidate = request.getEmployeeNumber() + " "
                    + request.getEmployeeName();
        } else {
            candidate = request.getEmployeeNumber() + " "
                    + request.getEmployeeName() + " "
                    + request.getLeaveType() + " "
                    + request.getStatus() + " "
                    + request.getStatusMessage();
        }
        return candidate.toLowerCase().contains(query);
    }

    private void updateQueueSummary(List<LeaveRequest> filtered) {
        if (queueSummaryLabel == null) {
            return;
        }

        int pendingCount = 0;
        for (LeaveRequest request : filtered) {
            if ("PENDING".equalsIgnoreCase(request.getStatus())) {
                pendingCount++;
            }
        }

        String summary = filtered.size() == requests.size()
                ? "Showing " + filtered.size() + " leave requests."
                : "Showing " + filtered.size() + " of " + requests.size() + " leave requests.";
        summary += pendingCount == 1 ? " 1 pending for review." : " " + pendingCount + " pending for review.";
        queueSummaryLabel.setText(summary);
    }

    private void updateDiscrepancyBanner(List<LeaveRequest> filtered) {
        if (discrepancyBanner == null || discrepancyBannerLabel == null) {
            return;
        }

        List<String> missingNotifications = findMissingNotifications(filtered);
        if (missingNotifications.isEmpty()) {
            discrepancyBanner.setVisible(false);
            discrepancyBannerLabel.setText("");
            discrepancyBanner.setToolTipText(null);
            return;
        }

        String suffix = missingNotifications.size() == 1
                ? "1 recent item"
                : missingNotifications.size() + " recent items";
        discrepancyBannerLabel.setText("Some leave request notifications may not currently appear in the queue. Adjust filters or refresh to view all records. (" + suffix + ")");
        discrepancyBanner.setToolTipText("<html>" + String.join("<br>", missingNotifications) + "</html>");
        discrepancyBanner.setVisible(true);
    }

    private List<String> findMissingNotifications(List<LeaveRequest> filtered) {
        Set<String> missing = new LinkedHashSet<>();
        Set<String> visibleKeys = new LinkedHashSet<>();
        for (LeaveRequest request : filtered) {
            visibleKeys.add(toRequestKey(request));
        }

        // Match recent leave notifications against currently visible queue rows.
        for (NotificationEntry notification : notificationService.getRecentNotifications(RECENT_NOTIFICATION_LIMIT)) {
            if (!isLeaveQueueNotification(notification)) {
                continue;
            }

            String requestKey = toNotificationRequestKey(notification);
            if (requestKey == null || visibleKeys.contains(requestKey)) {
                continue;
            }

            missing.add(notification.getTimestamp() + " | " + notification.getActor() + " | " + notification.getDetails());
        }
        return new ArrayList<>(missing);
    }

    private boolean isLeaveQueueNotification(NotificationEntry notification) {
        if (notification == null) {
            return false;
        }
        String action = notification.getAction() == null ? "" : notification.getAction().trim().toUpperCase();
        return "LEAVE_REQUEST".equals(action) || "LEAVE_SUBMITTED".equals(action);
    }

    private String toNotificationRequestKey(NotificationEntry notification) {
        if (notification == null) {
            return null;
        }

        try {
            int employeeNumber = Integer.parseInt(notification.getActor().trim());
            Matcher matcher = NOTIFICATION_DATE_RANGE.matcher(notification.getDetails() == null ? "" : notification.getDetails());
            if (!matcher.find()) {
                return null;
            }
            return employeeNumber + "|" + matcher.group(1) + "|" + matcher.group(2);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toRequestKey(LeaveRequest request) {
        return request.getEmployeeNumber() + "|" + request.getStartDate() + "|" + request.getEndDate();
    }

    private void applyShowAllQuickFilter() {
        runWithoutFilterEvents(() -> {
            txtSearchLeaveRequest.setText("");
            if (cmbSearchFilter != null) {
                cmbSearchFilter.setSelectedItem(defaultSearchScope());
            }
            if (cmbStatusFilter != null) {
                cmbStatusFilter.setSelectedItem(FILTER_ALL);
            }
            if (cmbLeaveTypeFilter != null) {
                cmbLeaveTypeFilter.setSelectedItem(FILTER_ALL);
            }
            if (startDateFilterField != null) {
                startDateFilterField.setDate(null);
            }
            if (endDateFilterField != null) {
                endDateFilterField.setDate(null);
            }
        });
        applyCurrentFilters();
    }

    private void applyPendingQuickFilter() {
        runWithoutFilterEvents(() -> {
            txtSearchLeaveRequest.setText("");
            if (cmbSearchFilter != null) {
                cmbSearchFilter.setSelectedItem(defaultSearchScope());
            }
            if (cmbStatusFilter != null) {
                cmbStatusFilter.setSelectedItem("Pending");
            }
            if (cmbLeaveTypeFilter != null) {
                cmbLeaveTypeFilter.setSelectedItem(FILTER_ALL);
            }
            if (startDateFilterField != null) {
                startDateFilterField.setDate(null);
            }
            if (endDateFilterField != null) {
                endDateFilterField.setDate(null);
            }
        });
        applyCurrentFilters();
    }

    private void applyRecentQuickFilter() {
        runWithoutFilterEvents(() -> {
            txtSearchLeaveRequest.setText("");
            if (cmbSearchFilter != null) {
                cmbSearchFilter.setSelectedItem(defaultSearchScope());
            }
            if (cmbStatusFilter != null) {
                cmbStatusFilter.setSelectedItem(FILTER_ALL);
            }
            if (cmbLeaveTypeFilter != null) {
                cmbLeaveTypeFilter.setSelectedItem(FILTER_ALL);
            }
            if (startDateFilterField != null) {
                startDateFilterField.setDate(AppClock.today().minusDays(30));
            }
            if (endDateFilterField != null) {
                endDateFilterField.setDate(AppClock.today());
            }
        });
        applyCurrentFilters();
    }

    private void applyDefaultSorting() {
        if (tableSorter == null) {
            return;
        }
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        tableSorter.setSortKeys(sortKeys);
        tableSorter.sort();
    }

    private int statusSortOrder(Object value) {
        String status = value == null ? "" : value.toString().trim().toUpperCase();
        return switch (status) {
            case "PENDING" -> 0;
            case "APPROVED" -> 1;
            case "REJECTED" -> 2;
            default -> 3;
        };
    }

    private String selectedText(JComboBox<String> comboBox, String fallback) {
        if (comboBox == null || comboBox.getSelectedItem() == null) {
            return fallback;
        }
        return String.valueOf(comboBox.getSelectedItem());
    }

    private String defaultSearchScope() {
        if (includeAllSearchScope) {
            return SEARCH_SCOPE_ALL;
        }
        return searchFilterOptions.length == 0 ? SEARCH_SCOPE_ALL : searchFilterOptions[0];
    }

    private String[] withAllSearchScope(String[] options) {
        String[] values = new String[options.length + 1];
        values[0] = SEARCH_SCOPE_ALL;
        System.arraycopy(options, 0, values, 1, options.length);
        return values;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void runWithoutFilterEvents(Runnable task) {
        boolean previous = suppressFilterEvents;
        suppressFilterEvents = true;
        try {
            task.run();
        } finally {
            suppressFilterEvents = previous;
        }
    }

    private void populateTable(List<LeaveRequest> list) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (LeaveRequest request : list) {
            model.addRow(new Object[]{
                request.getEmployeeNumber(),
                request.getEmployeeName(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                computeLeaveDays(request),
                request.getStatus(),
                request.getStatusMessage()
            });
        }
        if (tableSorter != null) {
            tableSorter.sort();
        }
    }

    private long computeLeaveDays(LeaveRequest request) {
        try {
            LocalDate start = LocalDate.parse(request.getStartDate());
            LocalDate end = LocalDate.parse(request.getEndDate());
            return ChronoUnit.DAYS.between(start, end) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private LeaveRequest getSelectedRequest() {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = jTable1.convertRowIndexToModel(viewRow);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int employeeNumber = Integer.parseInt(String.valueOf(model.getValueAt(modelRow, 0)));
        String startDate = String.valueOf(model.getValueAt(modelRow, 3));
        String endDate = String.valueOf(model.getValueAt(modelRow, 4));

        for (int index = requests.size() - 1; index >= 0; index--) {
            LeaveRequest request = requests.get(index);
            if (request.getEmployeeNumber() == employeeNumber && request.getStartDate().equals(startDate)) {
                if (request.getEndDate().equals(endDate)) {
                    return request;
                }
            }
        }
        return null;
    }

    private void btnRespondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRespondActionPerformed
        LeaveRequest request = getSelectedRequest();
        if (request == null) {
            JOptionPane.showMessageDialog(this, "Select a leave request first.");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be processed.", "Leave Request", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String choice = DialogHelper.showActionChoice(
                this,
                "Leave Response",
                "Respond to leave request for " + request.getEmployeeName(),
                "Approve",
                "Reject",
                "Cancel"
        );

        if ("Approve".equals(choice)) {
            String statusMessage = promptStatusMessage(
                    "Approval Message",
                    "Enter an approval message:",
                    request.getStatusMessage().startsWith("Approved") ? request.getStatusMessage() : "Approved by HR/Admin.",
                    false
            );
            if (statusMessage == null) {
                return;
            }
            leaveService.respondToLeave(request.getEmployeeNumber(), request.getStartDate(), request.getEndDate(), true, statusMessage);
            notificationService.record(
                    actorUser,
                    "LEAVE_APPROVED",
                    "Approved leave for employee " + request.getEmployeeNumber() + " from " + request.getStartDate() + " to " + request.getEndDate() + "."
            );
        } else if ("Reject".equals(choice)) {
            String statusMessage = promptStatusMessage(
                    "Rejection Message",
                    "Enter the reason for rejection:",
                    request.getStatusMessage().startsWith("Rejected") ? request.getStatusMessage() : "",
                    true
            );
            if (statusMessage == null) {
                return;
            }
            leaveService.respondToLeave(request.getEmployeeNumber(), request.getStartDate(), request.getEndDate(), false, statusMessage);
            notificationService.record(
                    actorUser,
                    "LEAVE_REJECTED",
                    "Rejected leave for employee " + request.getEmployeeNumber() + " from " + request.getStartDate() + " to " + request.getEndDate() + "."
            );
        } else {
            return;
        }

        reloadRequests();
    }//GEN-LAST:event_btnRespondActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        reloadRequests();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtSearchLeaveRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchLeaveRequestActionPerformed
        btnSearchLeaveRequestActionPerformed(evt);
    }//GEN-LAST:event_txtSearchLeaveRequestActionPerformed

    private void btnSearchLeaveRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchLeaveRequestActionPerformed
        applyCurrentFilters();
    }//GEN-LAST:event_btnSearchLeaveRequestActionPerformed

    private String promptStatusMessage(String title,
                                       String prompt,
                                       String initialValue,
                                       boolean required) {

        String message = DialogHelper.promptText(
                this,
                title,
                prompt,
                initialValue,
                "Save Message"
        );
        if (message == null) {
            return null;
        }

        String trimmed = message.trim();
        if (required && trimmed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A reason is required for rejected leave requests.");
            return null;
        }

        return trimmed.isEmpty() ? initialValue.trim() : trimmed;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRespond;
    private javax.swing.JButton btnSearchLeaveRequest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblLeaveRequest;
    private javax.swing.JTextField txtSearchLeaveRequest;
    // End of variables declaration//GEN-END:variables
}

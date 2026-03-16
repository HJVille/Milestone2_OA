package com.mycompany.motorph.ui;

import com.mycompany.motorph.service.AttendanceService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class pnlAttendance extends javax.swing.JPanel {

    private static final String[] DEFAULT_SEARCH_FILTERS = {
            "Employee ID",
            "Employee Name"
    };

    private final AttendanceService attendanceService = new AttendanceService();
    private final List<String[]> attendanceRows = new ArrayList<>();
    private final boolean responsiveLayout;
    private final String[] searchFilterOptions;

    private JLabel lblAttendance;
    private JLabel filterLabel;
    private JLabel searchLabel;
    private JComboBox<String> cmbSearchFilter;
    private JTextField txtSearchAttendance;
    private JTable tblAttendance;
    private JScrollPane scrollAttendance;
    private JButton btnSearchAttendance;
    private JButton btnViewAttendance;
    private JButton btnRefreshAttendance;

    public pnlAttendance() {
        this(false, null);
    }

    public pnlAttendance(boolean responsiveLayout) {
        this(responsiveLayout, null);
    }

    public pnlAttendance(boolean responsiveLayout, String[] searchFilterOptions) {
        this.responsiveLayout = responsiveLayout;
        this.searchFilterOptions = searchFilterOptions == null || searchFilterOptions.length == 0
                ? DEFAULT_SEARCH_FILTERS.clone()
                : searchFilterOptions.clone();
        initComponents();
        reloadAttendance();
    }

    private void initComponents() {
        lblAttendance = new JLabel();
        filterLabel = new JLabel("Search By");
        searchLabel = new JLabel("Keyword");
        cmbSearchFilter = new JComboBox<>(searchFilterOptions);
        txtSearchAttendance = new JTextField(22);
        btnSearchAttendance = new JButton("Search");
        btnViewAttendance = new JButton("View");
        btnRefreshAttendance = new JButton("Refresh");
        tblAttendance = new JTable(createTableModel());
        scrollAttendance = new JScrollPane(tblAttendance);

        BrandTheme.styleSurface(this);
        setLayout(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        BrandTheme.setTitleWithLogo(lblAttendance, "Attendance Record");
        if (responsiveLayout) {
            lblAttendance.setIcon(null);
            lblAttendance.setText("Attendance Record");
        }

        styleLabel(filterLabel);
        styleLabel(searchLabel);
        BrandTheme.styleComboBox(cmbSearchFilter);
        BrandTheme.styleInputField(txtSearchAttendance);
        BrandTheme.styleSecondaryButton(btnSearchAttendance);
        BrandTheme.styleSecondaryButton(btnViewAttendance);
        BrandTheme.styleSecondaryButton(btnRefreshAttendance);
        BrandTheme.styleTable(tblAttendance);
        BrandTheme.styleScrollPane(scrollAttendance);

        cmbSearchFilter.setPreferredSize(new Dimension(170, 36));
        txtSearchAttendance.setPreferredSize(new Dimension(240, 36));
        btnSearchAttendance.setPreferredSize(new Dimension(108, 38));
        btnViewAttendance.setPreferredSize(new Dimension(108, 38));
        btnRefreshAttendance.setPreferredSize(new Dimension(108, 38));

        tblAttendance.setRowHeight(30);
        tblAttendance.setFillsViewportHeight(true);
        tblAttendance.setAutoCreateRowSorter(true);
        tblAttendance.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        configureTableColumns();

        txtSearchAttendance.addActionListener(this::txtSearchAttendanceActionPerformed);
        btnSearchAttendance.addActionListener(this::btnSearchAttendanceActionPerformed);
        btnViewAttendance.addActionListener(this::btnViewAttendanceActionPerformed);
        btnRefreshAttendance.addActionListener(this::btnRefreshAttendanceActionPerformed);

        add(buildHeaderCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Employee ID",
                        "Employee Name",
                        "Date",
                        "Time In",
                        "Time Out",
                        "Total Hours",
                        "Status"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel buildHeaderCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        BrandTheme.styleCardSurface(card);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        searchRow.add(filterLabel);
        searchRow.add(cmbSearchFilter);
        searchRow.add(searchLabel);
        searchRow.add(txtSearchAttendance);
        searchRow.add(btnSearchAttendance);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.add(btnViewAttendance);
        actionRow.add(btnRefreshAttendance);

        controls.add(searchRow);
        controls.add(javax.swing.Box.createVerticalStrut(10));
        controls.add(actionRow);

        card.add(lblAttendance, BorderLayout.NORTH);
        card.add(controls, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        BrandTheme.styleCardSurface(card);

        JLabel tableTitle = new JLabel("Attendance Log");
        tableTitle.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
        tableTitle.setForeground(BrandTheme.PRIMARY_BLUE);

        card.add(tableTitle, BorderLayout.NORTH);
        card.add(scrollAttendance, BorderLayout.CENTER);
        return card;
    }

    private void styleLabel(JLabel label) {
        label.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
        label.setForeground(BrandTheme.TEXT);
    }

    private void configureTableColumns() {
        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightAlignedRenderer = new DefaultTableCellRenderer();
        rightAlignedRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table,
                                                                    Object value,
                                                                    boolean isSelected,
                                                                    boolean hasFocus,
                                                                    int row,
                                                                    int column) {
                java.awt.Component component = super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    String status = value.toString();
                    component.setForeground("Under Time".equalsIgnoreCase(status)
                            ? BrandTheme.MOTORPH_RED
                            : BrandTheme.TEAL);
                    component.setBackground(row % 2 == 0 ? BrandTheme.INPUT_BG : BrandTheme.TABLE_ALT);
                }
                return component;
            }
        };

        tblAttendance.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblAttendance.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblAttendance.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAttendance.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblAttendance.getColumnModel().getColumn(4).setPreferredWidth(95);
        tblAttendance.getColumnModel().getColumn(5).setPreferredWidth(110);
        tblAttendance.getColumnModel().getColumn(6).setPreferredWidth(110);

        tblAttendance.getColumnModel().getColumn(0).setCellRenderer(centeredRenderer);
        tblAttendance.getColumnModel().getColumn(2).setCellRenderer(centeredRenderer);
        tblAttendance.getColumnModel().getColumn(3).setCellRenderer(centeredRenderer);
        tblAttendance.getColumnModel().getColumn(4).setCellRenderer(centeredRenderer);
        tblAttendance.getColumnModel().getColumn(5).setCellRenderer(rightAlignedRenderer);
        tblAttendance.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
    }

    public final void reloadAttendance() {
        attendanceRows.clear();
        attendanceRows.addAll(attendanceService.getAllAttendanceRows());
        applySearchFilter();
    }

    private void applySearchFilter() {
        String query = txtSearchAttendance.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(attendanceRows);
            return;
        }

        List<String[]> filtered = new ArrayList<>();
        String filterType = String.valueOf(cmbSearchFilter.getSelectedItem());
        for (String[] row : attendanceRows) {
            String employeeName = row[2] + " " + row[1];
            String date = row[3];
            String status = computeHours(row[4], row[5]) >= 8 ? "Complete" : "Under Time";

            String candidate;
            if ("Employee ID".equalsIgnoreCase(filterType)) {
                candidate = row[0];
            } else if ("Employee Name".equalsIgnoreCase(filterType)) {
                candidate = employeeName;
            } else if ("Date".equalsIgnoreCase(filterType)) {
                candidate = date;
            } else if ("Status".equalsIgnoreCase(filterType)) {
                candidate = status;
            } else {
                candidate = String.join(" ", row) + " " + status;
            }

            if (candidate.toLowerCase().contains(query)) {
                filtered.add(row);
            }
        }
        populateTable(filtered);
    }

    private void populateTable(List<String[]> rows) {
        DefaultTableModel model = (DefaultTableModel) tblAttendance.getModel();
        model.setRowCount(0);
        for (String[] row : rows) {
            double totalHours = computeHours(row[4], row[5]);
            String status = totalHours >= 8 ? "Complete" : "Under Time";
            model.addRow(new Object[]{
                    row[0],
                    row[2] + " " + row[1],
                    row[3],
                    row[4],
                    row[5],
                    String.format("%.2f", totalHours),
                    status
            });
        }
    }

    private double computeHours(String timeIn, String timeOut) {
        try {
            String[] in = timeIn.split(":");
            String[] out = timeOut.split(":");
            double start = Integer.parseInt(in[0]) + Integer.parseInt(in[1]) / 60.0;
            double end = Integer.parseInt(out[0]) + Integer.parseInt(out[1]) / 60.0;
            return Math.round(Math.max(0, end - start - 1.0) * 100.0) / 100.0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void txtSearchAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        btnSearchAttendanceActionPerformed(evt);
    }

    private void btnSearchAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        applySearchFilter();
    }

    private void btnViewAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblAttendance.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an attendance row first.");
            return;
        }

        int modelRow = tblAttendance.convertRowIndexToModel(selectedRow);
        JOptionPane.showMessageDialog(
                this,
                "Employee ID: " + tblAttendance.getModel().getValueAt(modelRow, 0)
                + "\nEmployee Name: " + tblAttendance.getModel().getValueAt(modelRow, 1)
                + "\nDate: " + tblAttendance.getModel().getValueAt(modelRow, 2)
                + "\nTime In: " + tblAttendance.getModel().getValueAt(modelRow, 3)
                + "\nTime Out: " + tblAttendance.getModel().getValueAt(modelRow, 4)
                + "\nTotal Hours: " + tblAttendance.getModel().getValueAt(modelRow, 5),
                "Attendance Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void btnRefreshAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        reloadAttendance();
    }
}

package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.CsvFilePaths;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class pnlAttendance extends javax.swing.JPanel {

    private final List<String[]> attendanceRows = new ArrayList<>();
    private final boolean responsiveLayout;
    private final String[] searchFilterOptions;
    private JComboBox<String> cmbSearchFilter;
    private JPanel attendanceHeaderPanel;
    private JLabel filterLabel;
    private JLabel searchLabel;
    private boolean headerResizeListenerAttached;

    public pnlAttendance() {
        this(false, null);
    }

    public pnlAttendance(boolean responsiveLayout) {
        this(responsiveLayout, null);
    }

    public pnlAttendance(boolean responsiveLayout, String[] searchFilterOptions) {
        this.responsiveLayout = responsiveLayout;
        this.searchFilterOptions = searchFilterOptions == null || searchFilterOptions.length == 0
                ? null
                : searchFilterOptions.clone();
        initComponents();
        BrandTheme.styleSurface(this);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        BrandTheme.setTitleWithLogo(lblAttendance, "Attendance Record");
        if (responsiveLayout) {
            lblAttendance.setIcon(null);
            lblAttendance.setText("Attendance Record");
        }
        BrandTheme.styleTable(tblAttendance);
        BrandTheme.styleInputField(txtSearchAttendance);
        BrandTheme.styleScrollPane(scrollAttendance);
        BrandTheme.styleSecondaryButton(btnSearchAttendance);
        BrandTheme.styleSecondaryButton(btnViewAttendance);
        BrandTheme.styleSecondaryButton(btnRefreshAttendance);
        BrandTheme.styleSecondaryButton(btnImportAttendance);
        if (responsiveLayout) {
            rebuildLayout();
        }
        reloadAttendance();
        btnImportAttendance.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblAttendance = new javax.swing.JLabel();
        txtSearchAttendance = new javax.swing.JTextField();
        scrollAttendance = new javax.swing.JScrollPane();
        tblAttendance = new javax.swing.JTable();
        btnSearchAttendance = new javax.swing.JButton();
        btnImportAttendance = new javax.swing.JButton();
        btnViewAttendance = new javax.swing.JButton();
        btnRefreshAttendance = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        lblAttendance.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblAttendance.setForeground(new java.awt.Color(0, 51, 102));
        lblAttendance.setText("Attendance Record");

        txtSearchAttendance.addActionListener(this::txtSearchAttendanceActionPerformed);

        tblAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Employee Name", "Date", "Time In", "Time Out", "Total Hours", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollAttendance.setViewportView(tblAttendance);

        btnSearchAttendance.setText("Search");
        btnSearchAttendance.addActionListener(this::btnSearchAttendanceActionPerformed);

        btnImportAttendance.setText("Import");

        btnViewAttendance.setText("View");
        btnViewAttendance.addActionListener(this::btnViewAttendanceActionPerformed);

        btnRefreshAttendance.setText("Refresh");
        btnRefreshAttendance.addActionListener(this::btnRefreshAttendanceActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnImportAttendance)
                        .addGap(18, 18, 18)
                        .addComponent(btnViewAttendance)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefreshAttendance))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblAttendance)
                            .addGap(597, 597, 597))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtSearchAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSearchAttendance)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollAttendance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(lblAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchAttendance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImportAttendance)
                    .addComponent(btnViewAttendance)
                    .addComponent(btnRefreshAttendance))
                .addContainerGap(142, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public final void reloadAttendance() {
        attendanceRows.clear();
        try (BufferedReader br = Files.newBufferedReader(CsvFilePaths.ATTENDANCE)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    attendanceRows.add(data);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance: " + e.getMessage());
        }
        populateTable(attendanceRows);
    }

    private void rebuildLayout() {
        if (cmbSearchFilter == null && searchFilterOptions != null) {
            cmbSearchFilter = new JComboBox<>(searchFilterOptions);
            BrandTheme.styleComboBox(cmbSearchFilter);
            cmbSearchFilter.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            cmbSearchFilter.setPreferredSize(new Dimension(160, 34));
        }
        if (filterLabel == null) {
            filterLabel = new JLabel("Filter By:");
            filterLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            filterLabel.setForeground(BrandTheme.TEXT);
        }
        if (searchLabel == null) {
            searchLabel = new JLabel("Search");
            searchLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            searchLabel.setForeground(BrandTheme.TEXT);
        }

        txtSearchAttendance.setPreferredSize(new Dimension(190, 34));
        tblAttendance.setFillsViewportHeight(true);
        tblAttendance.setAutoCreateRowSorter(true);
        tblAttendance.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        configureResponsiveColumns();

        removeAll();
        setLayout(new BorderLayout(0, 16));

        attendanceHeaderPanel = new JPanel(new BorderLayout(0, 10));
        attendanceHeaderPanel.setOpaque(false);
        updateHeaderLayout();

        add(attendanceHeaderPanel, BorderLayout.NORTH);
        add(scrollAttendance, BorderLayout.CENTER);

        if (!headerResizeListenerAttached) {
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateHeaderLayout();
                }
            });
            headerResizeListenerAttached = true;
        }

        revalidate();
        repaint();
    }

    private void updateHeaderLayout() {
        if (attendanceHeaderPanel == null) {
            return;
        }

        attendanceHeaderPanel.removeAll();
        attendanceHeaderPanel.add(lblAttendance, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        if (cmbSearchFilter != null) {
            if (getWidth() <= 860) {
                txtSearchAttendance.setPreferredSize(new Dimension(160, 34));
                cmbSearchFilter.setPreferredSize(new Dimension(150, 34));
            } else {
                txtSearchAttendance.setPreferredSize(new Dimension(190, 34));
                cmbSearchFilter.setPreferredSize(new Dimension(160, 34));
            }
            searchRow.add(filterLabel);
            searchRow.add(cmbSearchFilter);
        }

        searchRow.add(searchLabel);
        searchRow.add(txtSearchAttendance);
        searchRow.add(btnSearchAttendance);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        if (cmbSearchFilter == null) {
            actionRow.add(btnImportAttendance);
        }
        actionRow.add(btnViewAttendance);
        actionRow.add(btnRefreshAttendance);

        JPanel controls = new JPanel(new BorderLayout(0, 8));
        controls.setOpaque(false);
        controls.add(searchRow, BorderLayout.NORTH);
        controls.add(actionRow, BorderLayout.CENTER);

        attendanceHeaderPanel.add(controls, BorderLayout.CENTER);
        attendanceHeaderPanel.revalidate();
        attendanceHeaderPanel.repaint();
    }

    private void configureResponsiveColumns() {
        if (tblAttendance.getColumnModel().getColumnCount() < 7) {
            return;
        }

        tblAttendance.getTableHeader().setResizingAllowed(true);
        tblAttendance.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblAttendance.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblAttendance.getColumnModel().getColumn(2).setPreferredWidth(125);
        tblAttendance.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblAttendance.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblAttendance.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblAttendance.getColumnModel().getColumn(6).setPreferredWidth(110);
    }

    private void populateTable(List<String[]> rows) {
        DefaultTableModel model = (DefaultTableModel) tblAttendance.getModel();
        model.setRowCount(0);
        for (String[] row : rows) {
            double totalHours = computeHours(row[4], row[5]);
            String status = totalHours >= 8 ? "Complete" : "Under Time";
            model.addRow(new Object[]{row[0], row[2] + " " + row[1], row[3], row[4], row[5], totalHours, status});
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

    private void txtSearchAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchAttendanceActionPerformed
        btnSearchAttendanceActionPerformed(evt);
    }//GEN-LAST:event_txtSearchAttendanceActionPerformed

    private void btnSearchAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchAttendanceActionPerformed
        String query = txtSearchAttendance.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(attendanceRows);
            return;
        }
        List<String[]> filtered = new ArrayList<>();
        String filterType = cmbSearchFilter == null
                ? ""
                : String.valueOf(cmbSearchFilter.getSelectedItem());
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
    }//GEN-LAST:event_btnSearchAttendanceActionPerformed

    private void btnViewAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblAttendance.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an attendance row first.");
            return;
        }
        JOptionPane.showMessageDialog(
                this,
                "Employee ID: " + tblAttendance.getValueAt(row, 0)
                + "\nEmployee Name: " + tblAttendance.getValueAt(row, 1)
                + "\nDate: " + tblAttendance.getValueAt(row, 2)
                + "\nTime In: " + tblAttendance.getValueAt(row, 3)
                + "\nTime Out: " + tblAttendance.getValueAt(row, 4)
                + "\nTotal Hours: " + tblAttendance.getValueAt(row, 5),
                "Attendance Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void btnRefreshAttendanceActionPerformed(java.awt.event.ActionEvent evt) {
        reloadAttendance();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImportAttendance;
    private javax.swing.JButton btnRefreshAttendance;
    private javax.swing.JButton btnSearchAttendance;
    private javax.swing.JButton btnViewAttendance;
    private javax.swing.JLabel lblAttendance;
    private javax.swing.JScrollPane scrollAttendance;
    private javax.swing.JTable tblAttendance;
    private javax.swing.JTextField txtSearchAttendance;
    // End of variables declaration//GEN-END:variables
}

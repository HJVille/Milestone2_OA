package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.service.AccessControlService;
import com.mycompany.motorph.service.LeaveService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class pnlLeaveRequest extends javax.swing.JPanel {

    private final LeaveService leaveService = new LeaveService();
    private final NotificationService notificationService = new NotificationService();
    private final AccessControlService accessControlService = new AccessControlService();
    private final User actorUser;
    private final boolean responsiveLayout;
    private final String[] searchFilterOptions;
    private List<LeaveRequest> requests = new ArrayList<>();
    private JComboBox<String> cmbSearchFilter;
    private JPanel leaveHeaderPanel;
    private JLabel filterLabel;
    private JLabel searchLabel;
    private boolean headerResizeListenerAttached;

    public pnlLeaveRequest() {
        this(null);
    }

    public pnlLeaveRequest(User actorUser) {
        this(actorUser, false);
    }

    public pnlLeaveRequest(User actorUser, boolean responsiveLayout) {
        this(actorUser, responsiveLayout, null);
    }

    public pnlLeaveRequest(User actorUser, boolean responsiveLayout, String[] searchFilterOptions) {
        this.actorUser = actorUser;
        this.responsiveLayout = responsiveLayout;
        this.searchFilterOptions = searchFilterOptions == null || searchFilterOptions.length == 0
                ? null
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
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

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
        populateTable(requests);
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

        txtSearchLeaveRequest.setPreferredSize(new Dimension(190, 34));
        jTable1.setFillsViewportHeight(true);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        configureResponsiveColumns();

        removeAll();
        setLayout(new BorderLayout(0, 16));

        leaveHeaderPanel = new JPanel(new BorderLayout(0, 10));
        leaveHeaderPanel.setOpaque(false);
        updateHeaderLayout();

        add(leaveHeaderPanel, BorderLayout.NORTH);
        add(jScrollPane1, BorderLayout.CENTER);

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
        if (leaveHeaderPanel == null) {
            return;
        }

        leaveHeaderPanel.removeAll();
        leaveHeaderPanel.add(lblLeaveRequest, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        if (cmbSearchFilter != null) {
            if (getWidth() <= 860) {
                txtSearchLeaveRequest.setPreferredSize(new Dimension(160, 34));
                cmbSearchFilter.setPreferredSize(new Dimension(150, 34));
            } else {
                txtSearchLeaveRequest.setPreferredSize(new Dimension(190, 34));
                cmbSearchFilter.setPreferredSize(new Dimension(160, 34));
            }
            searchRow.add(filterLabel);
            searchRow.add(cmbSearchFilter);
        }

        searchRow.add(searchLabel);
        searchRow.add(txtSearchLeaveRequest);
        searchRow.add(btnSearchLeaveRequest);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.add(btnRespond);
        actionRow.add(btnRefresh);

        JPanel controls = new JPanel(new BorderLayout(0, 8));
        controls.setOpaque(false);
        controls.add(searchRow, BorderLayout.NORTH);
        controls.add(actionRow, BorderLayout.CENTER);

        leaveHeaderPanel.add(controls, BorderLayout.CENTER);
        leaveHeaderPanel.revalidate();
        leaveHeaderPanel.repaint();
    }

    private void configureResponsiveColumns() {
        if (jTable1.getColumnModel().getColumnCount() < 8) {
            return;
        }

        jTable1.getTableHeader().setResizingAllowed(true);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(110);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(220);
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
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int employeeNumber = Integer.parseInt(jTable1.getValueAt(row, 0).toString());
        String startDate = jTable1.getValueAt(row, 3).toString();
        for (LeaveRequest request : requests) {
            if (request.getEmployeeNumber() == employeeNumber && request.getStartDate().equals(startDate)) {
                return request;
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
            leaveService.respondToLeave(request.getEmployeeNumber(), request.getStartDate(), true, statusMessage);
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
            leaveService.respondToLeave(request.getEmployeeNumber(), request.getStartDate(), false, statusMessage);
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
        String query = txtSearchLeaveRequest.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(requests);
            return;
        }
        List<LeaveRequest> filtered = new ArrayList<>();
        String filterType = cmbSearchFilter == null
                ? ""
                : String.valueOf(cmbSearchFilter.getSelectedItem());
        for (LeaveRequest request : requests) {
            String candidate;
            if ("Employee ID".equalsIgnoreCase(filterType)) {
                candidate = String.valueOf(request.getEmployeeNumber());
            } else if ("Employee Name".equalsIgnoreCase(filterType)) {
                candidate = request.getEmployeeName();
            } else if ("Leave Type".equalsIgnoreCase(filterType)) {
                candidate = request.getLeaveType();
            } else if ("Status".equalsIgnoreCase(filterType)) {
                candidate = request.getStatus();
            } else {
                candidate = request.getEmployeeNumber() + " "
                        + request.getEmployeeName() + " "
                        + request.getLeaveType() + " "
                        + request.getStatus() + " "
                        + request.getStatusMessage();
            }
            if (candidate.toLowerCase().contains(query)) {
                filtered.add(request);
            }
        }
        populateTable(filtered);
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

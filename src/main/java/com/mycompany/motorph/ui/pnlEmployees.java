package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.CsvFilePaths;
import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AccessControlService;
import com.mycompany.motorph.service.EmployeeValidationService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class pnlEmployees extends javax.swing.JPanel {

    private static final String[] DEFAULT_SEARCH_FILTERS = {
            "Employee Name", "Employee Number"
    };

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final EmployeeValidationService validationService = new EmployeeValidationService();
    private final NotificationService notificationService = new NotificationService();
    private final AccessControlService accessControlService = new AccessControlService();
    private final User actorUser;
    private final boolean responsiveLayout;
    private final String[] searchFilterOptions;
    private List<Employee> employees = new ArrayList<>();
    private JComboBox<String> cmbSearchFilter;
    private JPanel employeeHeaderPanel;
    private JLabel filterLabel;
    private JLabel searchLabel;
    private boolean headerResizeListenerAttached;

    public pnlEmployees() {
        this(null);
    }

    public pnlEmployees(User actorUser) {
        this(actorUser, false);
    }

    public pnlEmployees(User actorUser, boolean responsiveLayout) {
        this(actorUser, responsiveLayout, DEFAULT_SEARCH_FILTERS);
    }

    public pnlEmployees(User actorUser, boolean responsiveLayout, String[] searchFilterOptions) {
        this.actorUser = actorUser;
        this.responsiveLayout = responsiveLayout;
        this.searchFilterOptions = searchFilterOptions == null || searchFilterOptions.length == 0
                ? DEFAULT_SEARCH_FILTERS
                : searchFilterOptions.clone();
        initComponents();
        BrandTheme.styleSurface(this);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        BrandTheme.setTitleWithLogo(lblEmployees, "Employees");
        if (responsiveLayout) {
            lblEmployees.setIcon(null);
            lblEmployees.setText("Employees");
        }
        BrandTheme.styleTable(tblEmployee);
        BrandTheme.styleInputField(txtSearchEmployee);
        BrandTheme.styleScrollPane(scrollEmployee);
        BrandTheme.styleSecondaryButton(btnSearchEmployee);
        BrandTheme.styleSecondaryButton(btnGovtIDInfo);
        BrandTheme.stylePrimaryButton(btnAddEmployee);
        BrandTheme.styleSecondaryButton(btnUpdateEmployee);
        BrandTheme.styleDangerButton(btnDeleteEmployee);
        applyRolePermissions();
        if (responsiveLayout) {
            rebuildLayout();
        }
        reloadEmployees();
    }

    private void applyRolePermissions() {
        if (actorUser == null) {
            return;
        }
        btnAddEmployee.setEnabled(accessControlService.canAccess(actorUser, "Add employee"));
        btnUpdateEmployee.setEnabled(accessControlService.canAccess(actorUser, "Edit employee"));
        btnDeleteEmployee.setEnabled(accessControlService.canAccess(actorUser, "Delete employee"));
        btnGovtIDInfo.setEnabled(accessControlService.canAccess(actorUser, "View government IDs"));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblEmployee = new javax.swing.JLabel();
        scrollEmployee = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        btnAddEmployee = new javax.swing.JButton();
        btnUpdateEmployee = new javax.swing.JButton();
        btnDeleteEmployee = new javax.swing.JButton();
        txtSearchEmployee = new javax.swing.JTextField();
        btnSearchEmployee = new javax.swing.JButton();
        btnGovtIDInfo = new javax.swing.JButton();
        lblEmployees = new javax.swing.JLabel();

        lblEmployee.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblEmployee.setForeground(new java.awt.Color(0, 51, 102));
        lblEmployee.setText("Employee");

        setBackground(new java.awt.Color(255, 255, 255));

        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee Number", "Last Name", "First Name", "Position", "Status", "Basic Salary", "Hourly Rate", "Phone Number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollEmployee.setViewportView(tblEmployee);

        btnAddEmployee.setText("Add Employee");
        btnAddEmployee.addActionListener(this::btnAddEmployeeActionPerformed);

        btnUpdateEmployee.setText("Update Employee");
        btnUpdateEmployee.addActionListener(this::btnUpdateEmployeeActionPerformed);

        btnDeleteEmployee.setText("Delete Employee");
        btnDeleteEmployee.addActionListener(this::btnDeleteEmployeeActionPerformed);

        txtSearchEmployee.addActionListener(this::txtSearchEmployeeActionPerformed);

        btnSearchEmployee.setText("Search");
        btnSearchEmployee.addActionListener(this::btnSearchEmployeeActionPerformed);

        btnGovtIDInfo.setText("Government ID Information");
        btnGovtIDInfo.addActionListener(this::btnGovtIDInfoActionPerformed);

        lblEmployees.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblEmployees.setForeground(new java.awt.Color(0, 51, 102));
        lblEmployees.setText("Employees");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmployees)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddEmployee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdateEmployee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteEmployee))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtSearchEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnSearchEmployee)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGovtIDInfo))
                        .addComponent(scrollEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(272, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addComponent(lblEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchEmployee)
                    .addComponent(btnGovtIDInfo))
                .addGap(33, 33, 33)
                .addComponent(scrollEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddEmployee)
                    .addComponent(btnUpdateEmployee)
                    .addComponent(btnDeleteEmployee))
                .addGap(163, 163, 163))
        );
    }// </editor-fold>//GEN-END:initComponents

    public final void reloadEmployees() {
        employees = employeeDAO.loadEmployees(CsvFilePaths.EMPLOYEES.toString());
        populateTable(employees);
    }

    private void rebuildLayout() {
        if (cmbSearchFilter == null) {
            cmbSearchFilter = new JComboBox<>(searchFilterOptions);
        }
        if (filterLabel == null) {
            filterLabel = new JLabel("Filter By:");
        }
        if (searchLabel == null) {
            searchLabel = new JLabel("Search");
        }

        BrandTheme.styleComboBox(cmbSearchFilter);
        cmbSearchFilter.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        cmbSearchFilter.setPreferredSize(new Dimension(160, 34));
        txtSearchEmployee.setPreferredSize(new Dimension(210, 34));
        tblEmployee.setFillsViewportHeight(true);
        tblEmployee.setAutoCreateRowSorter(true);
        tblEmployee.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        configureResponsiveColumns();

        removeAll();
        setLayout(new BorderLayout(0, 16));

        employeeHeaderPanel = new JPanel(new BorderLayout(0, 10));
        employeeHeaderPanel.setOpaque(false);

        filterLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        filterLabel.setForeground(BrandTheme.TEXT);

        searchLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        searchLabel.setForeground(BrandTheme.TEXT);
        updateHeaderLayout();

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        footer.setOpaque(false);
        footer.add(btnAddEmployee);
        footer.add(btnUpdateEmployee);
        footer.add(btnDeleteEmployee);

        add(employeeHeaderPanel, BorderLayout.NORTH);
        add(scrollEmployee, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        if (!headerResizeListenerAttached) {
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (responsiveLayout) {
                        updateHeaderLayout();
                    }
                }
            });
            headerResizeListenerAttached = true;
        }

        revalidate();
        repaint();
    }

    private void updateHeaderLayout() {
        if (employeeHeaderPanel == null) {
            return;
        }

        employeeHeaderPanel.removeAll();
        employeeHeaderPanel.add(lblEmployees, BorderLayout.NORTH);
        if (getWidth() <= 840) {
            txtSearchEmployee.setPreferredSize(new Dimension(150, 34));
            cmbSearchFilter.setPreferredSize(new Dimension(150, 34));

            JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            searchRow.setOpaque(false);
            searchRow.add(filterLabel);
            searchRow.add(cmbSearchFilter);
            searchRow.add(searchLabel);
            searchRow.add(txtSearchEmployee);
            searchRow.add(btnSearchEmployee);

            JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            actionRow.setOpaque(false);
            actionRow.add(btnGovtIDInfo);

            JPanel controls = new JPanel(new BorderLayout(0, 8));
            controls.setOpaque(false);
            controls.add(searchRow, BorderLayout.NORTH);
            controls.add(actionRow, BorderLayout.CENTER);

            employeeHeaderPanel.add(controls, BorderLayout.CENTER);
        } else {
            txtSearchEmployee.setPreferredSize(new Dimension(210, 34));
            cmbSearchFilter.setPreferredSize(new Dimension(160, 34));

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            actions.setOpaque(false);
            actions.add(filterLabel);
            actions.add(cmbSearchFilter);
            actions.add(searchLabel);
            actions.add(txtSearchEmployee);
            actions.add(btnSearchEmployee);
            actions.add(btnGovtIDInfo);
            employeeHeaderPanel.add(actions, BorderLayout.CENTER);
        }

        employeeHeaderPanel.revalidate();
        employeeHeaderPanel.repaint();
    }

    private void configureResponsiveColumns() {
        if (tblEmployee.getColumnModel().getColumnCount() < 8) {
            return;
        }

        tblEmployee.getTableHeader().setResizingAllowed(true);
        tblEmployee.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblEmployee.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblEmployee.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblEmployee.getColumnModel().getColumn(3).setPreferredWidth(240);
        tblEmployee.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblEmployee.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblEmployee.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblEmployee.getColumnModel().getColumn(7).setPreferredWidth(140);
    }

    private void populateTable(List<Employee> employeeList) {
        DefaultTableModel model = (DefaultTableModel) tblEmployee.getModel();
        model.setRowCount(0);
        for (Employee employee : employeeList) {
            model.addRow(new Object[]{
                employee.getEmployeeNumber(),
                employee.getLastName(),
                employee.getFirstName(),
                employee.getPosition(),
                employee.getStatus(),
                employee.getBasicSalary(),
                employee.getHourlyRate(),
                employee.getPhone()
            });
        }
    }

    private Employee getSelectedEmployee() {
        int row = tblEmployee.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int employeeNumber = Integer.parseInt(tblEmployee.getValueAt(row, 0).toString());
        for (Employee employee : employees) {
            if (employee.getEmployeeNumber() == employeeNumber) {
                return employee;
            }
        }
        return null;
    }

    private void txtSearchEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchEmployeeActionPerformed
        btnSearchEmployeeActionPerformed(evt);
    }//GEN-LAST:event_txtSearchEmployeeActionPerformed

    private void btnSearchEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchEmployeeActionPerformed
        String query = txtSearchEmployee.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(employees);
            return;
        }

        List<Employee> filtered = new ArrayList<>();
        String filterType = cmbSearchFilter == null
                ? DEFAULT_SEARCH_FILTERS[0]
                : String.valueOf(cmbSearchFilter.getSelectedItem());
        for (Employee employee : employees) {
            String haystack;
            if ("Employee Number".equalsIgnoreCase(filterType)) {
                haystack = String.valueOf(employee.getEmployeeNumber());
            } else if ("Last Name".equalsIgnoreCase(filterType)) {
                haystack = employee.getLastName();
            } else if ("First Name".equalsIgnoreCase(filterType)) {
                haystack = employee.getFirstName();
            } else if ("Position".equalsIgnoreCase(filterType)) {
                haystack = employee.getPosition();
            } else {
                haystack = employee.getEmployeeName() + " " + employee.getLastName() + ", " + employee.getFirstName();
            }
            haystack = haystack == null ? "" : haystack.toLowerCase();
            if (haystack.contains(query)) {
                filtered.add(employee);
            }
        }
        populateTable(filtered);
    }//GEN-LAST:event_btnSearchEmployeeActionPerformed

    private void btnAddEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEmployeeActionPerformed
        showEmployeeEditor(null);
    }//GEN-LAST:event_btnAddEmployeeActionPerformed

    private void btnUpdateEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateEmployeeActionPerformed
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        showEmployeeEditor(employee);
    }//GEN-LAST:event_btnUpdateEmployeeActionPerformed

    private void btnDeleteEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteEmployeeActionPerformed
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        boolean confirmed = DialogHelper.showConfirmMessage(
                this,
                "Confirm Delete",
                "Delete employee " + employee.getEmployeeName() + "?",
                "Delete",
                "Cancel"
        );

        if (confirmed) {
            employeeDAO.deleteEmployee(
                    employee.getEmployeeNumber(),
                    new ArrayList<>(employees),
                    CsvFilePaths.EMPLOYEES.toString()
            );
            notificationService.record(
                    actorUser,
                    "EMPLOYEE_DELETED",
                    "Deleted employee " + employee.getEmployeeNumber() + " - " + employee.getEmployeeName() + "."
            );
            reloadEmployees();
        }
    }//GEN-LAST:event_btnDeleteEmployeeActionPerformed

    private void btnGovtIDInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGovtIDInfoActionPerformed
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "SSS: " + employee.getSss()
                + "\nPhilHealth: " + employee.getPhilhealth()
                + "\nTIN: " + employee.getTin()
                + "\nPag-IBIG: " + employee.getPagibig(),
                "Government ID Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_btnGovtIDInfoActionPerformed

    private void showEmployeeEditor(Employee employee) {

        EmployeeEditorPanel editorPanel = new EmployeeEditorPanel(employee);
        String title = employee == null ? "Add Employee" : "Update Employee";

        boolean confirmed = responsiveLayout
                ? showResponsiveEmployeeEditorDialog(
                        employee,
                        editorPanel,
                        title,
                        employee == null ? "Add Employee" : "Save Changes"
                )
                : DialogHelper.showFormDialog(
                        this,
                        title,
                        editorPanel,
                        employee == null ? "Add Employee" : "Save Changes",
                        "Cancel"
                );

        if (!confirmed) {
            return;
        }

        try {
            Employee savedEmployee = employee == null
                    ? validationService.createEmployee(editorPanel.getFormData(), employees)
                    : validationService.updateEmployee(
                            editorPanel.getFormData(),
                            employees,
                            employee.getEmployeeNumber()
                    );

            if (employee == null) {
                employeeDAO.addEmployee(savedEmployee, CsvFilePaths.EMPLOYEES.toString());
                notificationService.record(
                        actorUser,
                        "EMPLOYEE_ADDED",
                        "Added employee " + savedEmployee.getEmployeeNumber() + " - " + savedEmployee.getEmployeeName() + "."
                );
            } else {
                employeeDAO.updateEmployee(
                        employee.getEmployeeNumber(),
                        savedEmployee,
                        new ArrayList<>(employees),
                        CsvFilePaths.EMPLOYEES.toString()
                );
                notificationService.record(
                        actorUser,
                        "EMPLOYEE_UPDATED",
                        "Updated employee " + savedEmployee.getEmployeeNumber() + " - " + savedEmployee.getEmployeeName() + "."
                );
            }

            reloadEmployees();
            JOptionPane.showMessageDialog(this, title + " successful.");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean showResponsiveEmployeeEditorDialog(Employee employee,
                                                       EmployeeEditorPanel editorPanel,
                                                       String title,
                                                       String primaryLabel) {

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);

        JPanel shell = new JPanel(new BorderLayout(0, 16));
        BrandTheme.styleSurface(shell);
        shell.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");
        javax.swing.JButton primaryButton = new javax.swing.JButton(primaryLabel);
        BrandTheme.styleSecondaryButton(cancelButton);
        BrandTheme.stylePrimaryButton(primaryButton);

        final boolean[] confirmed = {false};
        cancelButton.addActionListener(evt -> dialog.dispose());
        primaryButton.addActionListener(evt -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(primaryButton);

        shell.add(editorPanel, BorderLayout.CENTER);
        shell.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(shell);
        dialog.getRootPane().setDefaultButton(primaryButton);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(760, 740));
        dialog.setSize(Math.max(dialog.getWidth(), 820), Math.max(dialog.getHeight(), 760));
        dialog.setLocationRelativeTo(owner == null ? this : owner);
        dialog.setVisible(true);
        return confirmed[0];
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEmployee;
    private javax.swing.JButton btnDeleteEmployee;
    private javax.swing.JButton btnGovtIDInfo;
    private javax.swing.JButton btnSearchEmployee;
    private javax.swing.JButton btnUpdateEmployee;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblEmployees;
    private javax.swing.JScrollPane scrollEmployee;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTextField txtSearchEmployee;
    // End of variables declaration//GEN-END:variables
}

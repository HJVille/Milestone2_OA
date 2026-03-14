package com.mycompany.motorph.ui;

import javax.swing.table.DefaultTableModel;

public class EmployeeViewPayslip extends javax.swing.JPanel {

    public EmployeeViewPayslip(String[] payslipRow) {
        initComponents();
        BrandTheme.styleSurface(this);
        BrandTheme.setTitleWithLogo(jLabelTitle, "Payslip Statement");
        BrandTheme.styleTable(tblPayslip);
        BrandTheme.styleScrollPane(jScrollPane1);
        jSeparator1.setForeground(BrandTheme.GOLD);
        jSeparator1.setBackground(BrandTheme.GOLD);
        loadPayslip(payslipRow);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayslip = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 0, 51));

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setText("View Payslip");

        tblPayslip.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Item", "Value"}
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        jScrollPane1.setViewportView(tblPayslip);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitle)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addGap(22, 22, 22))
        );
    }

    private void loadPayslip(String[] row) {
        DefaultTableModel model = (DefaultTableModel) tblPayslip.getModel();
        model.setRowCount(0);
        if (row == null || row.length < 14) {
            model.addRow(new Object[]{"Status", "No payslip record selected"});
            return;
        }

        model.addRow(new Object[]{"Employee Number", row[0]});
        model.addRow(new Object[]{"Employee Name", row[1]});
        model.addRow(new Object[]{"Period Start", row[2]});
        model.addRow(new Object[]{"Period End", row[3]});
        model.addRow(new Object[]{"Basic Salary", row[4]});
        model.addRow(new Object[]{"Rice Subsidy", row[5]});
        model.addRow(new Object[]{"Phone Allowance", row[6]});
        model.addRow(new Object[]{"Clothing Allowance", row[7]});
        model.addRow(new Object[]{"Gross Salary", row[8]});
        model.addRow(new Object[]{"SSS", row[9]});
        model.addRow(new Object[]{"PhilHealth", row[10]});
        model.addRow(new Object[]{"Pag-IBIG", row[11]});
        model.addRow(new Object[]{"Tax", row[12]});
        model.addRow(new Object[]{"Net Salary", row[13]});
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblPayslip;
    // End of variables declaration
}

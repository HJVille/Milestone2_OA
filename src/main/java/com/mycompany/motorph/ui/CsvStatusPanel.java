package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.CsvFilePaths;
import java.awt.BorderLayout;
import java.awt.Font;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CsvStatusPanel extends JPanel {

    private final JTable table = new JTable();

    public CsvStatusPanel() {
        setLayout(new BorderLayout(0, 14));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("CSV and Audit Paths");
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(BrandTheme.TEXT);

        JButton refreshButton = new JButton("Refresh");
        BrandTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(evt -> reloadPaths());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
            "File", "Resolved Path", "Exists"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        BrandTheme.styleTable(table);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setResizingAllowed(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(760);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        add(header, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        BrandTheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        reloadPaths();
    }

    public final void reloadPaths() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        addRow(model, "Users", CsvFilePaths.USERS);
        addRow(model, "Employees", CsvFilePaths.EMPLOYEES);
        addRow(model, "Attendance", CsvFilePaths.ATTENDANCE);
        addRow(model, "Leave Requests", CsvFilePaths.LEAVE_REQUESTS);
        addRow(model, "Payroll Records", CsvFilePaths.PAYROLL_RECORDS);
        addRow(model, "Password Audit", Path.of("password_audit.csv"));
    }

    private void addRow(DefaultTableModel model, String label, Path path) {
        model.addRow(new Object[]{label, path.toAbsolutePath().toString(), Files.exists(path) ? "Yes" : "No"});
    }
}

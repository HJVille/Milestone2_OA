package com.mycompany.motorph.ui;

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

public class AuditLogPanel extends JPanel {

    private final JTable table = new JTable();

    public AuditLogPanel() {
        setLayout(new BorderLayout(0, 14));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Password Audit Log");
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(BrandTheme.TEXT);

        JButton refreshButton = new JButton("Refresh");
        BrandTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(evt -> reloadAuditLog());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
            "Timestamp", "Username", "Action"
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
        table.getColumnModel().getColumn(0).setPreferredWidth(320);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(240);

        add(header, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        BrandTheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        reloadAuditLog();
    }

    public final void reloadAuditLog() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        Path auditFile = Path.of("password_audit.csv");
        if (!Files.exists(auditFile)) {
            return;
        }

        try {
            boolean firstLine = true;
            for (String line : Files.readAllLines(auditFile)) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length >= 3) {
                    model.addRow(new Object[]{data[0], data[1], data[2]});
                }
            }
        } catch (Exception e) {
            model.addRow(new Object[]{"Status", "Unable to read audit log", e.getMessage()});
        }
    }
}

package com.mycompany.motorph.ui;

import com.mycompany.motorph.service.SystemToolsService;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AuditLogPanel extends JPanel {

    private final SystemToolsService systemToolsService = new SystemToolsService();
    private final JTable table = new JTable();

    public AuditLogPanel() {
        setLayout(new BorderLayout(0, 18));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Password Audit Log");
        title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(BrandTheme.PRIMARY_BLUE);

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

        JScrollPane scrollPane = new JScrollPane(table);
        BrandTheme.styleScrollPane(scrollPane);
        add(wrapInCard(header), BorderLayout.NORTH);
        add(wrapInCard(scrollPane, "Audit Entries"), BorderLayout.CENTER);

        reloadAuditLog();
    }

    private JPanel wrapInCard(java.awt.Component content) {
        JPanel card = new JPanel(new BorderLayout());
        BrandTheme.styleCardSurface(card);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel wrapInCard(java.awt.Component content, String sectionTitle) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        BrandTheme.styleCardSurface(card);

        JLabel label = new JLabel(sectionTitle);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 16f));
        label.setForeground(BrandTheme.PRIMARY_BLUE);

        card.add(label, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public final void reloadAuditLog() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String[] row : systemToolsService.getPasswordAuditRows()) {
            if (row.length >= 3) {
                model.addRow(new Object[]{row[0], row[1], row[2]});
            }
        }
    }
}

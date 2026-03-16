package com.mycompany.motorph.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;

public final class BrandTheme {

    public static final Color PRIMARY_BLUE = new Color(0x7B, 0x94, 0xB8);
    public static final Color MOTORPH_RED = new Color(0xE1, 0x1D, 0x2E);
    public static final Color GOLD = new Color(0xD7, 0xC5, 0x9A);
    public static final Color BACKDROP = new Color(0xEB, 0xF1, 0xF8);
    public static final Color PANEL_BG = new Color(0xFF, 0xFF, 0xFF);
    public static final Color BORDER = new Color(0xD8, 0xE2, 0xEE);
    public static final Color TEXT = new Color(0x24, 0x35, 0x4F);
    public static final Color TEXT_DARK = new Color(0x1F, 0x2D, 0x44);
    public static final Color MUTED = new Color(0x6B, 0x7E, 0x97);
    public static final Color MUTED_DARK = new Color(0x5C, 0x6F, 0x89);
    public static final Color TEXT_INVERSE = new Color(0xF7, 0xFA, 0xFE);
    public static final Color MUTED_INVERSE = new Color(0xC7, 0xD5, 0xE8);
    public static final Color INPUT_BG = new Color(0xF7, 0xFA, 0xFE);
    public static final Color ACCENT_BLUE = new Color(0xE3, 0xEC, 0xF8);
    public static final Color TEAL = new Color(0x84, 0xA6, 0xBE);
    public static final Color PURPLE = new Color(0x8F, 0x98, 0xB6);
    public static final Color TABLE_ALT = new Color(0xF6, 0xF9, 0xFD);
    public static final Color TABLE_HEADER = new Color(0xEC, 0xF2, 0xF9);

    public static final Color NAVY = BACKDROP;
    public static final Color ROYAL = PRIMARY_BLUE;
    public static final Color ROSE_RED = MOTORPH_RED;
    public static final Color IVORY = BACKDROP;
    public static final Color SKY = new Color(0xD9, 0xE5, 0xF4);
    public static final Color LAVENDER = BORDER;
    public static final Color PEACH = TEAL;
    public static final Color PAPER = INPUT_BG;
    public static final Color GRAPHITE = new Color(0x3A, 0x4C, 0x68);
    public static final Color CARD = PANEL_BG;
    public static final Color SECONDARY_BUTTON = PRIMARY_BLUE;

    public static final Color[] CHART_PALETTE = new Color[]{
            PRIMARY_BLUE,
            MOTORPH_RED,
            GOLD,
            TEAL,
            PURPLE
    };

    public static final String TAGLINE = "The Filipino's Choice";

    public static final Font TITLE_FONT = loadFont("Helvetica", Font.BOLD, 20f);
    public static final Font SUBTITLE_FONT = loadFont("Helvetica", Font.PLAIN, 12f);
    public static final Font BODY_FONT = loadFont("Helvetica", Font.PLAIN, 13f);
    public static final Font BUTTON_FONT = loadFont("Helvetica", Font.BOLD, 13f);
    public static final Font WELCOME_FONT = loadFont("Helvetica", Font.BOLD, 20f);

    private static final Path PRIMARY_LOGO_PATH = Paths.get("Images", "motorph-system-logo.png");
    private static final Path[] FALLBACK_LOGO_PATHS = new Path[]{
            PRIMARY_LOGO_PATH,
            Paths.get("Images", "motorph-brand-logo.png"),
            Paths.get("Images", "motorph-login-transparent.png")
    };
    private static boolean defaultsInstalled;

    private BrandTheme() {
    }

    public static void installGlobalTheme() {
        if (defaultsInstalled) {
            return;
        }

        UIManager.put("Panel.background", BACKDROP);
        UIManager.put("OptionPane.background", INPUT_BG);
        UIManager.put("OptionPane.foreground", TEXT_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Button.background", PANEL_BG);
        UIManager.put("Button.foreground", TEXT_DARK);
        UIManager.put("Button.select", SKY);
        UIManager.put("Button.disabledText", MUTED);
        UIManager.put("TextField.background", INPUT_BG);
        UIManager.put("TextField.foreground", TEXT_DARK);
        UIManager.put("TextField.caretForeground", TEXT_DARK);
        UIManager.put("TextField.selectionBackground", ACCENT_BLUE);
        UIManager.put("TextField.selectionForeground", TEXT_DARK);
        UIManager.put("TextField.inactiveForeground", MUTED_DARK);
        UIManager.put("PasswordField.background", INPUT_BG);
        UIManager.put("PasswordField.foreground", TEXT_DARK);
        UIManager.put("PasswordField.caretForeground", TEXT_DARK);
        UIManager.put("PasswordField.selectionBackground", ACCENT_BLUE);
        UIManager.put("PasswordField.selectionForeground", TEXT_DARK);
        UIManager.put("PasswordField.inactiveForeground", MUTED_DARK);
        UIManager.put("TextArea.background", INPUT_BG);
        UIManager.put("TextArea.foreground", TEXT_DARK);
        UIManager.put("TextArea.caretForeground", TEXT_DARK);
        UIManager.put("TextArea.selectionBackground", ACCENT_BLUE);
        UIManager.put("TextArea.selectionForeground", TEXT_DARK);
        UIManager.put("FormattedTextField.background", INPUT_BG);
        UIManager.put("FormattedTextField.foreground", TEXT_DARK);
        UIManager.put("FormattedTextField.caretForeground", TEXT_DARK);
        UIManager.put("FormattedTextField.selectionBackground", ACCENT_BLUE);
        UIManager.put("FormattedTextField.selectionForeground", TEXT_DARK);
        UIManager.put("ComboBox.background", INPUT_BG);
        UIManager.put("ComboBox.foreground", TEXT_DARK);
        UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
        UIManager.put("ComboBox.selectionForeground", TEXT_DARK);
        UIManager.put("ComboBox.disabledForeground", MUTED_DARK);
        UIManager.put("List.background", INPUT_BG);
        UIManager.put("List.foreground", TEXT_DARK);
        UIManager.put("List.selectionBackground", ACCENT_BLUE);
        UIManager.put("List.selectionForeground", TEXT_DARK);
        UIManager.put("Table.background", INPUT_BG);
        UIManager.put("Table.font", BODY_FONT);
        UIManager.put("Table.foreground", TEXT_DARK);
        UIManager.put("Table.selectionBackground", ACCENT_BLUE);
        UIManager.put("Table.selectionForeground", TEXT_DARK);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.font", BUTTON_FONT);
        UIManager.put("ScrollPane.background", INPUT_BG);
        UIManager.put("Viewport.background", INPUT_BG);
        UIManager.put("TabbedPane.background", GRAPHITE);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selected", PANEL_BG);
        UIManager.put("TabbedPane.focus", PRIMARY_BLUE);
        UIManager.put("Separator.foreground", BORDER);
        UIManager.put("Separator.background", BORDER);
        UIManager.put("Menu.background", INPUT_BG);
        UIManager.put("Menu.foreground", TEXT_DARK);
        UIManager.put("MenuItem.background", INPUT_BG);
        UIManager.put("MenuItem.foreground", TEXT_DARK);
        UIManager.put("CheckBox.foreground", TEXT_DARK);
        UIManager.put("RadioButton.foreground", TEXT_DARK);
        UIManager.put("ToolTip.background", INPUT_BG);
        UIManager.put("ToolTip.foreground", TEXT_DARK);
        defaultsInstalled = true;
    }

    private static Font loadFont(String family, int style, float size) {
        Font requested = new Font(family, style, Math.round(size));
        if (!"Dialog".equalsIgnoreCase(requested.getFamily())) {
            return requested.deriveFont(style, size);
        }

        Font fallback = new Font("Arial", style, Math.round(size));
        if (!"Dialog".equalsIgnoreCase(fallback.getFamily())) {
            return fallback.deriveFont(style, size);
        }

        return new Font("SansSerif", style, Math.round(size)).deriveFont(style, size);
    }

    public static ImageIcon loadLogoIcon(int width, int height) {
        return loadScaledIcon(width, height);
    }

    public static ImageIcon loadEmployeeDashboardLogoIcon(int width, int height) {
        return loadLogoIcon(width, height);
    }

    public static ImageIcon loadHeaderLogoIcon() {
        return loadLogoIcon(188, 72);
    }

    public static ImageIcon loadTitleLogoIcon() {
        return loadLogoIcon(58, 24);
    }

    private static ImageIcon loadScaledIcon(int width, int height) {
        for (Path path : FALLBACK_LOGO_PATHS) {
            if (!Files.exists(path)) {
                continue;
            }
            ImageIcon icon = new ImageIcon(path.toString());
            int originalWidth = Math.max(1, icon.getIconWidth());
            int originalHeight = Math.max(1, icon.getIconHeight());
            double scale = Math.min((double) width / originalWidth, (double) height / originalHeight);
            int targetWidth = Math.max(1, (int) Math.round(originalWidth * scale));
            int targetHeight = Math.max(1, (int) Math.round(originalHeight * scale));
            Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY_BLUE, TEXT_INVERSE, new Color(0x6F, 0x88, 0xAD), new Insets(8, 14, 8, 14));
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, PANEL_BG, TEXT_DARK, new Color(0xC9, 0xD6, 0xE8), new Insets(8, 14, 8, 14));
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, new Color(0xFE, 0xF2, 0xF2), MOTORPH_RED, new Color(0xFE, 0xCD, 0xD3), new Insets(10, 18, 10, 18));
    }

    private static void styleButton(JButton button,
                                    Color background,
                                    Color foreground,
                                    Color borderColor,
                                    Insets padding) {
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));
    }

    public static void styleNavigationButton(JButton button) {
        styleButton(button, new Color(0x46, 0x5C, 0x7D), TEXT_INVERSE, new Color(0x5A, 0x72, 0x95), new Insets(9, 14, 9, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 13f));
    }

    public static void styleSurface(JComponent component) {
        component.setBackground(BACKDROP);
        component.setForeground(TEXT_DARK);
    }

    public static void styleDarkSurface(JComponent component) {
        component.setBackground(GRAPHITE);
        component.setForeground(TEXT_INVERSE);
    }

    public static void styleCardSurface(JComponent component) {
        component.setBackground(PANEL_BG);
        component.setForeground(TEXT_DARK);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
    }

    public static void styleTable(JTable table) {
        table.setBackground(INPUT_BG);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setRowHeight(32);
        table.setFont(BODY_FONT.deriveFont(13f));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setRowMargin(0);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setFont(BUTTON_FONT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable source,
                                                                    Object value,
                                                                    boolean isSelected,
                                                                    boolean hasFocus,
                                                                    int row,
                                                                    int column) {
                java.awt.Component component = super.getTableCellRendererComponent(source, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    component.setBackground(ACCENT_BLUE);
                    component.setForeground(TEXT_DARK);
                } else {
                    component.setBackground(row % 2 == 0 ? INPUT_BG : TABLE_ALT);
                    component.setForeground(TEXT_DARK);
                }
                return component;
            }
        });
    }

    public static void setTitleWithLogo(JLabel label, String text) {
        label.setText(text);
        label.setFont(TITLE_FONT.deriveFont(Font.BOLD, 18f));
        label.setForeground(TEXT_DARK);
        label.setIcon(loadTitleLogoIcon());
        label.setIconTextGap(10);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public static void setWelcomeText(JLabel label, String text, boolean darkSurface) {
        label.setText(text);
        label.setFont(WELCOME_FONT);
        label.setForeground(darkSurface ? TEXT_INVERSE : TEXT_DARK);
    }

    public static void styleInputField(JTextField field) {
        field.setFont(BODY_FONT.deriveFont(13f));
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setSelectionColor(ACCENT_BLUE);
        field.setSelectedTextColor(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC4, 0xD2, 0xE4), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(INPUT_BG);
        comboBox.setForeground(TEXT_DARK);
        comboBox.setFont(BODY_FONT.deriveFont(13f));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC4, 0xD2, 0xE4), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(INPUT_BG);
        scrollPane.setBackground(PANEL_BG);
    }

    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(GRAPHITE);
        tabbedPane.setForeground(TEXT);
        tabbedPane.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        tabbedPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }
}

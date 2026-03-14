package com.mycompany.motorph.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.io.InputStream;
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
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;

public final class BrandTheme {

    public static final Color BACKDROP = new Color(0xAF, 0xC4, 0xD6);
    public static final Color NAVY = new Color(0x1F, 0x2A, 0x3A);
    public static final Color TABLE_HEADER = new Color(0x1F, 0x2A, 0x3A);
    public static final Color ROYAL = new Color(0x8F, 0xA8, 0xC6);
    public static final Color ROSE_RED = new Color(0xB2, 0x3A, 0x3A);
    public static final Color GOLD = new Color(0xF2, 0xC9, 0x4C);
    public static final Color IVORY = BACKDROP;
    public static final Color SKY = new Color(0xC3, 0xD2, 0xE1);
    public static final Color TEXT = new Color(0xE8, 0xEE, 0xF5);
    public static final Color MUTED = new Color(0xA9, 0xB4, 0xC3);
    public static final Color LAVENDER = new Color(0x3B, 0x4C, 0x63);
    public static final Color PEACH = ROYAL;
    public static final Color PAPER = new Color(0x2D, 0x3E, 0x55);
    public static final Color GRAPHITE = new Color(0x1C, 0x27, 0x36);
    public static final Color CARD = new Color(0x27, 0x36, 0x4A);
    public static final Color SECONDARY_BUTTON = PAPER;
    public static final Color TABLE_ALT = new Color(0x2C, 0x3B, 0x50);

    public static final String TAGLINE = "The Filipino's Choice";

    public static final Font TITLE_FONT = loadFont("/fonts/DMSans-Bold.ttf", Font.BOLD, 22f);
    public static final Font SUBTITLE_FONT = loadFont("/fonts/DMSans-Regular.ttf", Font.PLAIN, 12f);
    public static final Font BODY_FONT = loadFont("/fonts/DMSans-Regular.ttf", Font.PLAIN, 14f);
    public static final Font BUTTON_FONT = loadFont("/fonts/DMSans-Medium.ttf", Font.BOLD, 14f);
    public static final Font WELCOME_FONT = loadFont("/fonts/DMSans-Bold.ttf", Font.BOLD, 22f);

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

        UIManager.put("Panel.background", NAVY);
        UIManager.put("OptionPane.background", GRAPHITE);
        UIManager.put("OptionPane.foreground", TEXT);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Button.background", SECONDARY_BUTTON);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.select", ROYAL);
        UIManager.put("Button.disabledText", MUTED);
        UIManager.put("TextField.background", PAPER);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.selectionBackground", ROYAL);
        UIManager.put("TextField.selectionForeground", NAVY);
        UIManager.put("TextField.inactiveForeground", MUTED);
        UIManager.put("PasswordField.background", PAPER);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.caretForeground", TEXT);
        UIManager.put("PasswordField.selectionBackground", ROYAL);
        UIManager.put("PasswordField.selectionForeground", NAVY);
        UIManager.put("PasswordField.inactiveForeground", MUTED);
        UIManager.put("TextArea.background", PAPER);
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("TextArea.caretForeground", TEXT);
        UIManager.put("TextArea.selectionBackground", ROYAL);
        UIManager.put("TextArea.selectionForeground", NAVY);
        UIManager.put("FormattedTextField.background", PAPER);
        UIManager.put("FormattedTextField.foreground", TEXT);
        UIManager.put("FormattedTextField.caretForeground", TEXT);
        UIManager.put("FormattedTextField.selectionBackground", ROYAL);
        UIManager.put("FormattedTextField.selectionForeground", NAVY);
        UIManager.put("ComboBox.background", PAPER);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", ROYAL);
        UIManager.put("ComboBox.selectionForeground", NAVY);
        UIManager.put("ComboBox.disabledForeground", MUTED);
        UIManager.put("List.background", CARD);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("List.selectionBackground", ROYAL);
        UIManager.put("List.selectionForeground", NAVY);
        UIManager.put("Table.background", CARD);
        UIManager.put("Table.font", BODY_FONT);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", ROYAL);
        UIManager.put("Table.selectionForeground", NAVY);
        UIManager.put("Table.gridColor", LAVENDER);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.font", BUTTON_FONT);
        UIManager.put("ScrollPane.background", CARD);
        UIManager.put("Viewport.background", CARD);
        UIManager.put("TabbedPane.background", NAVY);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selected", ROYAL);
        UIManager.put("TabbedPane.focus", ROYAL);
        UIManager.put("Separator.foreground", LAVENDER);
        UIManager.put("Separator.background", LAVENDER);
        UIManager.put("Menu.foreground", TEXT);
        UIManager.put("MenuItem.foreground", TEXT);
        UIManager.put("CheckBox.foreground", TEXT);
        UIManager.put("RadioButton.foreground", TEXT);
        UIManager.put("ToolTip.background", PAPER);
        UIManager.put("ToolTip.foreground", TEXT);
        defaultsInstalled = true;
    }

    private static Font loadFont(String resourcePath, int style, float size) {
        try (InputStream stream = BrandTheme.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(style, size);
            }
        } catch (Exception ignored) {
        }

        Font direct = new Font("DM Sans", style, Math.round(size));
        if (!"Dialog".equalsIgnoreCase(direct.getFamily())
                || "DM Sans".equalsIgnoreCase(direct.getName())
                || "DM Sans".equalsIgnoreCase(direct.getFamily())) {
            return direct.deriveFont(style, size);
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
        button.setUI(new BasicButtonUI());
        button.setBackground(ROYAL);
        button.setForeground(NAVY);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x72, 0x89, 0xA3), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(
                                BevelBorder.RAISED,
                                new Color(0xA5, 0xB8, 0xCF),
                                new Color(0x97, 0xAE, 0xC9),
                                new Color(0x74, 0x8C, 0xA6),
                                new Color(0x65, 0x7B, 0x92)
                        ),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                )
        ));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setBackground(SECONDARY_BUTTON);
        button.setForeground(TEXT);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LAVENDER, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(
                                BevelBorder.RAISED,
                                new Color(0x38, 0x4B, 0x61),
                                new Color(0x34, 0x46, 0x5B),
                                new Color(0x24, 0x31, 0x43),
                                new Color(0x1D, 0x27, 0x35)
                        ),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                )
        ));
    }

    public static void styleDangerButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setBackground(ROSE_RED);
        button.setForeground(TEXT);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x8E, 0x2B, 0x2B), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(
                                BevelBorder.RAISED,
                                new Color(0xC9, 0x62, 0x62),
                                new Color(0xB8, 0x50, 0x50),
                                new Color(0x88, 0x2B, 0x2B),
                                new Color(0x72, 0x22, 0x22)
                        ),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                )
        ));
    }

    public static void styleNavigationButton(JButton button) {
        styleSecondaryButton(button);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LAVENDER, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(
                                BevelBorder.RAISED,
                                new Color(0x38, 0x4B, 0x61),
                                new Color(0x34, 0x46, 0x5B),
                                new Color(0x24, 0x31, 0x43),
                                new Color(0x1D, 0x27, 0x35)
                        ),
                        BorderFactory.createEmptyBorder(9, 16, 9, 16)
                )
        ));
    }

    public static void styleSurface(JComponent component) {
        component.setBackground(NAVY);
        component.setForeground(TEXT);
    }

    public static void styleDarkSurface(JComponent component) {
        component.setBackground(GRAPHITE);
        component.setForeground(TEXT);
    }

    public static void styleCardSurface(JComponent component) {
        component.setBackground(CARD);
        component.setForeground(TEXT);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 4, new Color(0, 0, 0, 48)),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LAVENDER, 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        ));
    }

    public static void styleTable(JTable table) {
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(LAVENDER);
        table.setRowHeight(32);
        table.setFont(BODY_FONT);
        table.setSelectionBackground(ROYAL);
        table.setSelectionForeground(NAVY);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(BUTTON_FONT);
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
                    component.setBackground(ROYAL);
                    component.setForeground(NAVY);
                } else {
                    component.setBackground(row % 2 == 0 ? CARD : TABLE_ALT);
                    component.setForeground(TEXT);
                }
                return component;
            }
        });
    }

    public static void setTitleWithLogo(JLabel label, String text) {
        label.setText(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        label.setIcon(loadTitleLogoIcon());
        label.setIconTextGap(10);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public static void setWelcomeText(JLabel label, String text, boolean darkSurface) {
        label.setText(text);
        label.setFont(WELCOME_FONT);
        label.setForeground(darkSurface ? TEXT : NAVY);
    }

    public static void styleInputField(JTextField field) {
        field.setFont(BODY_FONT.deriveFont(16f));
        field.setBackground(PAPER);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(ROYAL);
        field.setSelectedTextColor(NAVY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LAVENDER, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(
                                BevelBorder.LOWERED,
                                new Color(0x34, 0x47, 0x5C),
                                new Color(0x2F, 0x41, 0x56),
                                new Color(0x1F, 0x2A, 0x3A),
                                new Color(0x18, 0x22, 0x30)
                        ),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(PAPER);
        comboBox.setForeground(TEXT);
        comboBox.setFont(BODY_FONT.deriveFont(15f));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LAVENDER, 1),
                BorderFactory.createBevelBorder(
                        BevelBorder.LOWERED,
                        new Color(0x34, 0x47, 0x5C),
                        new Color(0x2F, 0x41, 0x56),
                        new Color(0x1F, 0x2A, 0x3A),
                        new Color(0x18, 0x22, 0x30)
                )
        ));
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 4, new Color(0, 0, 0, 48)),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LAVENDER, 1),
                        BorderFactory.createBevelBorder(
                                BevelBorder.LOWERED,
                                new Color(0x34, 0x47, 0x5C),
                                new Color(0x2F, 0x41, 0x56),
                                new Color(0x1F, 0x2A, 0x3A),
                                new Color(0x18, 0x22, 0x30)
                        )
                )
        ));
        scrollPane.getViewport().setBackground(CARD);
    }

    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(NAVY);
        tabbedPane.setForeground(TEXT);
        tabbedPane.setFont(BUTTON_FONT.deriveFont(Font.BOLD, 15f));
        tabbedPane.setBorder(BorderFactory.createLineBorder(LAVENDER, 1));
    }
}

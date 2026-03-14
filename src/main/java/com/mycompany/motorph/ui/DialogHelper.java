package com.mycompany.motorph.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class DialogHelper {

    private DialogHelper() {
    }

    public static boolean showFormDialog(Component parent,
                                         String title,
                                         JComponent content,
                                         String primaryLabel,
                                         String cancelLabel) {

        AtomicReference<Boolean> confirmed = new AtomicReference<>(Boolean.FALSE);
        JDialog dialog = createDialog(parent, title);

        JPanel shell = new JPanel(new BorderLayout(0, 16));
        BrandTheme.styleSurface(shell);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));

        JButton cancelButton = new JButton(cancelLabel == null || cancelLabel.isBlank() ? "Cancel" : cancelLabel);
        JButton primaryButton = new JButton(primaryLabel == null || primaryLabel.isBlank() ? "Continue" : primaryLabel);
        BrandTheme.styleSecondaryButton(cancelButton);
        BrandTheme.stylePrimaryButton(primaryButton);

        cancelButton.addActionListener(evt -> dialog.dispose());
        primaryButton.addActionListener(evt -> {
            confirmed.set(Boolean.TRUE);
            dialog.dispose();
        });

        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(cancelButton);
        buttonRow.add(Box.createHorizontalStrut(10));
        buttonRow.add(primaryButton);

        shell.add(content, BorderLayout.CENTER);
        shell.add(buttonRow, BorderLayout.SOUTH);
        dialog.setContentPane(shell);
        dialog.getRootPane().setDefaultButton(primaryButton);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return confirmed.get();
    }

    public static boolean showConfirmMessage(Component parent,
                                             String title,
                                             String message,
                                             String primaryLabel,
                                             String cancelLabel) {
        JPanel panel = buildMessagePanel(message);
        return showFormDialog(parent, title, panel, primaryLabel, cancelLabel);
    }

    public static String showActionChoice(Component parent,
                                          String title,
                                          String message,
                                          String... options) {

        AtomicReference<String> selected = new AtomicReference<>();
        JDialog dialog = createDialog(parent, title);

        JPanel shell = new JPanel(new BorderLayout(0, 16));
        BrandTheme.styleSurface(shell);
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        shell.add(buildMessagePanel(message), BorderLayout.CENTER);

        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.add(Box.createHorizontalGlue());

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            JButton button = new JButton(option);
            if (i == 0) {
                BrandTheme.stylePrimaryButton(button);
            } else {
                BrandTheme.styleSecondaryButton(button);
            }
            button.addActionListener(evt -> {
                selected.set(option);
                dialog.dispose();
            });
            if (i > 0) {
                buttonRow.add(Box.createHorizontalStrut(10));
            }
            buttonRow.add(button);
        }

        shell.add(buttonRow, BorderLayout.SOUTH);
        dialog.setContentPane(shell);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return selected.get();
    }

    public static String promptText(Component parent,
                                    String title,
                                    String prompt,
                                    String initialValue,
                                    String primaryLabel) {

        JTextField field = new JTextField(initialValue == null ? "" : initialValue);
        field.setPreferredSize(new Dimension(320, 38));
        BrandTheme.styleInputField(field);

        JPanel panel = buildPromptPanel(prompt, field);
        boolean confirmed = showFormDialog(parent, title, panel, primaryLabel, "Cancel");
        if (!confirmed) {
            return null;
        }
        return field.getText();
    }

    public static String promptPassword(Component parent,
                                        String title,
                                        String prompt,
                                        String primaryLabel) {

        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(320, 38));
        BrandTheme.styleInputField(field);

        JPanel panel = buildPromptPanel(prompt, field);
        boolean confirmed = showFormDialog(parent, title, panel, primaryLabel, "Cancel");
        if (!confirmed) {
            return null;
        }
        return new String(field.getPassword());
    }

    private static JPanel buildPromptPanel(String prompt, JComponent inputField) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        BrandTheme.styleSurface(panel);

        JLabel promptLabel = new JLabel(prompt);
        promptLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
        promptLabel.setForeground(BrandTheme.TEXT);

        panel.add(promptLabel, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel buildMessagePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        BrandTheme.styleSurface(panel);

        JLabel label = new JLabel("<html><body style='width:320px'>" + escape(message) + "</body></html>");
        label.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
        label.setForeground(BrandTheme.TEXT);
        label.setVerticalAlignment(SwingConstants.TOP);

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private static JDialog createDialog(Component parent, String title) {
        Window owner = parent instanceof Window
                ? (Window) parent
                : SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        return dialog;
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}

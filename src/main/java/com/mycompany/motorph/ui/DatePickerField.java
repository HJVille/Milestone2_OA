package com.mycompany.motorph.ui;

import com.mycompany.motorph.service.AppClock;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DatePickerField extends JPanel {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final JTextField textField = new JTextField();
    private final JButton triggerButton = new JButton("...");
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel daysPanel = new JPanel(new GridLayout(6, 7, 4, 4));

    private LocalDate selectedDate;
    private LocalDate defaultDate = AppClock.today();
    private YearMonth visibleMonth = YearMonth.from(defaultDate);
    private Runnable onDateChange;

    public DatePickerField() {
        setLayout(new BorderLayout(4, 0));
        setOpaque(false);

        textField.setEditable(false);
        BrandTheme.styleInputField(textField);

        BrandTheme.styleSecondaryButton(triggerButton);
        triggerButton.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        triggerButton.setPreferredSize(new Dimension(42, 34));
        triggerButton.setMinimumSize(new Dimension(42, 34));
        triggerButton.setMaximumSize(new Dimension(42, 34));
        triggerButton.addActionListener(evt -> togglePopup());

        add(textField, BorderLayout.CENTER);
        add(triggerButton, BorderLayout.EAST);

        buildPopup();
    }

    public String getDateText() {
        return textField.getText().trim();
    }

    public LocalDate getDate() {
        return selectedDate;
    }

    public void setOnDateChange(Runnable onDateChange) {
        this.onDateChange = onDateChange;
    }

    public void setDefaultDate(LocalDate defaultDate) {
        this.defaultDate = defaultDate == null ? AppClock.today() : defaultDate;
        if (selectedDate == null) {
            visibleMonth = YearMonth.from(this.defaultDate);
            refreshCalendar();
        }
    }

    public void setDate(LocalDate date) {
        boolean changed = selectedDate == null ? date != null : !selectedDate.equals(date);
        selectedDate = date;
        if (date == null) {
            textField.setText("");
            visibleMonth = YearMonth.from(defaultDate);
        } else {
            textField.setText(DISPLAY_FORMATTER.format(date));
            visibleMonth = YearMonth.from(date);
        }
        refreshCalendar();
        if (changed && onDateChange != null) {
            onDateChange.run();
        }
    }

    public void setDateText(String value) {
        if (value == null || value.trim().isEmpty()) {
            setDate(null);
            return;
        }
        setDate(LocalDate.parse(value.trim()));
    }

    private void togglePopup() {
        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
            return;
        }

        if (selectedDate != null) {
            visibleMonth = YearMonth.from(selectedDate);
        }
        refreshCalendar();
        popupMenu.show(this, 0, getHeight());
    }

    private void buildPopup() {
        JPanel popupRoot = new JPanel(new BorderLayout(0, 10));
        BrandTheme.styleSurface(popupRoot);
        popupRoot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setOpaque(false);

        JButton previousButton = createHeaderButton("<");
        JButton nextButton = createHeaderButton(">");
        previousButton.addActionListener(evt -> {
            visibleMonth = visibleMonth.minusMonths(1);
            refreshCalendar();
        });
        nextButton.addActionListener(evt -> {
            visibleMonth = visibleMonth.plusMonths(1);
            refreshCalendar();
        });

        monthLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
        monthLabel.setForeground(BrandTheme.TEXT);

        header.add(previousButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        JPanel weekdaysPanel = new JPanel(new GridLayout(1, 7, 4, 4));
        weekdaysPanel.setOpaque(false);
        for (String labelText : new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}) {
            JLabel dayLabel = new JLabel(labelText, SwingConstants.CENTER);
            dayLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
            dayLabel.setForeground(BrandTheme.MUTED);
            weekdaysPanel.add(dayLabel);
        }

        JPanel calendarPanel = new JPanel();
        calendarPanel.setOpaque(false);
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));
        calendarPanel.add(weekdaysPanel);
        calendarPanel.add(Box.createVerticalStrut(6));
        daysPanel.setOpaque(false);
        calendarPanel.add(daysPanel);

        JButton clearButton = new JButton("Clear");
        BrandTheme.styleSecondaryButton(clearButton);
        clearButton.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        clearButton.addActionListener(evt -> {
            setDate(null);
            popupMenu.setVisible(false);
        });

        popupRoot.add(header, BorderLayout.NORTH);
        popupRoot.add(calendarPanel, BorderLayout.CENTER);
        popupRoot.add(clearButton, BorderLayout.SOUTH);

        popupMenu.setBorder(null);
        popupMenu.add(popupRoot);
        refreshCalendar();
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        BrandTheme.styleSecondaryButton(button);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        button.setPreferredSize(new Dimension(38, 30));
        button.setMinimumSize(new Dimension(38, 30));
        button.setMaximumSize(new Dimension(38, 30));
        return button;
    }

    private void refreshCalendar() {
        monthLabel.setText(visibleMonth.getMonth().name().substring(0, 1)
                + visibleMonth.getMonth().name().substring(1).toLowerCase()
                + " " + visibleMonth.getYear());

        daysPanel.removeAll();

        LocalDate firstDay = visibleMonth.atDay(1);
        int leadingBlankDays = firstDay.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (leadingBlankDays < 0) {
            leadingBlankDays += 7;
        }

        for (int i = 0; i < leadingBlankDays; i++) {
            daysPanel.add(new JLabel());
        }

        for (int day = 1; day <= visibleMonth.lengthOfMonth(); day++) {
            LocalDate date = visibleMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            styleDayButton(dayButton, date);
            dayButton.addActionListener(evt -> {
                setDate(date);
                popupMenu.setVisible(false);
            });
            daysPanel.add(dayButton);
        }

        int cellsToFill = 42 - leadingBlankDays - visibleMonth.lengthOfMonth();
        for (int i = 0; i < cellsToFill; i++) {
            daysPanel.add(new JLabel());
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void styleDayButton(JButton button, LocalDate date) {
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setFont(BrandTheme.BODY_FONT.deriveFont(Font.PLAIN, 12f));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(34, 28));
        button.setBorder(BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1));

        if (date.equals(selectedDate)) {
            button.setBackground(BrandTheme.ROYAL);
            button.setForeground(BrandTheme.NAVY);
        } else if (date.equals(AppClock.today())) {
            button.setBackground(BrandTheme.PAPER);
            button.setForeground(BrandTheme.TEXT);
        } else {
            button.setBackground(BrandTheme.CARD);
            button.setForeground(BrandTheme.TEXT);
        }

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            button.setForeground(date.equals(selectedDate) ? BrandTheme.NAVY : BrandTheme.MUTED);
        }
    }
}

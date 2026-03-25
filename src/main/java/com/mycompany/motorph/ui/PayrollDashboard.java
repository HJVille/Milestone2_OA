package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AppClock;
import com.mycompany.motorph.service.EmployeePortalService;
import com.mycompany.motorph.service.NotificationService;
import com.mycompany.motorph.service.PayrollRecordService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PayrollDashboard extends JPanel {

    private static final int COMPANY_START_YEAR = 2020;
    private static final int FUTURE_YEAR_SPAN = 10;
    private static final DecimalFormat MONEY = new DecimalFormat("PHP #,##0.00");
    private static final DateTimeFormatter PERIOD_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter MONTH_NAME = DateTimeFormatter.ofPattern("MMMM");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");

    private final User user;
    private final EmployeePortalService employeePortalService = new EmployeePortalService();
    private final PayrollRecordService payrollRecordService = new PayrollRecordService();
    private final NotificationService notificationService = new NotificationService();
    private final List<PayrollPeriodOption> availablePeriods;

    private final CardLayout contentCards = new CardLayout();
    private final ProcessPayrollPanel processPayrollPanel;
    private final PayrollRecordsPanel payrollRecordsPanel;
    private JPanel contentPanel;
    private JButton btnProcessPayroll;
    private JButton btnPayrollRecords;
    private JButton btnLogout;

    public PayrollDashboard() {
        this(null);
    }

    public PayrollDashboard(User user) {
        BrandTheme.installGlobalTheme();
        this.user = user;
        this.availablePeriods = employeePortalService.getAvailablePayrollPeriods();
        this.processPayrollPanel = new ProcessPayrollPanel();
        this.payrollRecordsPanel = new PayrollRecordsPanel();
        setLayout(new BorderLayout(0, 16));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildNavigation(), BorderLayout.WEST);
        add(buildMainShell(), BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(BrandTheme.GRAPHITE);
        navigation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BrandTheme.LAVENDER),
                BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));
        navigation.setPreferredSize(new Dimension(202, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        javax.swing.JLabel menuLabel = new javax.swing.JLabel("Finance Tools");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
        menuLabel.setForeground(BrandTheme.MUTED_INVERSE);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnProcessPayroll = createNavigationButton("Process Payroll");
        btnPayrollRecords = createNavigationButton("Payroll Records");
        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

        btnProcessPayroll.addActionListener(evt -> {
            processPayrollPanel.refreshPreview();
            contentCards.show(contentPanel, "process");
        });
        btnPayrollRecords.addActionListener(evt -> {
            payrollRecordsPanel.reloadRows();
            contentCards.show(contentPanel, "records");
        });

        menuButtons.add(menuLabel);
        menuButtons.add(Box.createVerticalStrut(12));
        menuButtons.add(btnProcessPayroll);
        menuButtons.add(Box.createVerticalStrut(10));
        menuButtons.add(btnPayrollRecords);
        menuButtons.add(Box.createVerticalGlue());

        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.add(btnLogout);

        navigation.add(menuButtons, BorderLayout.CENTER);
        navigation.add(footerPanel, BorderLayout.SOUTH);
        return navigation;
    }

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        configureNavigationButton(button);
        return button;
    }

    private void configureNavigationButton(JButton button) {
        BrandTheme.styleNavigationButton(button);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 13f));
        button.setMinimumSize(new Dimension(170, 36));
        button.setPreferredSize(new Dimension(170, 36));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JPanel buildMainShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BrandTheme.NAVY);
        shell.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        contentPanel = new JPanel(contentCards);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(processPayrollPanel, "process");
        contentPanel.add(payrollRecordsPanel, "records");
        contentCards.show(contentPanel, "process");
        shell.add(contentPanel, BorderLayout.CENTER);
        return shell;
    }

    private void refreshAll() {
        processPayrollPanel.refreshPreview();
        payrollRecordsPanel.reloadRows();
    }

    public void refreshDashboard() {
        refreshAll();
    }

    private Set<String> buildAllowedPeriodKeys() {
        Set<String> allowedKeys = new LinkedHashSet<>();
        for (PayrollPeriodOption period : availablePeriods) {
            allowedKeys.add(period.getStartDate() + "|" + period.getEndDate());
        }
        return allowedKeys;
    }

    private String formatStoredDate(String value) {
        try {
            return PERIOD_DATE.format(LocalDate.parse(value.trim()));
        } catch (Exception e) {
            return value;
        }
    }

    private String formatRunTypeLabel(PayrollPeriodOption option) {
        if (option == null) {
            return "Not available";
        }
        if (option.getType() == PayrollPeriodOption.Type.MONTHLY) {
            return "Monthly Summary";
        }
        return option.getStartDate().getDayOfMonth() <= 15 ? "First Pay Period" : "Second Pay Period";
    }

    private String formatPeriodFilterLabel(PayrollPeriodOption option) {
        if (option == null) {
            return "";
        }
        return MONTH_YEAR.format(option.getEndDate()) + " | " + formatRunTypeLabel(option);
    }

    private String formatPeriod(PayrollPeriodOption option) {
        return formatPeriodFilterLabel(option) + " | "
                + PERIOD_DATE.format(option.getStartDate()) + " - " + PERIOD_DATE.format(option.getEndDate());
    }

    private boolean hasSavedPayrollForPeriod(PayrollPeriodOption option) {
        return option != null && payrollRecordService.hasPayrollForPeriod(
                option.getStartDate().toString(),
                option.getEndDate().toString()
        );
    }

    private PayrollPeriodOption getLatestAvailableSemiMonthlyPeriod() {
        for (PayrollPeriodOption option : availablePeriods) {
            if (option.getType() == PayrollPeriodOption.Type.SEMI_MONTHLY) {
                return option;
            }
        }
        return null;
    }

    private List<Integer> getYearOptions() {
        List<Integer> years = new ArrayList<>();
        int endYear = resolveYearRangeEnd();
        for (int year = COMPANY_START_YEAR; year <= endYear; year++) {
            years.add(year);
        }
        return years;
    }

    private int resolveYearRangeEnd() {
        int latestYear = AppClock.today().getYear();
        for (PayrollPeriodOption option : availablePeriods) {
            if (option.getType() == PayrollPeriodOption.Type.SEMI_MONTHLY) {
                latestYear = Math.max(latestYear, option.getStartDate().getYear());
            }
        }
        return latestYear + FUTURE_YEAR_SPAN;
    }

    private PayrollPeriodOption findSemiMonthlyPeriod(int year, Month month, CutoffOption cutoff) {
        if (month == null || cutoff == null) {
            return null;
        }
        for (PayrollPeriodOption option : availablePeriods) {
            if (option.getType() != PayrollPeriodOption.Type.SEMI_MONTHLY) {
                continue;
            }
            if (option.getStartDate().getYear() == year
                    && option.getStartDate().getMonth() == month
                    && cutoff.matches(option)) {
                return option;
            }
        }
        return null;
    }

    private static String formatCompactAmount(double amount) {
        double absolute = Math.abs(amount);
        if (absolute >= 1_000_000d) {
            return "PHP " + new DecimalFormat("0.0").format(amount / 1_000_000d) + "M";
        }
        if (absolute >= 1_000d) {
            return "PHP " + new DecimalFormat("0.0").format(amount / 1_000d) + "K";
        }
        return MONEY.format(amount);
    }

    private void setControlWidth(JComboBox<?> comboBox, int width) {
        Dimension size = new Dimension(width, 34);
        comboBox.setPreferredSize(size);
        comboBox.setMinimumSize(size);
        comboBox.setMaximumSize(size);
    }

    private static final class MonthOption {

        private final Month month;
        private final String label;

        MonthOption(Month month) {
            this.month = month;
            this.label = MONTH_NAME.format(LocalDate.of(2000, month, 1));
        }

        Month getMonth() {
            return month;
        }

        boolean matches(Month otherMonth) {
            return month == otherMonth;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum CutoffOption {
        FIRST("First Pay Period"),
        SECOND("Second Pay Period");

        private final String label;

        CutoffOption(String label) {
            this.label = label;
        }

        boolean matches(PayrollPeriodOption option) {
            int startDay = option.getStartDate().getDayOfMonth();
            return (this == FIRST && startDay <= 15) || (this == SECOND && startDay > 15);
        }

        static CutoffOption from(PayrollPeriodOption option) {
            return option != null && option.getStartDate().getDayOfMonth() > 15 ? SECOND : FIRST;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final class PeriodSelectionControls {

        private final JComboBox<CutoffOption> cutoffSelector = new JComboBox<>();
        private final JComboBox<MonthOption> monthSelector = new JComboBox<>();
        private final JComboBox<Integer> yearSelector = new JComboBox<>();
        private Runnable selectionListener = () -> { };
        private boolean adjusting;

        PeriodSelectionControls() {
            BrandTheme.styleComboBox(cutoffSelector);
            BrandTheme.styleComboBox(monthSelector);
            BrandTheme.styleComboBox(yearSelector);
            setControlWidth(cutoffSelector, 150);
            setControlWidth(monthSelector, 150);
            setControlWidth(yearSelector, 100);

            populateCutoffs();
            populateMonths();
            populateYears();
            setSelectedPeriod(getLatestAvailableSemiMonthlyPeriod());

            yearSelector.addActionListener(evt -> notifySelectionChanged());
            monthSelector.addActionListener(evt -> notifySelectionChanged());
            cutoffSelector.addActionListener(evt -> notifySelectionChanged());
        }

        void setOnSelectionChange(Runnable selectionListener) {
            this.selectionListener = selectionListener == null ? () -> { } : selectionListener;
        }

        JComboBox<CutoffOption> getCutoffSelector() {
            return cutoffSelector;
        }

        JComboBox<MonthOption> getMonthSelector() {
            return monthSelector;
        }

        JComboBox<Integer> getYearSelector() {
            return yearSelector;
        }

        PayrollPeriodOption getSelectedPeriod() {
            Integer year = (Integer) yearSelector.getSelectedItem();
            Month month = getSelectedMonth();
            CutoffOption cutoff = (CutoffOption) cutoffSelector.getSelectedItem();
            if (year == null || month == null || cutoff == null) {
                return null;
            }
            return findSemiMonthlyPeriod(year, month, cutoff);
        }

        void setSelectedPeriod(PayrollPeriodOption targetPeriod) {
            adjusting = true;
            if (targetPeriod != null) {
                yearSelector.setSelectedItem(targetPeriod.getStartDate().getYear());
                selectMonth(targetPeriod.getStartDate().getMonth());
                cutoffSelector.setSelectedItem(CutoffOption.from(targetPeriod));
            } else {
                LocalDate today = AppClock.today();
                yearSelector.setSelectedItem(today.getYear());
                selectMonth(today.getMonth());
                cutoffSelector.setSelectedItem(today.getDayOfMonth() <= 15 ? CutoffOption.FIRST : CutoffOption.SECOND);
            }
            adjusting = false;
        }

        private void notifySelectionChanged() {
            if (adjusting) {
                return;
            }
            selectionListener.run();
        }

        private Month getSelectedMonth() {
            MonthOption option = (MonthOption) monthSelector.getSelectedItem();
            return option == null ? null : option.getMonth();
        }

        private void populateYears() {
            yearSelector.removeAllItems();
            List<Integer> years = getYearOptions();
            for (Integer year : years) {
                yearSelector.addItem(year);
            }
        }

        private void populateMonths() {
            monthSelector.removeAllItems();
            for (Month month : Month.values()) {
                monthSelector.addItem(new MonthOption(month));
            }
        }

        private void populateCutoffs() {
            cutoffSelector.removeAllItems();
            for (CutoffOption option : CutoffOption.values()) {
                cutoffSelector.addItem(option);
            }
        }

        private void selectMonth(Month month) {
            for (int index = 0; index < monthSelector.getItemCount(); index++) {
                MonthOption option = monthSelector.getItemAt(index);
                if (option != null && option.matches(month)) {
                    monthSelector.setSelectedIndex(index);
                    return;
                }
            }
        }
    }

    private class ProcessPayrollPanel extends JPanel {

        private final PeriodSelectionControls periodSelectors = new PeriodSelectionControls();
        private final JTable previewTable = new JTable();
        private final JLabel summaryLabel = new JLabel(" ");
        private final MetricCard employeesCard = new MetricCard(
                "Employees Covered",
                "0",
                "Run a preview to populate this KPI.",
                BrandTheme.PRIMARY_BLUE
        );
        private final MetricCard grossPayrollCard = new MetricCard(
                "Gross Salary",
                MONEY.format(0),
                "Attendance-based salary before allowances.",
                BrandTheme.GOLD
        );
        private final MetricCard deductionsCard = new MetricCard(
                "Total Deductions",
                MONEY.format(0),
                "Taxes and government contributions.",
                BrandTheme.MOTORPH_RED
        );
        private final MetricCard netPayrollCard = new MetricCard(
                "Net Payroll",
                MONEY.format(0),
                "Projected amount for release.",
                BrandTheme.TEAL
        );
        private final AnalyticsChartCard compositionChart = new AnalyticsChartCard(
                "Payroll Composition",
                "Gross salary plus allowances less deductions for the selected pay period."
        );
        private final JLabel selectedPeriodValue = createContextValueLabel();
        private final JLabel coverageWindowValue = createContextValueLabel();
        private final JLabel statusValue = createContextValueLabel();

        ProcessPayrollPanel() {
            setLayout(new BorderLayout(0, 10));
            BrandTheme.styleSurface(this);

            JPanel top = new JPanel(new BorderLayout(12, 12));
            top.setOpaque(false);

            JLabel title = new JLabel("Process Payroll");
            title.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 16f));
            title.setForeground(BrandTheme.TEXT_DARK);
            top.add(title, BorderLayout.WEST);

            JPanel controls = new JPanel();
            controls.setOpaque(false);
            controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

            JLabel cutoffLabel = new JLabel("Pay Period");
            JLabel monthLabel = new JLabel("Month");
            JLabel yearLabel = new JLabel("Year");
            for (JLabel label : new JLabel[]{cutoffLabel, monthLabel, yearLabel}) {
                label.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
                label.setForeground(BrandTheme.TEXT_DARK);
            }

            JButton btnPreview = new JButton("Preview");
            JButton btnProcess = new JButton("Process & Save");
            BrandTheme.styleSecondaryButton(btnPreview);
            BrandTheme.stylePrimaryButton(btnProcess);
            periodSelectors.setOnSelectionChange(this::markPreviewStale);
            btnPreview.addActionListener(evt -> refreshPreview());
            btnProcess.addActionListener(evt -> processSelectedPeriod());

            controls.add(cutoffLabel);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(periodSelectors.getCutoffSelector());
            controls.add(Box.createHorizontalStrut(10));
            controls.add(monthLabel);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(periodSelectors.getMonthSelector());
            controls.add(Box.createHorizontalStrut(10));
            controls.add(yearLabel);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(periodSelectors.getYearSelector());
            controls.add(Box.createHorizontalStrut(10));
            controls.add(btnPreview);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(btnProcess);
            top.add(controls, BorderLayout.EAST);

            previewTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Employee #", "Employee Name", "Gross Salary", "Allowances", "Total Gross", "Deductions", "Net Salary", "Days"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            BrandTheme.styleTable(previewTable);

            summaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            summaryLabel.setForeground(BrandTheme.MUTED);

            JPanel north = new JPanel(new BorderLayout(0, 12));
            north.setOpaque(false);
            north.add(top, BorderLayout.NORTH);
            north.add(buildAnalyticsDeck(), BorderLayout.CENTER);

            add(north, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(previewTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(summaryLabel, BorderLayout.SOUTH);

            resetPreviewInsights("Awaiting payroll preview.");
        }

        private void markPreviewStale() {
            DefaultTableModel model = (DefaultTableModel) previewTable.getModel();
            model.setRowCount(0);
            resetPreviewInsights("Selection changed. Click Preview to load the selected pay period.");
            summaryLabel.setText("Selection updated. Click Preview to load payroll data.");
        }

        private JPanel buildAnalyticsDeck() {
            JPanel deck = new JPanel();
            deck.setOpaque(false);
            deck.setLayout(new BoxLayout(deck, BoxLayout.Y_AXIS));

            JPanel contextStrip = new JPanel(new GridLayout(1, 3, 8, 8));
            contextStrip.setOpaque(false);
            contextStrip.add(buildContextTile("Selected Period", selectedPeriodValue));
            contextStrip.add(buildContextTile("Coverage Window", coverageWindowValue));
            contextStrip.add(buildContextTile("Status", statusValue));

            JPanel metricGrid = new JPanel(new GridLayout(1, 4, 8, 8));
            metricGrid.setOpaque(false);
            metricGrid.add(employeesCard);
            metricGrid.add(grossPayrollCard);
            metricGrid.add(deductionsCard);
            metricGrid.add(netPayrollCard);

            deck.add(contextStrip);
            deck.add(Box.createVerticalStrut(8));
            deck.add(metricGrid);
            deck.add(Box.createVerticalStrut(8));
            deck.add(compositionChart);
            return deck;
        }

        private JPanel buildContextTile(String labelText, JLabel valueLabel) {
            JPanel card = new JPanel(new BorderLayout(0, 6));
            BrandTheme.styleCardSurface(card);

            JLabel label = new JLabel(labelText);
            label.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
            label.setForeground(BrandTheme.MUTED);

            card.add(label, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            return card;
        }

        private JLabel createContextValueLabel() {
            JLabel label = new JLabel("Not available");
            label.setFont(BrandTheme.BODY_FONT.deriveFont(12f));
            label.setForeground(BrandTheme.TEXT);
            return label;
        }

        private void resetPreviewInsights(String statusText) {
            employeesCard.setValue("0");
            employeesCard.setDetail("Run a preview to populate this KPI.");
            grossPayrollCard.setValue(MONEY.format(0));
            grossPayrollCard.setDetail("Attendance-based salary before allowances.");
            deductionsCard.setValue(MONEY.format(0));
            deductionsCard.setDetail("Taxes and government contributions.");
            netPayrollCard.setValue(MONEY.format(0));
            netPayrollCard.setDetail("Projected amount for release.");
            selectedPeriodValue.setText("No period selected");
            coverageWindowValue.setText("Select pay period, month, and year");
            statusValue.setText(statusText);
            compositionChart.clear("Preview payroll to render the chart.");
        }

        private void updatePreviewInsights(PayrollPeriodOption selectedPeriod,
                                           int employeeCount,
                                           double totalBasic,
                                           double totalAllowances,
                                           double totalDeductions,
                                           double totalNet,
                                           boolean alreadyProcessed) {
            employeesCard.setValue(String.valueOf(employeeCount));
            employeesCard.setDetail(employeeCount == 0
                    ? "No employee records are ready for processing."
                    : employeeCount + (employeeCount == 1
                    ? " employee in the selected pay period."
                    : " employees in the selected pay period."));
            grossPayrollCard.setValue(MONEY.format(totalBasic));
            deductionsCard.setValue(MONEY.format(totalDeductions));
            netPayrollCard.setValue(MONEY.format(totalNet));
            selectedPeriodValue.setText(formatPeriodFilterLabel(selectedPeriod));
            coverageWindowValue.setText(PERIOD_DATE.format(selectedPeriod.getStartDate())
                    + " - " + PERIOD_DATE.format(selectedPeriod.getEndDate()));
            if (employeeCount == 0) {
                statusValue.setText("No preview rows found");
            } else if (alreadyProcessed) {
                statusValue.setText("Previously processed");
            } else {
                statusValue.setText("Ready to process");
            }
            compositionChart.setSeries(
                    new String[]{"Gross Salary", "Allowances", "Deductions", "Net Salary"},
                    new double[]{totalBasic, totalAllowances, totalDeductions, totalNet},
                    new Color[]{BrandTheme.GOLD, BrandTheme.PRIMARY_BLUE, BrandTheme.MOTORPH_RED, BrandTheme.TEAL}
            );
        }

        void refreshPreview() {

            PayrollPeriodOption selectedPeriod = periodSelectors.getSelectedPeriod();
            DefaultTableModel model = (DefaultTableModel) previewTable.getModel();
            model.setRowCount(0);

            if (selectedPeriod == null) {
                summaryLabel.setText("No attendance-backed payroll data found for the selected pay period.");
                resetPreviewInsights("No payroll data is available for the selected pay period.");
                return;
            }

            List<EmployeePayrollSummary> summaries = employeePortalService.getPayrollSummariesForPeriod(selectedPeriod.getKey());
            double totalBasic = 0;
            double totalAllowances = 0;
            double totalNet = 0;
            double totalDeductions = 0;
            boolean alreadyProcessed = hasSavedPayrollForPeriod(selectedPeriod);

            for (EmployeePayrollSummary summary : summaries) {
                double allowances = summary.getRiceSubsidy()
                        + summary.getPhoneAllowance()
                        + summary.getClothingAllowance();

                model.addRow(new Object[]{
                    summary.getEmployeeNumber(),
                    summary.getEmployeeName(),
                    MONEY.format(summary.getBasicSalary()),
                    MONEY.format(allowances),
                    MONEY.format(summary.getGrossSalary()),
                    MONEY.format(summary.getTotalDeductions()),
                    MONEY.format(summary.getNetSalary()),
                    summary.getAttendanceDays()
                });

                totalBasic += summary.getBasicSalary();
                totalAllowances += allowances;
                totalNet += summary.getNetSalary();
                totalDeductions += summary.getTotalDeductions();
            }

            updatePreviewInsights(selectedPeriod, summaries.size(), totalBasic, totalAllowances, totalDeductions, totalNet, alreadyProcessed);

            summaryLabel.setText(
                    "Previewing " + summaries.size()
                    + " employees for " + formatPeriod(selectedPeriod)
                    + " | Gross Salary " + MONEY.format(totalBasic)
                    + " + Allowances " + MONEY.format(totalAllowances)
                    + " - Deductions " + MONEY.format(totalDeductions)
                    + " = Net Salary " + MONEY.format(totalNet)
            );
        }

        private void processSelectedPeriod() {

            PayrollPeriodOption selectedPeriod = periodSelectors.getSelectedPeriod();
            if (selectedPeriod == null) {
                JOptionPane.showMessageDialog(this, "Select a pay period, month, and year before processing payroll.");
                return;
            }

            List<Payslip> payslips = employeePortalService.buildPayslipsForPeriod(selectedPeriod.getKey());
            if (payslips.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payroll data is available for the selected pay period.");
                return;
            }

            boolean alreadyProcessed = hasSavedPayrollForPeriod(selectedPeriod);
            if (alreadyProcessed) {
                int decision = JOptionPane.showConfirmDialog(
                        this,
                        "Payroll for this pay period has already been processed. Processing it again will replace the existing saved records. Do you want to continue?",
                        "Payroll Previously Processed",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (decision != JOptionPane.YES_OPTION) {
                    statusValue.setText("Reprocessing cancelled");
                    return;
                }
            }

            payrollRecordService.savePayrolls(payslips);
            notificationService.record(
                    user,
                    "PAYROLL_PROCESSED",
                    "Processed " + payslips.size() + " payroll records for " + formatPeriod(selectedPeriod) + "."
            );
            payrollRecordsPanel.reloadRows();
            refreshPreview();
            statusValue.setText(alreadyProcessed ? "Reprocessed and saved" : "Processed and saved");
            summaryLabel.setText(summaryLabel.getText() + " | Saved " + payslips.size() + (payslips.size() == 1 ? " record." : " records."));
            JOptionPane.showMessageDialog(
                    this,
                    (alreadyProcessed ? "Reprocessed " : "Processed ") + payslips.size()
                            + " payroll records for " + formatPeriod(selectedPeriod) + ".",
                    "Payroll Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private static final class MetricCard extends JPanel {

        private final JLabel valueLabel = new JLabel();
        private final JLabel detailLabel = new JLabel();

        MetricCard(String titleText, String valueText, String detailText, Color accent) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(true);
            setBackground(BrandTheme.PANEL_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                            BorderFactory.createEmptyBorder(14, 14, 14, 14)
                    )
            ));

            JLabel titleLabel = new JLabel(titleText);
            titleLabel.setFont(BrandTheme.BODY_FONT.deriveFont(12f));
            titleLabel.setForeground(BrandTheme.MUTED);
            titleLabel.setAlignmentX(LEFT_ALIGNMENT);

            valueLabel.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 16f));
            valueLabel.setForeground(accent);
            valueLabel.setAlignmentX(LEFT_ALIGNMENT);

            detailLabel.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
            detailLabel.setForeground(BrandTheme.MUTED);
            detailLabel.setAlignmentX(LEFT_ALIGNMENT);

            add(titleLabel);
            add(Box.createVerticalStrut(8));
            add(valueLabel);
            add(Box.createVerticalStrut(6));
            add(detailLabel);

            setValue(valueText);
            setDetail(detailText);
        }

        void setValue(String valueText) {
            valueLabel.setText(valueText);
        }

        void setDetail(String detailText) {
            detailLabel.setText("<html><body style='width:160px'>" + detailText + "</body></html>");
        }
    }

    private static final class AnalyticsChartCard extends JPanel {

        private final HorizontalBarChartPanel chartPanel = new HorizontalBarChartPanel();

        AnalyticsChartCard(String titleText, String subtitleText) {
            setLayout(new BorderLayout(0, 12));
            BrandTheme.styleCardSurface(this);

            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel(titleText);
            titleLabel.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 13f));
            titleLabel.setForeground(BrandTheme.TEXT);

            JLabel subtitleLabel = new JLabel("<html>" + subtitleText + "</html>");
            subtitleLabel.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
            subtitleLabel.setForeground(BrandTheme.MUTED);

            header.add(titleLabel);
            header.add(Box.createVerticalStrut(6));
            header.add(subtitleLabel);

            add(header, BorderLayout.NORTH);
            add(chartPanel, BorderLayout.CENTER);
        }

        void setSeries(String[] labels, double[] values, Color[] colors) {
            chartPanel.setSeries(labels, values, colors);
        }

        void clear(String message) {
            chartPanel.clear(message);
        }
    }

    private static final class HorizontalBarChartPanel extends JPanel {

        private String[] labels = new String[0];
        private double[] values = new double[0];
        private Color[] colors = new Color[0];
        private String emptyMessage = "No chart data available.";

        HorizontalBarChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 140));
            setMinimumSize(new Dimension(0, 140));
        }

        void setSeries(String[] labels, double[] values, Color[] colors) {
            this.labels = labels == null ? new String[0] : labels.clone();
            this.values = values == null ? new double[0] : values.clone();
            this.colors = colors == null ? new Color[0] : colors.clone();
            this.emptyMessage = "No chart data available.";
            repaint();
        }

        void clear(String message) {
            this.labels = new String[0];
            this.values = new double[0];
            this.colors = new Color[0];
            this.emptyMessage = message;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(BrandTheme.BODY_FONT.deriveFont(11f));

            if (values.length == 0) {
                g2.setColor(BrandTheme.MUTED);
                drawCenteredString(g2, emptyMessage, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            double maxValue = 0;
            for (double value : values) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }

            if (maxValue <= 0) {
                g2.setColor(BrandTheme.MUTED);
                drawCenteredString(g2, emptyMessage, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            int width = getWidth();
            int contentWidth = Math.max(140, width - 40);
            int rowHeight = Math.max(30, (getHeight() - 16) / Math.max(1, values.length));
            int barHeight = 8;
            int y = 10;

            for (int index = 0; index < values.length; index++) {
                String label = index < labels.length ? labels[index] : "Series " + (index + 1);
                Color color = index < colors.length ? colors[index] : BrandTheme.CHART_PALETTE[index % BrandTheme.CHART_PALETTE.length];
                String valueText = MONEY.format(values[index]);

                g2.setColor(BrandTheme.MUTED);
                g2.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
                g2.drawString(label, 4, y + 10);

                g2.setColor(BrandTheme.TEXT);
                g2.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
                int valueWidth = g2.getFontMetrics().stringWidth(valueText);
                g2.drawString(valueText, width - valueWidth - 4, y + 10);

                int barY = y + 15;
                g2.setColor(BrandTheme.TABLE_ALT);
                g2.fillRoundRect(4, barY, contentWidth, barHeight, 10, 10);

                int barWidth = (int) Math.round(contentWidth * (values[index] / maxValue));
                if (barWidth > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(4, barY, Math.max(8, barWidth), barHeight, 10, 10);
                }

                y += rowHeight;
            }

            g2.dispose();
        }

        private void drawCenteredString(Graphics2D g2, String text, int width, int height) {
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int x = Math.max(0, (width - textWidth) / 2);
            int y = Math.max(g2.getFontMetrics().getAscent(), height / 2);
            g2.drawString(text, x, y);
        }
    }

    private class PayrollRecordsPanel extends JPanel {

        private final PeriodSelectionControls periodSelectors = new PeriodSelectionControls();
        private final JComboBox<String> filterSelector = new JComboBox<>(new String[]{"Employee Name", "Employee Number"});
        private final JTextField searchField = new JTextField();
        private final JTable recordsTable = new JTable();
        private final JLabel summaryLabel = new JLabel(" ");
        private List<String[]> allRows = new ArrayList<>();
        private List<String[]> visibleRows = new ArrayList<>();

        PayrollRecordsPanel() {
            setLayout(new BorderLayout(0, 14));
            BrandTheme.styleSurface(this);

            JPanel top = new JPanel(new BorderLayout(0, 10));
            top.setOpaque(false);

            JLabel title = new JLabel("Payroll Records");
            title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
            title.setForeground(BrandTheme.TEXT);
            top.add(title, BorderLayout.NORTH);

            JPanel controlsWrapper = new JPanel(new BorderLayout(0, 8));
            controlsWrapper.setOpaque(false);

            JPanel firstRow = new JPanel();
            firstRow.setOpaque(false);
            firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));

            JPanel secondRow = new JPanel();
            secondRow.setOpaque(false);
            secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));

            JButton btnSearch = new JButton("Search");
            JButton btnRefresh = new JButton("Refresh");
            JButton btnView = new JButton("View Payslip");
            BrandTheme.styleSecondaryButton(btnSearch);
            BrandTheme.styleSecondaryButton(btnRefresh);
            BrandTheme.stylePrimaryButton(btnView);
            BrandTheme.styleInputField(searchField);
            BrandTheme.styleComboBox(filterSelector);
            filterSelector.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            filterSelector.setPreferredSize(new Dimension(160, 34));
            searchField.setPreferredSize(new Dimension(180, 34));
            periodSelectors.setOnSelectionChange(this::applyFilter);

            JLabel filterLabel = new JLabel("Filter By:");
            filterLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
            filterLabel.setForeground(BrandTheme.TEXT);

            JLabel searchLabel = new JLabel("Search");
            searchLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
            searchLabel.setForeground(BrandTheme.TEXT);

            JLabel cutoffLabel = new JLabel("Pay Period");
            JLabel monthLabel = new JLabel("Month");
            JLabel yearLabel = new JLabel("Year");
            for (JLabel label : new JLabel[]{cutoffLabel, monthLabel, yearLabel}) {
                label.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
                label.setForeground(BrandTheme.TEXT);
            }

            btnSearch.addActionListener(evt -> applyFilter());
            btnRefresh.addActionListener(evt -> reloadRows());
            btnView.addActionListener(evt -> viewSelectedPayslip());
            searchField.addActionListener(evt -> applyFilter());

            firstRow.add(filterLabel);
            firstRow.add(Box.createHorizontalStrut(8));
            firstRow.add(filterSelector);
            firstRow.add(Box.createHorizontalStrut(12));
            firstRow.add(searchLabel);
            firstRow.add(Box.createHorizontalStrut(8));
            firstRow.add(searchField);
            firstRow.add(Box.createHorizontalStrut(10));
            firstRow.add(btnSearch);

            secondRow.add(cutoffLabel);
            secondRow.add(Box.createHorizontalStrut(8));
            secondRow.add(periodSelectors.getCutoffSelector());
            secondRow.add(Box.createHorizontalStrut(12));
            secondRow.add(monthLabel);
            secondRow.add(Box.createHorizontalStrut(8));
            secondRow.add(periodSelectors.getMonthSelector());
            secondRow.add(Box.createHorizontalStrut(12));
            secondRow.add(yearLabel);
            secondRow.add(Box.createHorizontalStrut(8));
            secondRow.add(periodSelectors.getYearSelector());
            secondRow.add(Box.createHorizontalStrut(10));
            secondRow.add(btnRefresh);
            secondRow.add(Box.createHorizontalStrut(10));
            secondRow.add(btnView);

            controlsWrapper.add(firstRow, BorderLayout.NORTH);
            controlsWrapper.add(secondRow, BorderLayout.CENTER);
            top.add(controlsWrapper, BorderLayout.CENTER);

            recordsTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Employee #", "Employee Name", "Period Start", "Period End", "Gross", "Net"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            BrandTheme.styleTable(recordsTable);

            summaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
            summaryLabel.setForeground(BrandTheme.MUTED);

            add(top, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(recordsTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(summaryLabel, BorderLayout.SOUTH);
        }

        void reloadRows() {
            Set<String> allowedPeriods = buildAllowedPeriodKeys();
            allRows = new ArrayList<>();
            for (String[] row : payrollRecordService.getPayrollHistory()) {
                if (row.length < 14) {
                    continue;
                }

                String key = row[2].trim() + "|" + row[3].trim();
                if (!allowedPeriods.contains(key)) {
                    continue;
                }

                allRows.add(row);
            }
            allRows.sort(Comparator.comparing((String[] row) -> row[3]).reversed()
                    .thenComparing(row -> row[0]));
            applyFilter();
        }

        private void applyFilter() {

            String query = searchField.getText().trim().toLowerCase();
            visibleRows = new ArrayList<>();
            String filterType = String.valueOf(filterSelector.getSelectedItem());
            PayrollPeriodOption selectedPeriod = periodSelectors.getSelectedPeriod();

            for (String[] row : allRows) {
                if (selectedPeriod == null) {
                    continue;
                }
                if (selectedPeriod != null) {
                    if (!row[2].trim().equals(selectedPeriod.getStartDate().toString())
                            || !row[3].trim().equals(selectedPeriod.getEndDate().toString())) {
                        continue;
                    }
                }
                String haystack = "Employee Number".equalsIgnoreCase(filterType)
                        ? row[0].toLowerCase()
                        : row[1].toLowerCase();
                if (query.isEmpty() || haystack.contains(query)) {
                    visibleRows.add(row);
                }
            }

            DefaultTableModel model = (DefaultTableModel) recordsTable.getModel();
            model.setRowCount(0);

            for (String[] row : visibleRows) {
                model.addRow(new Object[]{
                    row[0],
                    row[1],
                    formatStoredDate(row[2]),
                    formatStoredDate(row[3]),
                    MONEY.format(Double.parseDouble(row[8])),
                    MONEY.format(Double.parseDouble(row[13]))
                });
            }

            String periodSummary = selectedPeriod != null
                    ? " for " + formatPeriodFilterLabel(selectedPeriod)
                    : "";
            summaryLabel.setText("Showing " + visibleRows.size() + " saved payroll records" + periodSummary + ".");
        }

        private void viewSelectedPayslip() {

            int rowIndex = recordsTable.getSelectedRow();
            if (rowIndex < 0 || rowIndex >= visibleRows.size()) {
                JOptionPane.showMessageDialog(this, "Select a payroll record first.");
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    new EmployeeViewPayslip(visibleRows.get(rowIndex)),
                    "Payslip View",
                    JOptionPane.PLAIN_MESSAGE
            );
        }
    }
}

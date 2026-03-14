package com.mycompany.motorph.ui;

import com.mycompany.motorph.dao.PayrollDAO;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.model.Payslip;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.service.AppClock;
import com.mycompany.motorph.service.EmployeePortalService;
import com.mycompany.motorph.service.NotificationService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.CardLayout;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PayrollDashboard extends JPanel {

    private static final DecimalFormat MONEY = new DecimalFormat("PHP #,##0.00");
    private static final DateTimeFormatter PERIOD_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");

    private final User user;
    private final EmployeePortalService employeePortalService = new EmployeePortalService();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final NotificationService notificationService = new NotificationService();
    private final int displayDataYear;
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
        this.user = user;
        List<PayrollPeriodOption> sourcedPeriods = employeePortalService.getAvailablePayrollPeriods();
        this.displayDataYear = resolveDisplayDataYear(sourcedPeriods);
        this.availablePeriods = filterPayrollPeriodsByYear(sourcedPeriods, displayDataYear);
        this.processPayrollPanel = new ProcessPayrollPanel();
        this.payrollRecordsPanel = new PayrollRecordsPanel();
        setLayout(new BorderLayout(0, 16));
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildNavigation(), BorderLayout.WEST);
        add(buildMainShell(), BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(BrandTheme.GRAPHITE);
        navigation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BrandTheme.LAVENDER),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));
        navigation.setPreferredSize(new Dimension(228, 0));

        JPanel menuButtons = new JPanel();
        menuButtons.setOpaque(false);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        javax.swing.JLabel menuLabel = new javax.swing.JLabel("Finance Menu");
        menuLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 12f));
        menuLabel.setForeground(BrandTheme.MUTED);
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);

        btnProcessPayroll = createNavigationButton("Process Payroll");
        btnPayrollRecords = createNavigationButton("Payroll Records");
        btnLogout = DashboardNavigation.createLogoutButton(this, user);
        configureNavigationButton(btnLogout);
        btnLogout.setText("Logout");

        btnProcessPayroll.addActionListener(evt -> {
            processPayrollPanel.reloadPeriods();
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
        button.setFont(BrandTheme.BUTTON_FONT);
        button.setMinimumSize(new Dimension(188, 40));
        button.setPreferredSize(new Dimension(188, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JPanel buildMainShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BrandTheme.NAVY);
        shell.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        contentPanel = new JPanel(contentCards);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        contentPanel.add(processPayrollPanel, "process");
        contentPanel.add(payrollRecordsPanel, "records");
        contentCards.show(contentPanel, "process");
        shell.add(contentPanel, BorderLayout.CENTER);
        return shell;
    }

    private void refreshAll() {
        processPayrollPanel.reloadPeriods();
        processPayrollPanel.refreshPreview();
        payrollRecordsPanel.reloadRows();
    }

    public void refreshDashboard() {
        refreshAll();
    }

    private int resolveDisplayDataYear(List<PayrollPeriodOption> periods) {
        if (periods == null || periods.isEmpty()) {
            return AppClock.today().getYear();
        }

        java.util.Map<Integer, Integer> counts = new java.util.LinkedHashMap<>();
        for (PayrollPeriodOption period : periods) {
            counts.merge(period.getEndDate().getYear(), 1, Integer::sum);
        }

        int selectedYear = periods.get(0).getEndDate().getYear();
        int highestCount = -1;
        for (java.util.Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int year = entry.getKey();
            int count = entry.getValue();
            if (count > highestCount || (count == highestCount && year > selectedYear)) {
                highestCount = count;
                selectedYear = year;
            }
        }
        return selectedYear;
    }

    private List<PayrollPeriodOption> filterPayrollPeriodsByYear(List<PayrollPeriodOption> periods, int year) {
        List<PayrollPeriodOption> filtered = new ArrayList<>();
        for (PayrollPeriodOption period : periods) {
            if (period.getEndDate().getYear() == year) {
                filtered.add(period);
            }
        }
        filtered.sort(Comparator.comparing(PayrollPeriodOption::getEndDate).reversed()
                .thenComparing(option -> option.getType() == PayrollPeriodOption.Type.MONTHLY ? 1 : 0));
        return filtered;
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

    private String formatPeriodFilterLabel(PayrollPeriodOption option) {
        if (option == null) {
            return "";
        }
        String monthLabel = MONTH_YEAR.format(option.getEndDate());
        if (option.getType() == PayrollPeriodOption.Type.MONTHLY) {
            return monthLabel + " | Monthly";
        }
        return monthLabel + " | " + option.getStartDate().getDayOfMonth() + "-" + option.getEndDate().getDayOfMonth();
    }

    private static String formatPeriod(PayrollPeriodOption option) {
        return option.getLabel() + " | " + PERIOD_DATE.format(option.getStartDate()) + " - " + PERIOD_DATE.format(option.getEndDate());
    }

    private PayrollPeriodOption findPeriodForDate(LocalDate selectedDate) {
        if (availablePeriods.isEmpty()) {
            return null;
        }

        if (selectedDate == null) {
            return availablePeriods.get(0);
        }

        PayrollPeriodOption monthlyFallback = null;
        for (PayrollPeriodOption option : availablePeriods) {
            if (selectedDate.isBefore(option.getStartDate()) || selectedDate.isAfter(option.getEndDate())) {
                continue;
            }
            if (option.getType() == PayrollPeriodOption.Type.SEMI_MONTHLY) {
                return option;
            }
            monthlyFallback = option;
        }
        return monthlyFallback;
    }

    private LocalDate resolveLatestAvailablePeriodDate() {
        if (availablePeriods.isEmpty()) {
            return AppClock.today();
        }
        return availablePeriods.get(0).getEndDate();
    }

    private void setControlWidth(DatePickerField datePickerField, int width) {
        Dimension size = new Dimension(width, 34);
        datePickerField.setPreferredSize(size);
        datePickerField.setMinimumSize(size);
        datePickerField.setMaximumSize(size);
    }

    private class ProcessPayrollPanel extends JPanel {

        private final DatePickerField periodSelector = new DatePickerField();
        private final JTable previewTable = new JTable();
        private final JLabel summaryLabel = new JLabel(" ");

        ProcessPayrollPanel() {
            setLayout(new BorderLayout(0, 14));
            BrandTheme.styleSurface(this);

            JPanel top = new JPanel(new BorderLayout(12, 12));
            top.setOpaque(false);

            JLabel title = new JLabel("Process Payroll");
            title.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 24f));
            title.setForeground(BrandTheme.TEXT);
            top.add(title, BorderLayout.WEST);

            JPanel controls = new JPanel();
            controls.setOpaque(false);
            controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

            JLabel selectorLabel = new JLabel("Payroll Period");
            selectorLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
            selectorLabel.setForeground(BrandTheme.TEXT);

            JButton btnPreview = new JButton("Preview");
            JButton btnProcess = new JButton("Process & Save");
            BrandTheme.styleSecondaryButton(btnPreview);
            BrandTheme.stylePrimaryButton(btnProcess);
            periodSelector.setDefaultDate(resolveLatestAvailablePeriodDate());
            setControlWidth(periodSelector, 220);
            if (!availablePeriods.isEmpty()) {
                periodSelector.setDate(resolveLatestAvailablePeriodDate());
            }

            btnPreview.addActionListener(evt -> refreshPreview());
            btnProcess.addActionListener(evt -> processSelectedPeriod());

            controls.add(selectorLabel);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(periodSelector);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(btnPreview);
            controls.add(Box.createHorizontalStrut(10));
            controls.add(btnProcess);
            top.add(controls, BorderLayout.EAST);

            previewTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "Employee #", "Employee Name", "Basic", "Allowances", "Gross", "Deductions", "Net", "Days"
            }) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            BrandTheme.styleTable(previewTable);

            summaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(15f));
            summaryLabel.setForeground(BrandTheme.MUTED);

            add(top, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(previewTable);
            BrandTheme.styleScrollPane(scrollPane);
            add(scrollPane, BorderLayout.CENTER);
            add(summaryLabel, BorderLayout.SOUTH);
        }

        void reloadPeriods() {
        }

        void refreshPreview() {

            PayrollPeriodOption selectedPeriod = findPeriodForDate(periodSelector.getDate());
            DefaultTableModel model = (DefaultTableModel) previewTable.getModel();
            model.setRowCount(0);

            if (selectedPeriod == null) {
                summaryLabel.setText("No attendance-backed payroll periods available.");
                return;
            }

            List<EmployeePayrollSummary> summaries = employeePortalService.getPayrollSummariesForPeriod(selectedPeriod.getKey());
            double totalGross = 0;
            double totalNet = 0;
            double totalDeductions = 0;

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

                totalGross += summary.getGrossSalary();
                totalNet += summary.getNetSalary();
                totalDeductions += summary.getTotalDeductions();
            }

            summaryLabel.setText(
                    "Previewing " + summaries.size()
                    + " employees for " + formatPeriod(selectedPeriod)
                    + " | Gross " + MONEY.format(totalGross)
                    + " | Deductions " + MONEY.format(totalDeductions)
                    + " | Net " + MONEY.format(totalNet)
            );
        }

        private void processSelectedPeriod() {

            PayrollPeriodOption selectedPeriod = findPeriodForDate(periodSelector.getDate());
            if (selectedPeriod == null) {
                JOptionPane.showMessageDialog(this, "Select a payroll period first.");
                return;
            }

            List<Payslip> payslips = employeePortalService.buildPayslipsForPeriod(selectedPeriod.getKey());
            if (payslips.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payroll data available for the selected period.");
                return;
            }

            payrollDAO.savePayrolls(payslips);
            notificationService.record(
                    user,
                    "PAYROLL_PROCESSED",
                    "Processed " + payslips.size() + " payroll records for " + formatPeriod(selectedPeriod) + "."
            );
            refreshAll();
            JOptionPane.showMessageDialog(
                    this,
                    "Processed " + payslips.size() + " payroll records for " + formatPeriod(selectedPeriod) + ".",
                    "Payroll Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private class PayrollRecordsPanel extends JPanel {

        private final DatePickerField periodSelector = new DatePickerField();
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
            periodSelector.setDefaultDate(resolveLatestAvailablePeriodDate());
            setControlWidth(periodSelector, 190);
            if (!availablePeriods.isEmpty()) {
                periodSelector.setDate(resolveLatestAvailablePeriodDate());
            }
            filterSelector.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            filterSelector.setPreferredSize(new Dimension(160, 34));
            searchField.setPreferredSize(new Dimension(180, 34));

            JLabel filterLabel = new JLabel("Filter By:");
            filterLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
            filterLabel.setForeground(BrandTheme.TEXT);

            JLabel searchLabel = new JLabel("Search");
            searchLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
            searchLabel.setForeground(BrandTheme.TEXT);

            JLabel periodLabel = new JLabel("Pay Period");
            periodLabel.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f));
            periodLabel.setForeground(BrandTheme.TEXT);

            btnSearch.addActionListener(evt -> applyFilter());
            btnRefresh.addActionListener(evt -> reloadRows());
            btnView.addActionListener(evt -> viewSelectedPayslip());
            searchField.addActionListener(evt -> applyFilter());
            periodSelector.setOnDateChange(this::applyFilter);

            firstRow.add(filterLabel);
            firstRow.add(Box.createHorizontalStrut(8));
            firstRow.add(filterSelector);
            firstRow.add(Box.createHorizontalStrut(12));
            firstRow.add(searchLabel);
            firstRow.add(Box.createHorizontalStrut(8));
            firstRow.add(searchField);
            firstRow.add(Box.createHorizontalStrut(10));
            firstRow.add(btnSearch);

            secondRow.add(periodLabel);
            secondRow.add(Box.createHorizontalStrut(8));
            secondRow.add(periodSelector);
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
            for (String[] row : payrollDAO.getPayrollHistory(0)) {
                if (row.length < 14) {
                    continue;
                }

                String key = row[2].trim() + "|" + row[3].trim();
                if (!allowedPeriods.contains(key)) {
                    continue;
                }

                try {
                    if (LocalDate.parse(row[3].trim()).getYear() != displayDataYear) {
                        continue;
                    }
                } catch (Exception e) {
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
            LocalDate selectedDate = periodSelector.getDate();
            PayrollPeriodOption selectedPeriod = findPeriodForDate(selectedDate);
            boolean specificDateSelected = selectedDate != null;

            for (String[] row : allRows) {
                if (specificDateSelected && selectedPeriod == null) {
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
            summaryLabel.setText("Showing " + visibleRows.size() + " saved payroll records" + periodSummary
                    + " aligned to the " + displayDataYear + " attendance dataset.");
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

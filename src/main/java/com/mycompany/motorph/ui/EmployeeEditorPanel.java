package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeFormData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EmployeeEditorPanel extends JPanel {

    private static final double WORK_DAYS_PER_MONTH = 21.0;
    private static final double HOURS_PER_DAY = 8.0;
    private static final String REQUIRED_MESSAGE = "This field is required.";
    private static final String LETTERS_ONLY_MESSAGE = "Only alphabetic characters are allowed.";
    private static final String NUMBERS_ONLY_MESSAGE = "Please enter numbers only.";
    private static final String SUMMARY_MESSAGE =
            "There were problems with your input. Please correct the highlighted fields.";
    private static final int FIELD_LABEL_WIDTH = 118;
    private static final int FULL_WIDTH_LABEL_WIDTH = 134;
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR_BUFFER = 10;
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DecimalFormat DISPLAY_AMOUNT_FORMAT;
    private static final String[] MONTH_LABELS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private static final String[] DAY_HEADERS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DISPLAY_AMOUNT_FORMAT = new DecimalFormat("0.00", symbols);
        DISPLAY_AMOUNT_FORMAT.setGroupingUsed(false);
    }

    private final HintTextField employeeNumberField =
            new HintTextField(18, "Enter numeric employee ID (e.g., 10001)");
    private final HintTextField firstNameField =
            new HintTextField(18, "Enter first name (letters only)");
    private final HintTextField lastNameField =
            new HintTextField(18, "Enter last name (letters only)");
    private final HintTextField birthDateField =
            new HintTextField(18, "Select date of birth");
    private final HintTextField positionField =
            new HintTextField(18, "Enter position");
    private final HintTextField statusField =
            new HintTextField(18, "Enter employment status");
    private final HintTextField supervisorField =
            new HintTextField(18, "Enter immediate supervisor");
    private final HintTextField addressField =
            new HintTextField(18, "Enter complete address");
    private final HintTextField phoneField =
            new HintTextField(18, "Enter phone number (digits only)");
    private final HintTextField sssField =
            new HintTextField(18, "Enter SSS number");
    private final HintTextField philhealthField =
            new HintTextField(18, "Enter PhilHealth number");
    private final HintTextField tinField =
            new HintTextField(18, "Enter TIN number");
    private final HintTextField pagibigField =
            new HintTextField(18, "Enter Pag-IBIG number");
    private final HintTextField basicSalaryField =
            new HintTextField(18, "Enter monthly salary");
    private final HintTextField riceSubsidyField =
            new HintTextField(18, "Enter subsidy amount");
    private final HintTextField phoneAllowanceField =
            new HintTextField(18, "Enter phone allowance");
    private final HintTextField clothingAllowanceField =
            new HintTextField(18, "Enter clothing allowance");
    private final HintTextField grossSemiMonthlyRateField =
            new HintTextField(18, "Enter computed gross semi-monthly rate");
    private final HintTextField hourlyRateField =
            new HintTextField(18, "Enter hourly rate");

    private final JButton birthDateButton = new JButton("Pick");
    private final JLabel validationSummaryLabel = new JLabel(" ");
    private final List<Employee> existingEmployees;
    private final Integer originalEmployeeNumber;
    private final List<FieldGroup> editableGroups = new ArrayList<>();
    private final CalendarDialog birthDateDialog = new CalendarDialog();
    private FieldGroup birthDateGroup;
    private LocalDate selectedBirthDate;
    private boolean validationActive;

    public EmployeeEditorPanel(Employee employee) {
        this(employee, List.of());
    }

    public EmployeeEditorPanel(Employee employee, List<Employee> existingEmployees) {
        this.existingEmployees = existingEmployees == null ? List.of() : new ArrayList<>(existingEmployees);
        this.originalEmployeeNumber = employee == null ? null : employee.getEmployeeNumber();

        setLayout(new BorderLayout());
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new Dimension(900, 500));
        setMinimumSize(new Dimension(820, 460));

        buildForm();
        configureDatePicker();
        populate(employee);
        attachComputedFieldUpdates();
        attachLiveValidation();
        updateDerivedCompensationFields();
        refreshValidationSummary();
    }

    public EmployeeFormData getFormData() {
        EmployeeFormData data = new EmployeeFormData();
        data.setEmployeeNumber(employeeNumberField.getText().trim());
        data.setFirstName(firstNameField.getText().trim());
        data.setLastName(lastNameField.getText().trim());
        data.setBirthDate(birthDateField.getText().trim());
        data.setPosition(positionField.getText().trim());
        data.setStatus(statusField.getText().trim());
        data.setSupervisor(supervisorField.getText().trim());
        data.setAddress(addressField.getText().trim());
        data.setPhone(normalizeGroupedDigits(phoneField.getText(), 3, 3, 3));
        data.setSss(normalizeGroupedDigits(sssField.getText(), 2, 7, 1));
        data.setPhilhealth(normalizeDigitsOnly(philhealthField.getText()));
        data.setTin(normalizeGroupedDigits(tinField.getText(), 3, 3, 3, 3));
        data.setPagibig(normalizeDigitsOnly(pagibigField.getText()));
        data.setBasicSalary(normalizeAmount(basicSalaryField.getText()));
        data.setRiceSubsidy(normalizeAmount(riceSubsidyField.getText()));
        data.setPhoneAllowance(normalizeAmount(phoneAllowanceField.getText()));
        data.setClothingAllowance(normalizeAmount(clothingAllowanceField.getText()));
        return data;
    }

    public boolean validateForm() {
        validationActive = true;
        FieldGroup firstInvalid = null;

        for (FieldGroup group : editableGroups) {
            group.markTouched();
            String message = group.validateValue(true);
            group.applyError(message);
            if (message != null && firstInvalid == null) {
                firstInvalid = group;
            }
        }

        refreshValidationSummary();
        if (firstInvalid != null) {
            firstInvalid.focusInput();
            return false;
        }
        return true;
    }

    private String resolveFieldValidation(FieldGroup group) {
        return group.validateValue(validationActive);
    }

    private void buildForm() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        validationSummaryLabel.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 11f));
        validationSummaryLabel.setForeground(BrandTheme.MOTORPH_RED);
        validationSummaryLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        validationSummaryLabel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        content.add(validationSummaryLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 10, 8);
        content.add(buildSectionCard("Employee Information", buildSectionGrid(
                createRequiredTextGroup("Employee Number", employeeNumberField, this::validateEmployeeNumber),
                createRequiredTextGroup("First Name", firstNameField, this::validatePersonName),
                createRequiredTextGroup("Last Name", lastNameField, this::validatePersonName),
                createRequiredDateGroup("Birth Date", birthDateField, birthDateButton, this::validateBirthDate)
        )), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 10, 0);
        content.add(buildSectionCard("Work Information", buildSectionGrid(
                createRequiredTextGroup("Position", positionField, this::validatePosition),
                createRequiredTextGroup("Status", statusField, this::validateStatus),
                createRequiredFullWidthTextGroup("Immediate Supervisor", supervisorField, this::validateSupervisor)
        )), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 8);
        content.add(buildSectionCard("Contact Information", buildSectionGrid(
                createRequiredTextGroup("Address", addressField, null),
                createRequiredTextGroup("Phone Number", phoneField, value -> validateDigitsOnly(value, 9))
        )), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 10, 0);
        content.add(buildSectionCard("Government Information", buildSectionGrid(
                createRequiredTextGroup("SSS #", sssField, value -> validateDigitsOnly(value, 10)),
                createRequiredTextGroup("PhilHealth #", philhealthField, value -> validateDigitsOnly(value, 12)),
                createRequiredTextGroup("TIN #", tinField, value -> validateDigitsOnly(value, 12)),
                createRequiredTextGroup("Pag-IBIG #", pagibigField, value -> validateDigitsOnly(value, 12))
        )), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);
        content.add(buildSectionCard("Compensation Information", buildSectionGrid(
                createRequiredTextGroup("Basic Salary", basicSalaryField, this::validateAmount),
                createReadOnlyGroup("Gross Semi-monthly Rate", grossSemiMonthlyRateField),
                createRequiredTextGroup("Rice Subsidy", riceSubsidyField, this::validateAmount),
                createRequiredTextGroup("Phone Allowance", phoneAllowanceField, this::validateAmount),
                createRequiredTextGroup("Clothing Allowance", clothingAllowanceField, this::validateAmount),
                createReadOnlyGroup("Hourly Rate", hourlyRateField)
        )), gbc);

        JPanel filler = new JPanel();
        filler.setOpaque(false);
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(filler, gbc);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildSectionCard(String titleText, JPanel body) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setOpaque(true);
        card.setBackground(BrandTheme.PANEL_BG);
        card.setForeground(BrandTheme.TEXT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12f));
        title.setForeground(BrandTheme.PRIMARY_BLUE);

        card.add(title, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSectionGrid(FieldGroup... groups) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        for (int index = 0; index < groups.length; index++) {
            FieldGroup group = groups[index];
            gbc.gridx = group.fullWidth ? 0 : index % 2;
            gbc.gridwidth = group.fullWidth ? 2 : 1;
            gbc.insets = new Insets(0, gbc.gridx == 0 ? 0 : 8, 6, gbc.gridx == 0 && gbc.gridwidth == 1 ? 8 : 0);
            grid.add(group, gbc);
            if (group.fullWidth || gbc.gridx == 1) {
                gbc.gridy++;
            }
        }

        return grid;
    }

    private FieldGroup createRequiredTextGroup(String labelText,
                                               HintTextField field,
                                               FieldValidator validator) {
        FieldGroup group = new FieldGroup(labelText, field, true, validator, false, false, null);
        editableGroups.add(group);
        return group;
    }

    private FieldGroup createRequiredFullWidthTextGroup(String labelText,
                                                        HintTextField field,
                                                        FieldValidator validator) {
        FieldGroup group = new FieldGroup(labelText, field, true, validator, false, true, null);
        editableGroups.add(group);
        return group;
    }

    private FieldGroup createRequiredDateGroup(String labelText,
                                               HintTextField field,
                                               JButton button,
                                               FieldValidator validator) {
        birthDateGroup = new FieldGroup(labelText, field, true, validator, false, false, button);
        editableGroups.add(birthDateGroup);
        return birthDateGroup;
    }

    private FieldGroup createReadOnlyGroup(String labelText, HintTextField field) {
        return new FieldGroup(labelText, field, false, null, true, false, null);
    }

    private void configureDatePicker() {
        birthDateField.setEditable(false);
        birthDateField.setFocusable(true);
        birthDateField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        birthDateField.setToolTipText("Use the calendar picker to select a date.");

        stylePickerButton(birthDateButton);
        birthDateButton.addActionListener(evt -> showBirthDatePicker());
        birthDateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showBirthDatePicker();
            }
        });
    }

    private void stylePickerButton(JButton button) {
        button.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 11f));
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(BrandTheme.SKY);
        button.setForeground(BrandTheme.PRIMARY_BLUE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        button.setPreferredSize(new Dimension(54, 28));
    }

    private void showBirthDatePicker() {
        birthDateDialog.showDialog(birthDateButton);
    }

    private void attachComputedFieldUpdates() {
        basicSalaryField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDerivedCompensationFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDerivedCompensationFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDerivedCompensationFields();
            }
        });
    }

    private void attachLiveValidation() {
        for (FieldGroup group : editableGroups) {
            group.field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    handleValidationUpdate(group);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    handleValidationUpdate(group);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    handleValidationUpdate(group);
                }
            });

            group.field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    group.markTouched();
                    group.applyError(resolveFieldValidation(group));
                    refreshValidationSummary();
                }
            });
        }
    }

    private void handleValidationUpdate(FieldGroup group) {
        if (!validationActive && !group.isTouched()) {
            return;
        }
        group.applyError(resolveFieldValidation(group));
        refreshValidationSummary();
    }

    private void updateDerivedCompensationFields() {
        String basicSalaryText = basicSalaryField.getText().trim();
        if (basicSalaryText.isEmpty()) {
            clearComputedFields();
            return;
        }

        String validationMessage = validateAmount(basicSalaryText);
        if (validationMessage != null) {
            clearComputedFields();
            return;
        }

        double basicSalary = parseAmountValue(basicSalaryText);
        grossSemiMonthlyRateField.setText(formatAmount(round(basicSalary / 2.0)));
        hourlyRateField.setText(formatAmount(round(basicSalary / WORK_DAYS_PER_MONTH / HOURS_PER_DAY)));
    }

    private void refreshValidationSummary() {
        for (FieldGroup group : editableGroups) {
            if (group.hasError()) {
                validationSummaryLabel.setText(SUMMARY_MESSAGE);
                validationSummaryLabel.setVisible(true);
                return;
            }
        }

        validationSummaryLabel.setText(" ");
        validationSummaryLabel.setVisible(false);
    }

    private String validateEmployeeNumber(String value) {
        String digitsMessage = validateDigitsOnly(value, -1);
        if (digitsMessage != null) {
            return digitsMessage;
        }

        try {
            int employeeNumber = Integer.parseInt(value);
            for (Employee employee : existingEmployees) {
                if (employee.getEmployeeNumber() != employeeNumber) {
                    continue;
                }
                if (originalEmployeeNumber != null && employeeNumber == originalEmployeeNumber) {
                    return null;
                }
                return "Employee Number already exists.";
            }
            return null;
        } catch (NumberFormatException ex) {
            return NUMBERS_ONLY_MESSAGE;
        }
    }

    private String validatePersonName(String value) {
        return matchesAllowedText(value, false, false, false)
                ? null
                : LETTERS_ONLY_MESSAGE;
    }

    private String validatePosition(String value) {
        return matchesAllowedText(value, false, true, false)
                ? null
                : LETTERS_ONLY_MESSAGE;
    }

    private String validateSupervisor(String value) {
        if ("N/A".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return matchesAllowedText(value, true, false, false)
                ? null
                : LETTERS_ONLY_MESSAGE;
    }

    private String validateStatus(String value) {
        return matchesAllowedText(value, false, false, false)
                ? null
                : LETTERS_ONLY_MESSAGE;
    }

    private String validateBirthDate(String value) {
        if (value == null || value.isBlank()) {
            return REQUIRED_MESSAGE;
        }

        try {
            LocalDate parsedDate = LocalDate.parse(value.trim(), BIRTH_DATE_FORMATTER);
            selectedBirthDate = parsedDate;
            return null;
        } catch (DateTimeParseException ex) {
            return "Please select a valid date.";
        }
    }

    private String validateDigitsOnly(String value, int expectedDigits) {
        if (!isDigitsOnly(value)) {
            return NUMBERS_ONLY_MESSAGE;
        }
        if (expectedDigits > 0 && value.length() != expectedDigits) {
            return "Please enter " + expectedDigits + " digits.";
        }
        return null;
    }

    private String validateAmount(String value) {
        if (value == null || value.isBlank()) {
            return REQUIRED_MESSAGE;
        }
        int decimalPoints = 0;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isDigit(current)) {
                continue;
            }
            if (current == '.') {
                decimalPoints++;
                if (decimalPoints <= 1) {
                    continue;
                }
            }
            return NUMBERS_ONLY_MESSAGE;
        }

        try {
            double amount = Double.parseDouble(value);
            if (amount < 0) {
                return NUMBERS_ONLY_MESSAGE;
            }
            return null;
        } catch (NumberFormatException ex) {
            return NUMBERS_ONLY_MESSAGE;
        }
    }

    private boolean matchesAllowedText(String value,
                                       boolean allowComma,
                                       boolean allowAmpersand,
                                       boolean allowSlash) {
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isLetter(current) || Character.isWhitespace(current)) {
                continue;
            }
            if (allowComma && current == ',') {
                continue;
            }
            if (allowAmpersand && current == '&') {
                continue;
            }
            if (allowSlash && current == '/') {
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean isDigitsOnly(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private String normalizeDigitsOnly(String value) {
        StringBuilder digits = new StringBuilder();
        if (value == null) {
            return "";
        }
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isDigit(current)) {
                digits.append(current);
            }
        }
        return digits.toString();
    }

    private String normalizeGroupedDigits(String value, int... groups) {
        String digits = normalizeDigitsOnly(value);
        int expectedDigits = 0;
        for (int groupSize : groups) {
            expectedDigits += groupSize;
        }

        if (digits.length() != expectedDigits) {
            return value == null ? "" : value.trim();
        }

        StringBuilder formatted = new StringBuilder();
        int offset = 0;
        for (int index = 0; index < groups.length; index++) {
            if (index > 0) {
                formatted.append("-");
            }
            int groupSize = groups[index];
            formatted.append(digits, offset, offset + groupSize);
            offset += groupSize;
        }
        return formatted.toString();
    }

    private String normalizeAmount(String value) {
        return value == null ? "" : value.trim().replace(",", "");
    }

    private double parseAmountValue(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String formatAmount(double value) {
        return DISPLAY_AMOUNT_FORMAT.format(value);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void populate(Employee employee) {
        if (employee == null) {
            selectedBirthDate = null;
            clearComputedFields();
            return;
        }

        employeeNumberField.setText(String.valueOf(employee.getEmployeeNumber()));
        firstNameField.setText(resolveEditableValue(employee.getFirstName()));
        lastNameField.setText(resolveEditableValue(employee.getLastName()));
        setBirthDateValue(resolveEditableValue(employee.getBirthDate()));
        positionField.setText(resolveEditableValue(employee.getPosition()));
        statusField.setText(resolveEditableValue(employee.getStatus()));
        supervisorField.setText(resolveEditableValue(employee.getSupervisor()));
        addressField.setText(resolveEditableValue(employee.getAddress()));
        phoneField.setText(normalizeDigitsOnly(employee.getPhone()));
        sssField.setText(normalizeDigitsOnly(employee.getSss()));
        philhealthField.setText(normalizeDigitsOnly(employee.getPhilhealth()));
        tinField.setText(normalizeDigitsOnly(employee.getTin()));
        pagibigField.setText(normalizeDigitsOnly(employee.getPagibig()));
        basicSalaryField.setText(formatAmount(employee.getBasicSalary()));
        riceSubsidyField.setText(formatAmount(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(formatAmount(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(formatAmount(employee.getClothingAllowance()));
        grossSemiMonthlyRateField.setText(formatAmount(employee.getGrossSemiMonthlyRate()));
        hourlyRateField.setText(formatAmount(employee.getHourlyRate()));
    }

    private void setBirthDateValue(String value) {
        if (value == null || value.isBlank()) {
            selectedBirthDate = null;
            birthDateField.setText("");
            return;
        }

        birthDateField.setText(value.trim());
        try {
            selectedBirthDate = LocalDate.parse(value.trim(), BIRTH_DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            selectedBirthDate = null;
        }
    }

    private void clearComputedFields() {
        grossSemiMonthlyRateField.setText("");
        hourlyRateField.setText("");
    }

    private String resolveEditableValue(String value) {
        return value == null ? "" : value.trim();
    }

    private Color placeholderColor() {
        return new Color(
                BrandTheme.MUTED.getRed(),
                BrandTheme.MUTED.getGreen(),
                BrandTheme.MUTED.getBlue(),
                170
        );
    }

    private javax.swing.border.Border createFieldBorder(Color borderColor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private interface FieldValidator {
        String validate(String value);
    }

    private final class FieldGroup extends JPanel {

        private final HintTextField field;
        private final JLabel errorLabel = new JLabel(" ");
        private final boolean required;
        private final FieldValidator validator;
        private final boolean readOnly;
        private final boolean fullWidth;
        private final JButton accessoryButton;
        private boolean touched;

        private FieldGroup(String labelText,
                           HintTextField field,
                           boolean required,
                           FieldValidator validator,
                           boolean readOnly,
                           boolean fullWidth,
                           JButton accessoryButton) {

            this.field = field;
            this.required = required;
            this.validator = validator;
            this.readOnly = readOnly;
            this.fullWidth = fullWidth;
            this.accessoryButton = accessoryButton;

            setOpaque(false);
            setLayout(new BorderLayout(0, 2));

            JLabel label = new JLabel(labelText);
            label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 10f));
            label.setForeground(BrandTheme.MUTED);
            label.setPreferredSize(new Dimension(fullWidth ? FULL_WIDTH_LABEL_WIDTH : FIELD_LABEL_WIDTH, 26));

            BrandTheme.styleInputField(field);
            field.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            field.setPreferredSize(new Dimension(320, 28));
            field.setMinimumSize(new Dimension(180, 28));

            errorLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 10f));
            errorLabel.setForeground(BrandTheme.MOTORPH_RED);
            errorLabel.setVisible(false);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(
                    0,
                    (fullWidth ? FULL_WIDTH_LABEL_WIDTH : FIELD_LABEL_WIDTH) + 8,
                    0,
                    0
            ));

            if (readOnly) {
                field.setEditable(false);
                field.setFocusable(false);
                field.setBackground(BrandTheme.TABLE_ALT);
                field.setForeground(BrandTheme.MUTED);
            }

            JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
            rowPanel.setOpaque(false);
            rowPanel.add(label, BorderLayout.WEST);

            JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
            inputPanel.setOpaque(false);
            inputPanel.add(field, BorderLayout.CENTER);
            if (accessoryButton != null) {
                inputPanel.add(accessoryButton, BorderLayout.EAST);
            }
            rowPanel.add(inputPanel, BorderLayout.CENTER);

            add(rowPanel, BorderLayout.CENTER);
            add(errorLabel, BorderLayout.SOUTH);
        }

        private String validateValue(boolean enforceRequired) {
            String value = field.getText() == null ? "" : field.getText().trim();
            if (required && value.isEmpty()) {
                if (!enforceRequired) {
                    return null;
                }
                return REQUIRED_MESSAGE;
            }
            if (!value.isEmpty() && validator != null) {
                return validator.validate(value);
            }
            return null;
        }

        private void applyError(String message) {
            if (message == null) {
                errorLabel.setText(" ");
                errorLabel.setVisible(false);
                field.setBorder(createFieldBorder(BrandTheme.BORDER));
                if (readOnly) {
                    field.setBackground(BrandTheme.TABLE_ALT);
                    field.setForeground(BrandTheme.MUTED);
                } else {
                    field.setBackground(BrandTheme.INPUT_BG);
                    field.setForeground(BrandTheme.TEXT);
                }
                return;
            }

            errorLabel.setText(message);
            errorLabel.setVisible(true);
            field.setBorder(createFieldBorder(BrandTheme.MOTORPH_RED));
        }

        private boolean hasError() {
            return errorLabel.isVisible() && errorLabel.getText() != null && !errorLabel.getText().isBlank();
        }

        private void markTouched() {
            touched = true;
        }

        private boolean isTouched() {
            return touched;
        }

        private void focusInput() {
            if (accessoryButton != null && !field.isEditable()) {
                accessoryButton.requestFocusInWindow();
                return;
            }
            field.requestFocusInWindow();
        }
    }

    private final class CalendarDialog {

        private final JComboBox<String> monthCombo = new JComboBox<>(MONTH_LABELS);
        private final JComboBox<Integer> yearCombo = new JComboBox<>();
        private final JButton previousButton = new JButton("<");
        private final JButton nextButton = new JButton(">");
        private final JButton[] dayButtons = new JButton[42];
        private final JPanel rootPanel = new JPanel(new BorderLayout(0, 8));
        private YearMonth displayedMonth = YearMonth.now();
        private boolean syncingControls;
        private JDialog dialog;

        private CalendarDialog() {
            rootPanel.setOpaque(true);
            rootPanel.setBackground(BrandTheme.PANEL_BG);
            rootPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JPanel header = new JPanel(new BorderLayout(6, 0));
            header.setOpaque(false);

            styleCalendarButton(previousButton);
            styleCalendarButton(nextButton);
            BrandTheme.styleComboBox(monthCombo);
            BrandTheme.styleComboBox(yearCombo);
            monthCombo.setFont(BrandTheme.BODY_FONT.deriveFont(12f));
            yearCombo.setFont(BrandTheme.BODY_FONT.deriveFont(12f));

            int maxYear = LocalDate.now().getYear() + MAX_YEAR_BUFFER;
            for (int year = MIN_YEAR; year <= maxYear; year++) {
                yearCombo.addItem(year);
            }

            previousButton.addActionListener(evt -> setDisplayedMonth(displayedMonth.minusMonths(1)));
            nextButton.addActionListener(evt -> setDisplayedMonth(displayedMonth.plusMonths(1)));
            monthCombo.addActionListener(evt -> updateDisplayedMonthFromSelectors());
            yearCombo.addActionListener(evt -> updateDisplayedMonthFromSelectors());

            JPanel selectorPanel = new JPanel(new GridLayout(1, 2, 6, 0));
            selectorPanel.setOpaque(false);
            selectorPanel.add(monthCombo);
            selectorPanel.add(yearCombo);

            header.add(previousButton, BorderLayout.WEST);
            header.add(selectorPanel, BorderLayout.CENTER);
            header.add(nextButton, BorderLayout.EAST);

            JPanel daysPanel = new JPanel(new GridLayout(7, 7, 4, 4));
            daysPanel.setOpaque(false);

            for (String headerLabel : DAY_HEADERS) {
                JLabel dayLabel = new JLabel(headerLabel, SwingConstants.CENTER);
                dayLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 10f));
                dayLabel.setForeground(BrandTheme.MUTED);
                daysPanel.add(dayLabel);
            }

            for (int index = 0; index < dayButtons.length; index++) {
                JButton button = new JButton();
                button.setFont(BrandTheme.BODY_FONT.deriveFont(11f));
                button.setFocusable(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setMargin(new Insets(4, 0, 4, 0));
                button.setBorder(BorderFactory.createLineBorder(BrandTheme.BORDER, 1));
                button.setBackground(BrandTheme.PANEL_BG);
                final int buttonIndex = index;
                button.addActionListener(evt -> selectDay(buttonIndex));
                dayButtons[index] = button;
                daysPanel.add(button);
            }

            rootPanel.add(header, BorderLayout.NORTH);
            rootPanel.add(daysPanel, BorderLayout.CENTER);
        }

        private void showDialog(Component invoker) {
            ensureDialog(invoker);
            setDisplayedMonth(selectedBirthDate == null
                    ? YearMonth.now()
                    : YearMonth.from(selectedBirthDate));
            dialog.pack();
            if (invoker != null && invoker.isShowing()) {
                Point location = invoker.getLocationOnScreen();
                dialog.setLocation(location.x, location.y + invoker.getHeight() + 6);
            } else {
                dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(EmployeeEditorPanel.this));
            }
            dialog.setVisible(true);
        }

        private void ensureDialog(Component invoker) {
            Window owner = invoker == null ? null : SwingUtilities.getWindowAncestor(invoker);
            if (dialog != null && dialog.getOwner() != owner) {
                dialog.dispose();
                dialog = null;
            }
            if (dialog != null) {
                return;
            }

            dialog = new JDialog(owner, "Select Birth Date", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.setContentPane(rootPanel);
        }

        private void setDisplayedMonth(YearMonth yearMonth) {
            displayedMonth = yearMonth;
            syncingControls = true;
            monthCombo.setSelectedIndex(displayedMonth.getMonthValue() - 1);
            yearCombo.setSelectedItem(displayedMonth.getYear());
            syncingControls = false;
            refreshCalendar();
        }

        private void updateDisplayedMonthFromSelectors() {
            if (syncingControls) {
                return;
            }

            Integer selectedYear = (Integer) yearCombo.getSelectedItem();
            if (selectedYear == null) {
                return;
            }

            displayedMonth = YearMonth.of(selectedYear, monthCombo.getSelectedIndex() + 1);
            refreshCalendar();
        }

        private void refreshCalendar() {
            LocalDate firstDay = displayedMonth.atDay(1);
            int firstColumn = firstDay.getDayOfWeek().getValue() % 7;
            int lengthOfMonth = displayedMonth.lengthOfMonth();
            LocalDate today = LocalDate.now();

            for (int index = 0; index < dayButtons.length; index++) {
                JButton button = dayButtons[index];
                int dayNumber = index - firstColumn + 1;
                if (dayNumber < 1 || dayNumber > lengthOfMonth) {
                    button.setText("");
                    button.setEnabled(false);
                    button.setBackground(BrandTheme.PANEL_BG);
                    button.setForeground(BrandTheme.TEXT);
                    continue;
                }

                LocalDate currentDate = displayedMonth.atDay(dayNumber);
                button.setText(String.valueOf(dayNumber));
                button.setEnabled(true);

                if (selectedBirthDate != null && selectedBirthDate.equals(currentDate)) {
                    button.setBackground(BrandTheme.SKY);
                    button.setForeground(BrandTheme.PRIMARY_BLUE);
                } else if (today.equals(currentDate)) {
                    button.setBackground(new Color(BrandTheme.GOLD.getRed(), BrandTheme.GOLD.getGreen(), BrandTheme.GOLD.getBlue(), 70));
                    button.setForeground(BrandTheme.TEXT);
                } else {
                    button.setBackground(BrandTheme.PANEL_BG);
                    button.setForeground(BrandTheme.TEXT);
                }
            }
        }

        private void selectDay(int buttonIndex) {
            String buttonText = dayButtons[buttonIndex].getText();
            if (buttonText == null || buttonText.isBlank()) {
                return;
            }

            int day = Integer.parseInt(buttonText);
            selectedBirthDate = displayedMonth.atDay(day);
            birthDateField.setText(BIRTH_DATE_FORMATTER.format(selectedBirthDate));
            if (birthDateGroup != null) {
                birthDateGroup.markTouched();
                birthDateGroup.applyError(resolveFieldValidation(birthDateGroup));
                refreshValidationSummary();
            }
            if (dialog != null) {
                dialog.setVisible(false);
            }
        }

        private void styleCalendarButton(JButton button) {
            button.setFont(BrandTheme.BODY_FONT.deriveFont(Font.BOLD, 11f));
            button.setFocusable(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBackground(BrandTheme.PANEL_BG);
            button.setForeground(BrandTheme.PRIMARY_BLUE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
        }
    }

    private final class HintTextField extends JTextField {

        private final String placeholder;

        private HintTextField(int columns, String placeholder) {
            super(columns);
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (!getText().isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(getFont().deriveFont(Font.ITALIC, getFont().getSize2D()));
            g2.setColor(placeholderColor());

            Insets insets = getInsets();
            int baseline = insets.top + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, insets.left, baseline);
            g2.dispose();
        }
    }
}

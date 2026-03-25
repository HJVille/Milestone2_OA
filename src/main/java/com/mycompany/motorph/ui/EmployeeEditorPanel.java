package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeFormData;
import com.mycompany.motorph.service.EmployeeValidationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class EmployeeEditorPanel extends JPanel {

    private static final String REQUIRED_MESSAGE = "This field is required.";
    private static final String LETTERS_ONLY_MESSAGE = "Only alphabetic characters are allowed.";
    private static final String NUMBERS_ONLY_MESSAGE = "Please enter numbers only.";
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR_BUFFER = 10;
    private static final int SECTION_COLUMN_GAP = 14;
    private static final int SECTION_ROW_GAP = 12;
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
            new HintTextField(18, "00-0000000-0");
    private final HintTextField philhealthField =
            new HintTextField(18, "00-000000000-0");
    private final HintTextField tinField =
            new HintTextField(18, "000-000-000-000");
    private final HintTextField pagibigField =
            new HintTextField(18, "0000-0000-0000");
    private final HintTextField basicSalaryField =
            new HintTextField(18, "0.00");
    private final HintTextField riceSubsidyField =
            new HintTextField(18, "0.00");
    private final HintTextField phoneAllowanceField =
            new HintTextField(18, "0.00");
    private final HintTextField clothingAllowanceField =
            new HintTextField(18, "0.00");
    private final HintTextField grossSemiMonthlyRateField =
            new HintTextField(18, "0.00");
    private final HintTextField hourlyRateField =
            new HintTextField(18, "0.00");

    private final JButton birthDateButton = new JButton("Pick");
    private final List<Employee> existingEmployees;
    private final Integer originalEmployeeNumber;
    private final EmployeeValidationService validationService = new EmployeeValidationService();
    private final List<FieldGroup> editableGroups = new ArrayList<>();
    private final Map<HintTextField, FieldGroup> fieldGroupByField = new HashMap<>();
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
        setPreferredSize(new Dimension(980, 960));
        setMinimumSize(new Dimension(900, 760));

        buildForm();
        configureDatePicker();
        attachInputFilters();
        populate(employee);
        attachComputedFieldUpdates();
        attachAmountFormatting();
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
        data.setPhilhealth(normalizeGroupedDigits(philhealthField.getText(), 2, 9, 1));
        data.setTin(normalizeGroupedDigits(tinField.getText(), 3, 3, 3, 3));
        data.setPagibig(normalizeGroupedDigits(pagibigField.getText(), 4, 4, 4));
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
        JPanel sheet = new JPanel();
        sheet.setOpaque(true);
        sheet.setBackground(BrandTheme.PAPER);
        sheet.setLayout(new BoxLayout(sheet, BoxLayout.Y_AXIS));
        sheet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        JLabel titleLabel = new JLabel("Employee Record Form");
        titleLabel.setFont(BrandTheme.TITLE_FONT.deriveFont(Font.BOLD, 20f));
        titleLabel.setForeground(BrandTheme.TEXT_DARK);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Complete the required employee details below.");
        subtitleLabel.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
        subtitleLabel.setForeground(BrandTheme.MUTED);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        sheet.add(titleLabel);
        sheet.add(Box.createVerticalStrut(6));
        sheet.add(subtitleLabel);
        sheet.add(Box.createVerticalStrut(18));
        sheet.add(buildSectionCard("Employee Information", buildSectionGrid(
                createRequiredTextGroup("Employee Number", employeeNumberField, this::validateEmployeeNumber),
                createRequiredTextGroup("First Name", firstNameField, this::validatePersonName),
                createRequiredTextGroup("Last Name", lastNameField, this::validatePersonName),
                createRequiredDateGroup("Birth Date", birthDateField, birthDateButton, this::validateBirthDate)
        )));
        sheet.add(Box.createVerticalStrut(16));
        sheet.add(buildSectionCard("Work Information", buildSectionGrid(
                createRequiredTextGroup("Position", positionField, this::validatePosition),
                createRequiredTextGroup("Status", statusField, this::validateStatus),
                createRequiredFullWidthTextGroup("Immediate Supervisor", supervisorField, this::validateSupervisor)
        )));
        sheet.add(Box.createVerticalStrut(16));
        sheet.add(buildSectionCard("Contact Information", buildSectionGrid(
                createRequiredFullWidthTextGroup("Address", addressField, null),
                createRequiredTextGroup("Phone Number", phoneField, value -> validateDigitsOnly(value, 9))
        )));
        sheet.add(Box.createVerticalStrut(16));
        sheet.add(buildSectionCard("Government Information", buildGovernmentInformationBody()));
        sheet.add(Box.createVerticalStrut(16));
        sheet.add(buildSectionCard("Compensation Information", buildSectionGrid(
                createCurrencyTextGroup("Basic Salary", basicSalaryField, this::validateAmount),
                createReadOnlyCurrencyGroup("Gross Semi-monthly Rate", grossSemiMonthlyRateField),
                createCurrencyTextGroup("Rice Subsidy", riceSubsidyField, this::validateAmount),
                createCurrencyTextGroup("Phone Allowance", phoneAllowanceField, this::validateAmount),
                createCurrencyTextGroup("Clothing Allowance", clothingAllowanceField, this::validateAmount),
                createReadOnlyCurrencyGroup("Hourly Rate", hourlyRateField)
        )));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(sheet, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        Dimension contentSize = sheet.getPreferredSize();
        setPreferredSize(new Dimension(Math.max(980, contentSize.width + 24), Math.max(960, contentSize.height + 24)));
    }

    private JPanel buildGovernmentInformationBody() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.add(buildSectionGrid(
                createGovernmentTextGroup("SSS #", sssField, this::validateSss),
                createGovernmentTextGroup("PhilHealth #", philhealthField, this::validatePhilhealth),
                createGovernmentTextGroup("TIN #", tinField, this::validateTin),
                createGovernmentTextGroup("Pag-IBIG #", pagibigField, this::validatePagibig)
        ));
        return container;
    }

    private JPanel buildSectionCard(String titleText, JPanel body) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setOpaque(false);
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BrandTheme.PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel(titleText);
        title.setFont(BrandTheme.BUTTON_FONT.deriveFont(Font.BOLD, 12.5f));
        title.setForeground(BrandTheme.TEXT_INVERSE);
        header.add(title, BorderLayout.WEST);

        JPanel bodyShell = new JPanel(new BorderLayout());
        bodyShell.setOpaque(true);
        bodyShell.setBackground(BrandTheme.PAPER);
        bodyShell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 14, 8, 14)
        ));
        bodyShell.add(body, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(bodyShell, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSectionGrid(FieldGroup... groups) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int column = 0;
        for (FieldGroup group : groups) {
            if (group.fullWidth) {
                gbc.gridx = 0;
                gbc.gridwidth = 2;
                gbc.insets = new Insets(0, 0, SECTION_ROW_GAP, 0);
                grid.add(group, gbc);
                gbc.gridy++;
                column = 0;
                continue;
            }

            gbc.gridx = column;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(
                    0,
                    column == 0 ? 0 : SECTION_COLUMN_GAP / 2,
                    SECTION_ROW_GAP,
                    column == 0 ? SECTION_COLUMN_GAP / 2 : 0
            );
            grid.add(group, gbc);

            if (column == 1) {
                gbc.gridy++;
                column = 0;
            } else {
                column = 1;
            }
        }

        if (column == 1) {
            gbc.gridx = 1;
            gbc.gridwidth = 1;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, SECTION_COLUMN_GAP / 2, SECTION_ROW_GAP, 0);
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            grid.add(filler, gbc);
        }

        return grid;
    }

    private FieldGroup createRequiredTextGroup(String labelText,
                                               HintTextField field,
                                               FieldValidator validator) {
        FieldGroup group = new FieldGroup(labelText, field, true, validator, false, false, null);
        return registerEditableGroup(group);
    }

    private FieldGroup createRequiredFullWidthTextGroup(String labelText,
                                                        HintTextField field,
                                                        FieldValidator validator) {
        FieldGroup group = new FieldGroup(labelText, field, true, validator, false, true, null);
        return registerEditableGroup(group);
    }

    private FieldGroup createRequiredDateGroup(String labelText,
                                               HintTextField field,
                                               JButton button,
                                               FieldValidator validator) {
        birthDateGroup = new FieldGroup(labelText, field, true, validator, false, false, button);
        return registerEditableGroup(birthDateGroup);
    }

    private FieldGroup createReadOnlyGroup(String labelText, HintTextField field) {
        return new FieldGroup(labelText, field, false, null, true, false, null);
    }

    private FieldGroup createCurrencyTextGroup(String labelText,
                                               HintTextField field,
                                               FieldValidator validator) {
        FieldGroup group = new FieldGroup(labelText, field, true, validator, false, false, null);
        group.setPrefixText("PHP");
        return registerEditableGroup(group);
    }

    private FieldGroup createGovernmentTextGroup(String labelText,
                                                 HintTextField field,
                                                 FieldValidator validator) {
        return createRequiredTextGroup(labelText, field, validator);
    }

    private FieldGroup createReadOnlyCurrencyGroup(String labelText, HintTextField field) {
        FieldGroup group = new FieldGroup(labelText, field, false, null, true, false, null);
        group.setPrefixText("PHP");
        return group;
    }

    private FieldGroup registerEditableGroup(FieldGroup group) {
        editableGroups.add(group);
        fieldGroupByField.put(group.field, group);
        return group;
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

    private void attachInputFilters() {
        applyDocumentFilter(employeeNumberField, new CharacterConstraintFilter(
                this::isEmployeeNumberInputValid,
                (candidateText, replacementText) -> showRejectedInput(
                        employeeNumberField,
                        resolveDigitsRejectMessage(candidateText, replacementText, 10)
                )
        ));
        applyDocumentFilter(firstNameField, new CharacterConstraintFilter(
                text -> matchesAllowedTextInput(text, false, false, false, false),
                (candidateText, replacementText) -> showRejectedInput(firstNameField, LETTERS_ONLY_MESSAGE)
        ));
        applyDocumentFilter(lastNameField, new CharacterConstraintFilter(
                text -> matchesAllowedTextInput(text, false, false, false, false),
                (candidateText, replacementText) -> showRejectedInput(lastNameField, LETTERS_ONLY_MESSAGE)
        ));
        applyDocumentFilter(positionField, new CharacterConstraintFilter(
                text -> matchesAllowedTextInput(text, false, true, true, false),
                (candidateText, replacementText) -> showRejectedInput(positionField, LETTERS_ONLY_MESSAGE)
        ));
        applyDocumentFilter(statusField, new CharacterConstraintFilter(
                text -> matchesAllowedTextInput(text, false, false, true, false),
                (candidateText, replacementText) -> showRejectedInput(statusField, LETTERS_ONLY_MESSAGE)
        ));
        applyDocumentFilter(supervisorField, new CharacterConstraintFilter(
                text -> matchesAllowedTextInput(text, true, false, true, true),
                (candidateText, replacementText) -> showRejectedInput(supervisorField, LETTERS_ONLY_MESSAGE)
        ));
        applyDocumentFilter(phoneField, new CharacterConstraintFilter(
                text -> isDigitsWithinLimit(text, 9),
                (candidateText, replacementText) -> showRejectedInput(
                        phoneField,
                        resolveDigitsRejectMessage(candidateText, replacementText, 9)
                )
        ));
        applyDocumentFilter(sssField, new GroupedDigitsFilter(
                (candidateText, replacementText) -> showRejectedInput(
                        sssField,
                        resolveGroupedDigitsRejectMessage(candidateText, replacementText, "Use the format ##-#######-#.")
                ),
                2, 7, 1
        ));
        applyDocumentFilter(philhealthField, new GroupedDigitsFilter(
                (candidateText, replacementText) -> showRejectedInput(
                        philhealthField,
                        resolveGroupedDigitsRejectMessage(candidateText, replacementText, "Use the format ##-#########-#.")
                ),
                2, 9, 1
        ));
        applyDocumentFilter(tinField, new GroupedDigitsFilter(
                (candidateText, replacementText) -> showRejectedInput(
                        tinField,
                        resolveGroupedDigitsRejectMessage(candidateText, replacementText, "Use the format ###-###-###-###.")
                ),
                3, 3, 3, 3
        ));
        applyDocumentFilter(pagibigField, new GroupedDigitsFilter(
                (candidateText, replacementText) -> showRejectedInput(
                        pagibigField,
                        resolveGroupedDigitsRejectMessage(candidateText, replacementText, "Use the format ####-####-####.")
                ),
                4, 4, 4
        ));
        applyDocumentFilter(basicSalaryField, new DecimalConstraintFilter(
                13,
                2,
                (candidateText, replacementText) -> showRejectedInput(
                        basicSalaryField,
                        resolveAmountRejectMessage(replacementText)
                )
        ));
        applyDocumentFilter(riceSubsidyField, new DecimalConstraintFilter(
                13,
                2,
                (candidateText, replacementText) -> showRejectedInput(
                        riceSubsidyField,
                        resolveAmountRejectMessage(replacementText)
                )
        ));
        applyDocumentFilter(phoneAllowanceField, new DecimalConstraintFilter(
                13,
                2,
                (candidateText, replacementText) -> showRejectedInput(
                        phoneAllowanceField,
                        resolveAmountRejectMessage(replacementText)
                )
        ));
        applyDocumentFilter(clothingAllowanceField, new DecimalConstraintFilter(
                13,
                2,
                (candidateText, replacementText) -> showRejectedInput(
                        clothingAllowanceField,
                        resolveAmountRejectMessage(replacementText)
                )
        ));
    }

    private void applyDocumentFilter(HintTextField field, DocumentFilter filter) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(filter);
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

    private void attachAmountFormatting() {
        attachAmountFormatter(basicSalaryField);
        attachAmountFormatter(riceSubsidyField);
        attachAmountFormatter(phoneAllowanceField);
        attachAmountFormatter(clothingAllowanceField);
    }

    private void attachAmountFormatter(HintTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = field.getText() == null ? "" : field.getText().trim();
                if (value.isEmpty()) {
                    return;
                }

                if (validationService.validateAmountInput(value) != null) {
                    return;
                }

                field.setText(formatAmount(parseAmountValue(value)));
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

    private void showRejectedInput(HintTextField field, String message) {
        FieldGroup group = fieldGroupByField.get(field);
        if (group == null) {
            return;
        }

        group.markTouched();
        group.applyError(message);
        refreshValidationSummary();
    }

    private String resolveDigitsRejectMessage(String candidateText, String replacementText, int maxDigits) {
        if (containsNonDigit(replacementText)) {
            return NUMBERS_ONLY_MESSAGE;
        }
        if (candidateText != null && candidateText.length() > maxDigits) {
            return "Maximum of " + maxDigits + " digits only.";
        }
        return NUMBERS_ONLY_MESSAGE;
    }

    private String resolveGroupedDigitsRejectMessage(String candidateText,
                                                     String replacementText,
                                                     String formatMessage) {
        if (containsCharactersOtherThanDigitsAndDashes(replacementText)) {
            return NUMBERS_ONLY_MESSAGE;
        }
        return formatMessage;
    }

    private String resolveAmountRejectMessage(String replacementText) {
        if (containsCharactersOtherThanDigitsAndDecimal(replacementText)) {
            return NUMBERS_ONLY_MESSAGE;
        }
        return "Use numbers with up to 2 decimal places only.";
    }

    private void updateDerivedCompensationFields() {
        String basicSalaryText = basicSalaryField.getText().trim();
        if (basicSalaryText.isEmpty()) {
            clearComputedFields();
            return;
        }

        String validationMessage = validationService.validateAmountInput(basicSalaryText);
        if (validationMessage != null) {
            clearComputedFields();
            return;
        }

        double basicSalary = parseAmountValue(basicSalaryText);
        grossSemiMonthlyRateField.setText(formatAmount(validationService.deriveGrossSemiMonthlyRate(basicSalary)));
        hourlyRateField.setText(formatAmount(validationService.deriveHourlyRate(basicSalary)));
    }

    private void refreshValidationSummary() {
        revalidate();
        repaint();
    }

    private String validateEmployeeNumber(String value) {
        return validationService.validateEmployeeNumberInput(value, existingEmployees, originalEmployeeNumber);
    }

    private String validatePersonName(String value) {
        return validationService.validatePersonNameInput(value);
    }

    private String validatePosition(String value) {
        return validationService.validatePositionInput(value);
    }

    private String validateSupervisor(String value) {
        return validationService.validateSupervisorInput(value);
    }

    private String validateStatus(String value) {
        return validationService.validateStatusInput(value);
    }

    private String validateBirthDate(String value) {
        String message = validationService.validateBirthDateInput(value);
        if (message == null) {
            try {
                selectedBirthDate = LocalDate.parse(value.trim(), BIRTH_DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                selectedBirthDate = null;
            }
        }
        return message;
    }

    private String validateDigitsOnly(String value, int expectedDigits) {
        return validationService.validateDigitsInput(value, expectedDigits);
    }

    private String validateSss(String value) {
        return validationService.validateSssInput(value);
    }

    private String validatePhilhealth(String value) {
        return validationService.validatePhilhealthInput(value);
    }

    private String validateTin(String value) {
        return validationService.validateTinInput(value);
    }

    private String validatePagibig(String value) {
        return validationService.validatePagibigInput(value);
    }

    private String validateAmount(String value) {
        return validationService.validateAmountInput(value);
    }

    private boolean matchesAllowedTextInput(String value,
                                            boolean allowComma,
                                            boolean allowAmpersand,
                                            boolean allowHyphen,
                                            boolean allowSlash) {
        if (value == null || value.isEmpty()) {
            return true;
        }
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
            if (allowHyphen && current == '-') {
                continue;
            }
            if (allowSlash && current == '/') {
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean isDigitsWithinLimit(String value, int maxLength) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (value.length() > maxLength) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmployeeNumberInputValid(String value) {
        return isDigitsWithinLimit(value, 10);
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

    private boolean containsNonDigit(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCharactersOtherThanDigitsAndDashes(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isDigit(current) || current == '-') {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean containsCharactersOtherThanDigitsAndDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isDigit(current) || current == '.') {
                continue;
            }
            return true;
        }
        return false;
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

    private String formatGroupedDigits(String value, int... groups) {
        String digits = normalizeDigitsOnly(value);
        if (digits.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        int offset = 0;
        for (int index = 0; index < groups.length && offset < digits.length(); index++) {
            if (formatted.length() > 0) {
                formatted.append("-");
            }

            int groupSize = groups[index];
            int end = Math.min(offset + groupSize, digits.length());
            formatted.append(digits, offset, end);
            offset = end;
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
        sssField.setText(normalizeGroupedDigits(employee.getSss(), 2, 7, 1));
        philhealthField.setText(normalizeGroupedDigits(employee.getPhilhealth(), 2, 9, 1));
        tinField.setText(normalizeGroupedDigits(employee.getTin(), 3, 3, 3, 3));
        pagibigField.setText(normalizeGroupedDigits(employee.getPagibig(), 4, 4, 4));
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
        private final JLabel prefixLabel = new JLabel();
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
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setAlignmentX(LEFT_ALIGNMENT);

            JLabel label = new JLabel(labelText);
            label.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11.5f));
            label.setForeground(BrandTheme.TEXT_DARK);
            label.setAlignmentX(LEFT_ALIGNMENT);

            BrandTheme.styleInputField(field);
            field.setFont(BrandTheme.BODY_FONT.deriveFont(13f));
            field.setPreferredSize(new Dimension(320, 36));
            field.setMinimumSize(new Dimension(180, 36));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            errorLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.PLAIN, 10.5f));
            errorLabel.setForeground(BrandTheme.MOTORPH_RED);
            errorLabel.setAlignmentX(LEFT_ALIGNMENT);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));
            errorLabel.setText(" ");
            errorLabel.setVisible(true);
            errorLabel.setPreferredSize(new Dimension(320, 16));
            errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

            JPanel rowPanel = new JPanel(new BorderLayout(6, 0));
            rowPanel.setOpaque(false);
            rowPanel.setAlignmentX(LEFT_ALIGNMENT);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
            inputPanel.setOpaque(false);
            inputPanel.setAlignmentX(LEFT_ALIGNMENT);
            prefixLabel.setVisible(false);
            prefixLabel.setFont(BrandTheme.SUBTITLE_FONT.deriveFont(Font.BOLD, 11f));
            prefixLabel.setForeground(BrandTheme.MUTED);
            prefixLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
            inputPanel.add(prefixLabel, BorderLayout.WEST);
            inputPanel.add(field, BorderLayout.CENTER);
            if (accessoryButton != null) {
                inputPanel.add(accessoryButton, BorderLayout.EAST);
            }

            add(label);
            add(Box.createVerticalStrut(4));
            add(inputPanel);
            add(errorLabel);

            applyInputState();
        }

        private void setPrefixText(String text) {
            prefixLabel.setText(text == null ? "" : text);
            prefixLabel.setVisible(text != null && !text.isBlank());
        }

        private void applyInputState() {
            boolean inputLocked = readOnly;
            field.setEditable(!inputLocked);
            field.setFocusable(!inputLocked);

            if (inputLocked) {
                field.setBackground(BrandTheme.TABLE_ALT);
                field.setForeground(BrandTheme.MUTED);
            } else {
                field.setBackground(BrandTheme.INPUT_BG);
                field.setForeground(BrandTheme.TEXT);
            }
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
                field.setBorder(createFieldBorder(BrandTheme.BORDER));
                applyInputState();
                return;
            }

            errorLabel.setText(message);
            field.setBorder(createFieldBorder(BrandTheme.MOTORPH_RED));
            applyInputState();
        }

        private boolean hasError() {
            return errorLabel.getText() != null && !errorLabel.getText().isBlank();
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

    private interface InputConstraint {
        boolean isValid(String candidateText);
    }

    private interface InvalidInputFeedback {
        void handleRejectedInput(String candidateText, String replacementText);
    }

    private static final class CharacterConstraintFilter extends DocumentFilter {

        private final InputConstraint constraint;
        private final InvalidInputFeedback rejectedInputFeedback;

        private CharacterConstraintFilter(InputConstraint constraint,
                                          InvalidInputFeedback rejectedInputFeedback) {
            this.constraint = constraint;
            this.rejectedInputFeedback = rejectedInputFeedback;
        }

        @Override
        public void insertString(FilterBypass fb,
                                 int offset,
                                 String string,
                                 AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb,
                            int offset,
                            int length,
                            String text,
                            AttributeSet attrs) throws BadLocationException {
            String replacement = text == null ? "" : text;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = currentText.substring(0, offset)
                    + replacement
                    + currentText.substring(offset + length);
            if (constraint.isValid(candidate)) {
                super.replace(fb, offset, length, replacement, attrs);
            } else if (rejectedInputFeedback != null && !replacement.isEmpty()) {
                rejectedInputFeedback.handleRejectedInput(candidate, replacement);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }
    }

    private final class GroupedDigitsFilter extends DocumentFilter {

        private final int[] groups;
        private final int maxDigits;
        private final InvalidInputFeedback rejectedInputFeedback;

        private GroupedDigitsFilter(InvalidInputFeedback rejectedInputFeedback, int... groups) {
            this.rejectedInputFeedback = rejectedInputFeedback;
            this.groups = groups.clone();

            int total = 0;
            for (int group : groups) {
                total += group;
            }
            this.maxDigits = total;
        }

        @Override
        public void insertString(FilterBypass fb,
                                 int offset,
                                 String string,
                                 AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb,
                            int offset,
                            int length,
                            String text,
                            AttributeSet attrs) throws BadLocationException {
            String replacement = text == null ? "" : text;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = currentText.substring(0, offset)
                    + replacement
                    + currentText.substring(offset + length);

            String digits = normalizeDigitsOnly(candidate);
            if (digits.length() > maxDigits) {
                if (rejectedInputFeedback != null && !replacement.isEmpty()) {
                    rejectedInputFeedback.handleRejectedInput(candidate, replacement);
                }
                return;
            }

            if (!replacement.isEmpty() && containsCharactersOtherThanDigitsAndDashes(replacement)) {
                if (rejectedInputFeedback != null) {
                    rejectedInputFeedback.handleRejectedInput(candidate, replacement);
                }
                return;
            }

            String formatted = formatGroupedDigits(digits, groups);
            fb.replace(0, fb.getDocument().getLength(), formatted, attrs);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }
    }

    private static final class DecimalConstraintFilter extends DocumentFilter {

        private final int maxDigitsBeforeDecimal;
        private final int maxDigitsAfterDecimal;
        private final InvalidInputFeedback rejectedInputFeedback;

        private DecimalConstraintFilter(int maxDigitsBeforeDecimal,
                                        int maxDigitsAfterDecimal,
                                        InvalidInputFeedback rejectedInputFeedback) {
            this.maxDigitsBeforeDecimal = maxDigitsBeforeDecimal;
            this.maxDigitsAfterDecimal = maxDigitsAfterDecimal;
            this.rejectedInputFeedback = rejectedInputFeedback;
        }

        @Override
        public void insertString(FilterBypass fb,
                                 int offset,
                                 String string,
                                 AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb,
                            int offset,
                            int length,
                            String text,
                            AttributeSet attrs) throws BadLocationException {
            String replacement = text == null ? "" : text;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = currentText.substring(0, offset)
                    + replacement
                    + currentText.substring(offset + length);

            if (isValidDecimal(candidate)) {
                super.replace(fb, offset, length, replacement, attrs);
            } else if (rejectedInputFeedback != null && !replacement.isEmpty()) {
                rejectedInputFeedback.handleRejectedInput(candidate, replacement);
            }
        }

        private boolean isValidDecimal(String value) {
            if (value == null || value.isEmpty()) {
                return true;
            }

            int decimalIndex = value.indexOf('.');
            if (decimalIndex >= 0 && value.indexOf('.', decimalIndex + 1) >= 0) {
                return false;
            }

            String wholePart = decimalIndex >= 0 ? value.substring(0, decimalIndex) : value;
            String decimalPart = decimalIndex >= 0 ? value.substring(decimalIndex + 1) : "";

            if (wholePart.length() > maxDigitsBeforeDecimal || decimalPart.length() > maxDigitsAfterDecimal) {
                return false;
            }

            for (int index = 0; index < wholePart.length(); index++) {
                if (!Character.isDigit(wholePart.charAt(index))) {
                    return false;
                }
            }
            for (int index = 0; index < decimalPart.length(); index++) {
                if (!Character.isDigit(decimalPart.charAt(index))) {
                    return false;
                }
            }

            return !(value.equals("."));
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

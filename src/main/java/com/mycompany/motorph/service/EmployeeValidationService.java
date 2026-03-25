package com.mycompany.motorph.service;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeFormData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeValidationService {

    private static final String REQUIRED_MESSAGE = "This field is required.";
    private static final String LETTERS_ONLY_MESSAGE = "Only alphabetic characters are allowed.";
    private static final String NUMBERS_ONLY_MESSAGE = "Please enter numbers only.";
    private static final double WORK_DAYS_PER_MONTH = 21.0;
    private static final double HOURS_PER_DAY = 8.0;
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
    private static final Pattern SSS_PATTERN = Pattern.compile("\\d{2}-\\d{7}-\\d");
    private static final Pattern PHILHEALTH_PATTERN = Pattern.compile("\\d{2}-\\d{9}-\\d");
    private static final Pattern TIN_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    private static final Pattern PAGIBIG_PATTERN = Pattern.compile("\\d{4}-\\d{4}-\\d{4}");

    public Employee createEmployee(EmployeeFormData data, List<Employee> existingEmployees) {
        return buildEmployee(data, existingEmployees, null);
    }

    public Employee updateEmployee(EmployeeFormData data,
                                   List<Employee> existingEmployees,
                                   int originalEmployeeNumber) {
        return buildEmployee(data, existingEmployees, originalEmployeeNumber);
    }

    private Employee buildEmployee(EmployeeFormData data,
                                   List<Employee> existingEmployees,
                                   Integer originalEmployeeNumber) {

        if (data == null) {
            throw new IllegalArgumentException("Employee form data is required.");
        }

        int employeeNumber = parseRequiredInt(data.getEmployeeNumber(), "Employee Number");
        ensureUniqueEmployeeNumber(employeeNumber, existingEmployees, originalEmployeeNumber);

        String firstName = requireValidatedValue(data.getFirstName(), "First Name", validatePersonNameInput(data.getFirstName()));
        String lastName = requireValidatedValue(data.getLastName(), "Last Name", validatePersonNameInput(data.getLastName()));
        String birthDate = requireValidatedValue(data.getBirthDate(), "Birth Date", validateBirthDateInput(data.getBirthDate()));
        String position = requireValidatedValue(data.getPosition(), "Position", validatePositionInput(data.getPosition()));
        String status = requireValidatedValue(data.getStatus(), "Status", validateStatusInput(data.getStatus()));
        String supervisor = requireValidatedValue(data.getSupervisor(), "Immediate Supervisor", validateSupervisorInput(data.getSupervisor()));
        String address = requireText(data.getAddress(), "Address");
        String phone = requirePattern(data.getPhone(), "Phone Number", PHONE_PATTERN, "Phone Number must follow ###-###-###.");
        String sss = requirePattern(data.getSss(), "SSS", SSS_PATTERN, "SSS must follow ##-#######-#.");
        String philhealth = requirePattern(data.getPhilhealth(), "PhilHealth", PHILHEALTH_PATTERN, "PhilHealth must follow ##-#########-#.");
        String tin = requirePattern(data.getTin(), "TIN", TIN_PATTERN, "TIN must follow ###-###-###-###.");
        String pagibig = requirePattern(data.getPagibig(), "Pag-IBIG", PAGIBIG_PATTERN, "Pag-IBIG must follow ####-####-####.");

        double basicSalary = parseRequiredDouble(data.getBasicSalary(), "Basic Salary");
        double riceSubsidy = parseRequiredDouble(data.getRiceSubsidy(), "Rice Subsidy");
        double phoneAllowance = parseRequiredDouble(data.getPhoneAllowance(), "Phone Allowance");
        double clothingAllowance = parseRequiredDouble(data.getClothingAllowance(), "Clothing Allowance");

        double grossSemiMonthlyRate = deriveGrossSemiMonthlyRate(basicSalary);
        double hourlyRate = deriveHourlyRate(basicSalary);

        return new Employee(
                employeeNumber,
                lastName,
                firstName,
                birthDate,
                address,
                phone,
                sss,
                philhealth,
                tin,
                pagibig,
                status,
                position,
                supervisor,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );
    }

    public String validateEmployeeNumberInput(String value,
                                              List<Employee> existingEmployees,
                                              Integer originalEmployeeNumber) {
        String digitsMessage = validateDigitsInput(value, -1);
        if (digitsMessage != null) {
            return digitsMessage;
        }

        try {
            int employeeNumber = Integer.parseInt(value.trim());
            ensureUniqueEmployeeNumber(employeeNumber, existingEmployees, originalEmployeeNumber);
            return null;
        } catch (NumberFormatException e) {
            return NUMBERS_ONLY_MESSAGE;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String validatePersonNameInput(String value) {
        return validateTextValue(value, false, false, true, false, false);
    }

    public String validatePositionInput(String value) {
        return validateTextValue(value, false, true, true, true, false);
    }

    public String validateSupervisorInput(String value) {
        String text = trim(value);
        if ("N/A".equalsIgnoreCase(text)) {
            return null;
        }
        return validateTextValue(text, true, false, true, true, true);
    }

    public String validateStatusInput(String value) {
        return validateTextValue(value, false, false, true, true, false);
    }

    public String validateBirthDateInput(String value) {
        if (trim(value).isEmpty()) {
            return REQUIRED_MESSAGE;
        }

        try {
            LocalDate.parse(value.trim(), BIRTH_DATE_FORMATTER);
            return null;
        } catch (DateTimeParseException e) {
            return "Please select a valid date.";
        }
    }

    public String validateDigitsInput(String value, int expectedDigits) {
        String text = trim(value);
        if (text.isEmpty()) {
            return REQUIRED_MESSAGE;
        }

        for (int index = 0; index < text.length(); index++) {
            if (!Character.isDigit(text.charAt(index))) {
                return NUMBERS_ONLY_MESSAGE;
            }
        }

        if (expectedDigits > 0 && text.length() != expectedDigits) {
            return "Please enter " + expectedDigits + " digits.";
        }

        return null;
    }

    public String validateAmountInput(String value) {
        String text = trim(value);
        if (text.isEmpty()) {
            return REQUIRED_MESSAGE;
        }

        int decimalPoints = 0;
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
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
            double amount = Double.parseDouble(text);
            if (amount < 0) {
                return NUMBERS_ONLY_MESSAGE;
            }
            return null;
        } catch (NumberFormatException e) {
            return NUMBERS_ONLY_MESSAGE;
        }
    }

    public String validateSssInput(String value) {
        return validateFormattedPattern(value, SSS_PATTERN, "Use the format ##-#######-#.");
    }

    public String validatePhilhealthInput(String value) {
        return validateFormattedPattern(value, PHILHEALTH_PATTERN, "Use the format ##-#########-#.");
    }

    public String validateTinInput(String value) {
        return validateFormattedPattern(value, TIN_PATTERN, "Use the format ###-###-###-###.");
    }

    public String validatePagibigInput(String value) {
        return validateFormattedPattern(value, PAGIBIG_PATTERN, "Use the format ####-####-####.");
    }

    public double deriveGrossSemiMonthlyRate(double basicSalary) {
        return round(basicSalary / 2.0);
    }

    public double deriveHourlyRate(double basicSalary) {
        return round(basicSalary / WORK_DAYS_PER_MONTH / HOURS_PER_DAY);
    }

    private void ensureUniqueEmployeeNumber(int employeeNumber,
                                            List<Employee> existingEmployees,
                                            Integer originalEmployeeNumber) {

        if (existingEmployees == null) {
            return;
        }

        for (Employee employee : existingEmployees) {
            if (employee.getEmployeeNumber() != employeeNumber) {
                continue;
            }

            if (originalEmployeeNumber != null && employeeNumber == originalEmployeeNumber) {
                return;
            }

            throw new IllegalArgumentException("Employee Number already exists.");
        }
    }

    private int parseRequiredInt(String value, String fieldName) {

        String text = requireText(value, fieldName);

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }
    }

    private double parseRequiredDouble(String value, String fieldName) {

        String text = requireText(value, fieldName);

        String validationMessage = validateAmountInput(text);
        if (validationMessage != null) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }

        try {
            double number = Double.parseDouble(text.replace(",", ""));
            if (number < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            }
            return round(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }
    }

    private String requirePattern(String value,
                                  String fieldName,
                                  Pattern pattern,
                                  String errorMessage) {

        String text = requireText(value, fieldName);
        if (!pattern.matcher(text).matches()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return text;
    }

    private String requireText(String value, String fieldName) {

        String text = trim(value);
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

        return text;
    }

    private String requireBirthDate(String value) {
        String text = requireText(value, "Birth Date");
        try {
            LocalDate.parse(text, BIRTH_DATE_FORMATTER);
            return text;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Birth Date must follow MM/dd/yyyy.");
        }
    }

    private String requireValidatedValue(String value, String fieldName, String validationMessage) {
        String text = requireText(value, fieldName);
        if (validationMessage != null) {
            throw new IllegalArgumentException(fieldName + ": " + validationMessage);
        }
        return text;
    }

    private String validateFormattedPattern(String value, Pattern pattern, String formatMessage) {
        String text = trim(value);
        if (text.isEmpty()) {
            return REQUIRED_MESSAGE;
        }
        if (!pattern.matcher(text).matches()) {
            return formatMessage;
        }
        return null;
    }

    private String validateTextValue(String value,
                                     boolean allowComma,
                                     boolean allowAmpersand,
                                     boolean allowSpaces,
                                     boolean allowHyphen,
                                     boolean allowSlash) {
        String text = trim(value);
        if (text.isEmpty()) {
            return REQUIRED_MESSAGE;
        }

        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (Character.isLetter(current)) {
                continue;
            }
            if (allowSpaces && Character.isWhitespace(current)) {
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
            return LETTERS_ONLY_MESSAGE;
        }
        return null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

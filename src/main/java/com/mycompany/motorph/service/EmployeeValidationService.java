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

    private static final double WORK_DAYS_PER_MONTH = 21.0;
    private static final double HOURS_PER_DAY = 8.0;
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
    private static final Pattern SSS_PATTERN = Pattern.compile("\\d{2}-\\d{7}-\\d");
    private static final Pattern PHILHEALTH_PATTERN = Pattern.compile("\\d{12}");
    private static final Pattern TIN_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    private static final Pattern PAGIBIG_PATTERN = Pattern.compile("\\d{12}");

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

        String firstName = requireText(data.getFirstName(), "First Name");
        String lastName = requireText(data.getLastName(), "Last Name");
        String birthDate = requireBirthDate(data.getBirthDate());
        String position = requireText(data.getPosition(), "Position");
        String status = requireText(data.getStatus(), "Status");
        String supervisor = requireText(data.getSupervisor(), "Immediate Supervisor");
        String address = requireText(data.getAddress(), "Address");
        String phone = requirePattern(data.getPhone(), "Phone Number", PHONE_PATTERN, "Phone Number must follow ###-###-###.");
        String sss = requirePattern(data.getSss(), "SSS", SSS_PATTERN, "SSS must follow ##-#######-#.");
        String philhealth = requirePattern(data.getPhilhealth(), "PhilHealth", PHILHEALTH_PATTERN, "PhilHealth must contain 12 digits.");
        String tin = requirePattern(data.getTin(), "TIN", TIN_PATTERN, "TIN must follow ###-###-###-###.");
        String pagibig = requirePattern(data.getPagibig(), "Pag-IBIG", PAGIBIG_PATTERN, "Pag-IBIG must contain 12 digits.");

        double basicSalary = parseRequiredDouble(data.getBasicSalary(), "Basic Salary");
        double riceSubsidy = parseRequiredDouble(data.getRiceSubsidy(), "Rice Subsidy");
        double phoneAllowance = parseRequiredDouble(data.getPhoneAllowance(), "Phone Allowance");
        double clothingAllowance = parseRequiredDouble(data.getClothingAllowance(), "Clothing Allowance");

        double grossSemiMonthlyRate = round(basicSalary / 2.0);
        double hourlyRate = round(basicSalary / WORK_DAYS_PER_MONTH / HOURS_PER_DAY);

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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

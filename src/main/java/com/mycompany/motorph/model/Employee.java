package com.mycompany.motorph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Employee {

    private int employeeNumber;
    private String lastName;
    private String firstName;
    private String birthDate;
    private String address;
    private String phoneNumber;

    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagibigNumber;

    private String status;
    private String position;
    private String immediateSupervisor;

    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    private final List<Attendance> attendanceRecords;
    private double hoursWorked;

    public Employee(
            int employeeNumber,
            String lastName,
            String firstName,
            String birthDate,
            String address,
            String phoneNumber,
            String sssNumber,
            String philhealthNumber,
            String tinNumber,
            String pagibigNumber,
            String status,
            String position,
            String immediateSupervisor,
            double basicSalary,
            double riceSubsidy,
            double phoneAllowance,
            double clothingAllowance,
            double grossSemiMonthlyRate,
            double hourlyRate) {

        validateEmployeeNumber(employeeNumber);
        validateRequired(lastName, "Last Name");
        validateRequired(firstName, "First Name");
        validateRequired(phoneNumber, "Phone Number");

        validateGovernmentId(sssNumber, "SSS");
        validateGovernmentId(philhealthNumber, "PhilHealth");
        validateGovernmentId(tinNumber, "TIN");
        validateGovernmentId(pagibigNumber, "Pag-IBIG");

        validatePositive(basicSalary, "Basic Salary");
        validatePositive(riceSubsidy, "Rice Subsidy");
        validatePositive(phoneAllowance, "Phone Allowance");
        validatePositive(clothingAllowance, "Clothing Allowance");
        validatePositive(grossSemiMonthlyRate, "Gross Semi Monthly Rate");
        validatePositive(hourlyRate, "Hourly Rate");

        this.attendanceRecords = new ArrayList<>();
        setEmployeeNumber(employeeNumber);
        setLastName(lastName);
        setFirstName(firstName);
        setBirthDate(birthDate);
        setAddress(address);
        setPhone(phoneNumber);
        setSss(sssNumber);
        setPhilhealth(philhealthNumber);
        setTin(tinNumber);
        setPagibig(pagibigNumber);
        setStatus(status);
        setPosition(position);
        setSupervisor(immediateSupervisor);
        setBasicSalary(basicSalary);
        setRiceSubsidy(riceSubsidy);
        setPhoneAllowance(phoneAllowance);
        setClothingAllowance(clothingAllowance);
        setGrossSemiMonthlyRate(grossSemiMonthlyRate);
        setHourlyRate(hourlyRate);
        this.hoursWorked = 0;
    }

    // ===============================
    // VALIDATION METHODS
    // ===============================

    private void validateEmployeeNumber(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid Employee Number.");
        }

    }

    private void validateRequired(String value, String fieldName) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

    }

    private void validateGovernmentId(String id, String type) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(type + " number cannot be empty.");
        }

    }

    private void validatePositive(double value, String field) {

        if (value < 0) {
            throw new IllegalArgumentException(field + " cannot be negative.");
        }

    }

    // ===== EXISTING GETTERS =====

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getEmployeeName() {
        return firstName + " " + lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public double getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public List<Attendance> getAttendanceRecords() {
        return Collections.unmodifiableList(attendanceRecords);
    }

    // ===== NEW GETTERS (FOR CRUD + DAO) =====

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getSss() {
        return sssNumber;
    }

    public String getPhilhealth() {
        return philhealthNumber;
    }

    public String getTin() {
        return tinNumber;
    }

    public String getPagibig() {
        return pagibigNumber;
    }

    public String getSupervisor() {
        return immediateSupervisor;
    }

    public void setEmployeeNumber(int employeeNumber) {
        validateEmployeeNumber(employeeNumber);
        this.employeeNumber = employeeNumber;
    }

    public void setLastName(String lastName) {
        validateRequired(lastName, "Last Name");
        this.lastName = lastName.trim();
    }

    public void setFirstName(String firstName) {
        validateRequired(firstName, "First Name");
        this.firstName = firstName.trim();
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate == null ? "" : birthDate.trim();
    }

    public void setAddress(String address) {
        this.address = address == null ? "" : address.trim();
    }

    public void setPhone(String phoneNumber) {
        validateRequired(phoneNumber, "Phone Number");
        this.phoneNumber = phoneNumber.trim();
    }

    public void setSss(String sssNumber) {
        validateGovernmentId(sssNumber, "SSS");
        this.sssNumber = sssNumber.trim();
    }

    public void setPhilhealth(String philhealthNumber) {
        validateGovernmentId(philhealthNumber, "PhilHealth");
        this.philhealthNumber = philhealthNumber.trim();
    }

    public void setTin(String tinNumber) {
        validateGovernmentId(tinNumber, "TIN");
        this.tinNumber = tinNumber.trim();
    }

    public void setPagibig(String pagibigNumber) {
        validateGovernmentId(pagibigNumber, "Pag-IBIG");
        this.pagibigNumber = pagibigNumber.trim();
    }

    public void setStatus(String status) {
        this.status = status == null ? "" : status.trim();
    }

    public void setPosition(String position) {
        this.position = position == null ? "" : position.trim();
    }

    public void setSupervisor(String immediateSupervisor) {
        this.immediateSupervisor = immediateSupervisor == null ? "" : immediateSupervisor.trim();
    }

    public void setBasicSalary(double basicSalary) {
        validatePositive(basicSalary, "Basic Salary");
        this.basicSalary = basicSalary;
    }

    public void setRiceSubsidy(double riceSubsidy) {
        validatePositive(riceSubsidy, "Rice Subsidy");
        this.riceSubsidy = riceSubsidy;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        validatePositive(phoneAllowance, "Phone Allowance");
        this.phoneAllowance = phoneAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        validatePositive(clothingAllowance, "Clothing Allowance");
        this.clothingAllowance = clothingAllowance;
    }

    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        validatePositive(grossSemiMonthlyRate, "Gross Semi Monthly Rate");
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        validatePositive(hourlyRate, "Hourly Rate");
        this.hourlyRate = hourlyRate;
    }

    // ===== ATTENDANCE METHODS =====

    public void setHoursWorked(double hoursWorked) {
        validatePositive(hoursWorked, "Hours Worked");
        this.hoursWorked = hoursWorked;
    }

    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null.");
        }
        attendanceRecords.add(attendance);
    }

    public void addWorkedHours(double hours) {
        validatePositive(hours, "Worked Hours");
        this.hoursWorked += hours;
    }

    public void clearAttendance() {
        attendanceRecords.clear();
        hoursWorked = 0;
    }

    // ===== FORMATTERS =====

    private String formatTime(double time) {

        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 60);

        if (minutes == 60) {
            hours++;
            minutes = 0;
        }

        return String.format("%02d:%02d", hours, minutes);
    }

    private String formatHours(double hours) {

        int h = (int) hours;
        int m = (int) Math.round((hours - h) * 60);

        if (m == 60) {
            h++;
            m = 0;
        }

        return h + "h " + m + "m";
    }

}

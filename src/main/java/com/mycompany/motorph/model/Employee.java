/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    private List<Attendance> attendanceRecords;
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

        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;

        this.sssNumber = sssNumber;
        this.philhealthNumber = philhealthNumber;
        this.tinNumber = tinNumber;
        this.pagibigNumber = pagibigNumber;

        this.status = status;
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;

        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;

        this.attendanceRecords = new ArrayList<>();
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
        return attendanceRecords;
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

    // ===== ATTENDANCE METHODS =====

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void addAttendance(Attendance attendance) {
        attendanceRecords.add(attendance);
    }

    public void addWorkedHours(double hours) {
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

    // ===== ATTENDANCE VIEW =====

    public void viewAttendance() {

        System.out.println("\n===== ATTENDANCE =====");

        for (Attendance a : attendanceRecords) {

            System.out.println(
                    a.getDate()
                    + " | IN: " + formatTime(a.getLogInTime())
                    + " | OUT: " + formatTime(a.getLogOutTime())
                    + " | HOURS: " + formatHours(a.getHoursWorked())
            );

        }

        System.out.println("\nTotal Hours Worked: " + formatHours(hoursWorked));
    }

    // ===== PROFILE DISPLAY =====

    public void displayDetails() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== EMPLOYEE PROFILE =====");
            System.out.println("Employee #: " + employeeNumber);
            System.out.println("Name: " + getEmployeeName());
            System.out.println("Birthday: " + birthDate);
            System.out.println("Address: " + address);
            System.out.println("Phone Number: " + phoneNumber);
            System.out.println("Status: " + status);
            System.out.println("Position: " + position);
            System.out.println("Immediate Supervisor: " + immediateSupervisor);

            System.out.println("\n1 View Government IDs");
            System.out.println("2 Back");

            System.out.print("Select option: ");
            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    viewGovernmentIDs();
                    break;

                case 2:
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewGovernmentIDs() {

        System.out.println("\n===== GOVERNMENT IDS =====");

        System.out.println("SSS #: " + sssNumber);
        System.out.println("Philhealth #: " + philhealthNumber);
        System.out.println("TIN #: " + tinNumber);
        System.out.println("Pag-ibig #: " + pagibigNumber);
    }
}
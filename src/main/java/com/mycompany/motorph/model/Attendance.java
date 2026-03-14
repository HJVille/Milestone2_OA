package com.mycompany.motorph.model;

public class Attendance implements WorkHoursCalculable {

    private String date;
    private double logInTime;
    private double logOutTime;
    private double breakHours;

    public Attendance(String date, double logInTime, double logOutTime) {
        this(date, logInTime, logOutTime, 0);
    }

    public Attendance(String date, double logInTime, double logOutTime, double breakHours) {
        setDate(date);
        setLogInTime(logInTime);
        setLogOutTime(logOutTime);
        setBreakHours(breakHours);
    }

    public String getDate() {
        return date;
    }

    public double getLogInTime() {
        return logInTime;
    }

    public double getLogOutTime() {
        return logOutTime;
    }

    public double getBreakHours() {
        return breakHours;
    }

    public double getHoursWorked() {
        return calculateHoursWorked();
    }

    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }
        this.date = date.trim();
    }

    public void setLogInTime(double logInTime) {
        validateTime(logInTime, "Log in time");
        this.logInTime = logInTime;
    }

    public void setLogOutTime(double logOutTime) {
        validateTime(logOutTime, "Log out time");
        this.logOutTime = logOutTime;
    }

    public void setBreakHours(double breakHours) {
        if (breakHours < 0) {
            throw new IllegalArgumentException("Break hours cannot be negative.");
        }
        this.breakHours = breakHours;
    }

    @Override
    public double calculateHoursWorked() {
        double hours = logOutTime - logInTime - breakHours;
        if (hours < 0) {
            hours = 0;
        }
        return Math.round(hours * 100.0) / 100.0;
    }

    private void validateTime(double time, String label) {
        if (time < 0 || time >= 24) {
            throw new IllegalArgumentException(label + " must be between 0.0 and 23.99.");
        }
    }
}

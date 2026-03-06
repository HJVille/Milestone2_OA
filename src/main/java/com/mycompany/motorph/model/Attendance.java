/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class Attendance {

    private String date;
    private double logInTime;
    private double logOutTime;

    public Attendance(String date, double logInTime, double logOutTime) {
        this.date = date;
        this.logInTime = logInTime;
        this.logOutTime = logOutTime;
    }

    // ===============================
    // GETTERS
    // ===============================

    public String getDate() {
        return date;
    }

    public double getLogInTime() {
        return logInTime;
    }

    public double getLogOutTime() {
        return logOutTime;
    }

    // IMPORTANT: used by Employee.computeTotalHours()
    public double getHoursWorked() {

        double hours = logOutTime - logInTime;

        // round to 2 decimal places
        return Math.round(hours * 100.0) / 100.0;
    }

    // ===============================
    // SETTERS
    // ===============================

    public void setDate(String date) {
        this.date = date;
    }

    public void setLogInTime(double logInTime) {
        this.logInTime = logInTime;
    }

    public void setLogOutTime(double logOutTime) {
        this.logOutTime = logOutTime;
    }
}
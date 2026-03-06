/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class LeaveRequest {

    private int employeeNumber;
    private String employeeName;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String status;

    public LeaveRequest(int employeeNumber,
                        String employeeName,
                        String leaveType,
                        String startDate,
                        String endDate) {

        super();

        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "PENDING";

    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void approve() {
        status = "APPROVED";
    }

    public void reject() {
        status = "REJECTED";
    }

    public void displayDetails() {

        System.out.println(
                employeeName + " | "
                + leaveType + " | "
                + startDate + " - " + endDate + " | "
                + status
        );

    }

}
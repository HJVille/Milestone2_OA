package com.mycompany.motorph.model;

public class LeaveRequest implements Approvable {

    private final int employeeNumber;
    private final String employeeName;
    private final String leaveType;
    private final String startDate;
    private final String endDate;
    private LeaveStatus status;
    private String statusMessage;

    public LeaveRequest(int employeeNumber,
                        String employeeName,
                        String leaveType,
                        String startDate,
                        String endDate) {
        this(employeeNumber, employeeName, leaveType, startDate, endDate, null);
    }

    public LeaveRequest(int employeeNumber,
                        String employeeName,
                        String leaveType,
                        String startDate,
                        String endDate,
                        String statusMessage) {

        this.employeeNumber = requirePositive(employeeNumber, "Employee number");
        this.employeeName = requireText(employeeName, "Employee name");
        this.leaveType = requireText(leaveType, "Leave type");
        this.startDate = requireText(startDate, "Start date");
        this.endDate = requireText(endDate, "End date");
        this.status = LeaveStatus.PENDING;
        this.statusMessage = resolveStatusMessage(statusMessage, LeaveStatus.PENDING);
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
        return status.name();
    }

    public LeaveStatus getLeaveStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public void approve() {
        approve(null);
    }

    @Override
    public void reject() {
        reject(null);
    }

    public void approve(String statusMessage) {
        setStatus(LeaveStatus.APPROVED, statusMessage);
    }

    public void reject(String statusMessage) {
        setStatus(LeaveStatus.REJECTED, statusMessage);
    }

    public void setStatus(LeaveStatus status) {
        setStatus(status, null);
    }

    public void setStatus(LeaveStatus status, String statusMessage) {
        if (status == null) {
            throw new IllegalArgumentException("Leave status cannot be null.");
        }
        this.status = status;
        this.statusMessage = resolveStatusMessage(statusMessage, status);
    }

    public void setStatus(String status) {
        setStatus(status, null);
    }

    public void setStatus(String status, String statusMessage) {
        setStatus(LeaveStatus.valueOf(requireText(status, "Leave status").toUpperCase()), statusMessage);
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = resolveStatusMessage(statusMessage, status);
    }

    public void displayDetails() {
        System.out.println(
                employeeName + " | "
                + leaveType + " | "
                + startDate + " - " + endDate + " | "
                + status + " | "
                + statusMessage
        );
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    private int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
        return value;
    }

    private String resolveStatusMessage(String message, LeaveStatus leaveStatus) {
        if (message != null && !message.trim().isEmpty()) {
            return message.trim();
        }

        switch (leaveStatus) {
            case APPROVED:
                return "Approved by HR/Admin.";
            case REJECTED:
                return "Rejected by HR/Admin.";
            case PENDING:
            default:
                return "Pending review by HR/Admin.";
        }
    }
}

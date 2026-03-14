package com.mycompany.motorph.model;

public abstract class PayrollComponent {

    private final int employeeNumber;
    private final String employeeName;

    public PayrollComponent(int employeeNumber, String employeeName) {
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public abstract void display();
}

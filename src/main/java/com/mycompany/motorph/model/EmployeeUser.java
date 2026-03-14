package com.mycompany.motorph.model;

public class EmployeeUser extends User {

    public EmployeeUser(String username, String password, int employeeNumber) {
        super(username, password, "EMPLOYEE", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "EMPLOYEE";
    }
}
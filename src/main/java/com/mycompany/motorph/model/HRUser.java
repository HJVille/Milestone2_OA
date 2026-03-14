package com.mycompany.motorph.model;

public class HRUser extends User {

    public HRUser(String username, String password, int employeeNumber) {
        super(username, password, "HR", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "HR";
    }
}
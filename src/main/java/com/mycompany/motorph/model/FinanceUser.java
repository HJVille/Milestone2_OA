package com.mycompany.motorph.model;

public class FinanceUser extends User {

    public FinanceUser(String username, String password, int employeeNumber) {
        super(username, password, "FINANCE", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "FINANCE";
    }
}

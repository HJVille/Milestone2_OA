package com.mycompany.motorph.model;

public class AdminUser extends User {

    public AdminUser(String username, String password, int employeeNumber) {
        super(username, password, "ADMIN", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }
}

package com.mycompany.motorph.model;

public class User extends SystemUser {

    public User(String username, String password, String role, int employeeNumber) {
        super(username, password, role, employeeNumber);
    }

    @Override
    public String getUserType() {
        return getRole();
    }
}

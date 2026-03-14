package com.mycompany.motorph.model;

public class ItUser extends User {

    public ItUser(String username, String password, int employeeNumber) {
        super(username, password, "IT", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "IT";
    }
}

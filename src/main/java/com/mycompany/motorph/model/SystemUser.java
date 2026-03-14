package com.mycompany.motorph.model;

public abstract class SystemUser {

    private String username;
    private String password;
    private final String role;
    private final int employeeNumber;

    public SystemUser(String username, String password, String role, int employeeNumber) {
        setUsername(username);
        setPassword(password);
        this.role = requireText(role, "Role");
        this.employeeNumber = requireNonNegative(employeeNumber, "Employee number");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setPassword(String password) {
        this.password = requireText(password, "Password");
    }

    public void setUsername(String username) {
        this.username = requireText(username, "Username");
    }

    public abstract String getUserType();

    protected final String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    protected final int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return value;
    }
}

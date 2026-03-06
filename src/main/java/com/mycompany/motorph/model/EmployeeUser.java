/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
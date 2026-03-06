/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class PayrollUser extends User {

    public PayrollUser(String username, String password, int employeeNumber) {
        super(username, password, "PAYROLL", employeeNumber);
    }

    @Override
    public String getUserType() {
        return "PAYROLL";
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
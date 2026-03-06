/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class User extends SystemUser {

    // ======================================
    // CONSTRUCTOR
    // ======================================

    public User(String username, String password, String role, int employeeNumber) {

        super(username, password, role, employeeNumber);

    }

    // ======================================
    // USER TYPE (ABSTRACT IMPLEMENTATION)
    // ======================================

    @Override
    public String getUserType() {

        return role;

    }

}
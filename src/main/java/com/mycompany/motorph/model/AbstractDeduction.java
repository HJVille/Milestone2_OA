/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public abstract class AbstractDeduction {

    protected String name;

    public AbstractDeduction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double compute(double monthlySalary);

}
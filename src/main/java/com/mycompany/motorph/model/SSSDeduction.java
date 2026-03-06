/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class SSSDeduction extends AbstractDeduction {

    public SSSDeduction() {
        super("SSS");
    }

    @Override
    public double compute(double monthlySalary) {

        double salaryCap = Math.min(monthlySalary, 35000);

        return salaryCap * 0.05;
    }
}
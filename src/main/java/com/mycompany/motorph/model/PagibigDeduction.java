/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.model;

public class PagibigDeduction extends AbstractDeduction {

    public PagibigDeduction() {
        super("PagIBIG");
    }

    @Override
    public double compute(double monthlySalary) {

        double contribution = monthlySalary * 0.02;

        return Math.min(contribution, 100);
    }
}
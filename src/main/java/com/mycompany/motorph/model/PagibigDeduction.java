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
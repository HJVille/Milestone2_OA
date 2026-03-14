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
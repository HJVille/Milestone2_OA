package com.mycompany.motorph.model;

public class PhilHealthDeduction extends AbstractDeduction {

    public PhilHealthDeduction() {
        super("PhilHealth");
    }

    @Override
    public double compute(double monthlySalary) {

        double salary = Math.max(10000, Math.min(monthlySalary, 100000));

        return salary * 0.025;
    }
}

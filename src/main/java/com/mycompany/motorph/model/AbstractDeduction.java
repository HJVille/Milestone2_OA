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
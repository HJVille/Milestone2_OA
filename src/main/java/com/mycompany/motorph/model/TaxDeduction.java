package com.mycompany.motorph.model;

public class TaxDeduction extends AbstractDeduction {

    public TaxDeduction() {
        super("Tax");
    }

    @Override
    public double compute(double taxableIncome) {

        double tax = 0;

        // TRAIN LAW - MONTHLY TAX BRACKETS

        if (taxableIncome <= 20833) {

            tax = 0;

        }
        else if (taxableIncome <= 33333) {

            tax = (taxableIncome - 20833) * 0.20;

        }
        else if (taxableIncome <= 66667) {

            tax = 2500 + (taxableIncome - 33333) * 0.25;

        }
        else if (taxableIncome <= 166667) {

            tax = 10833 + (taxableIncome - 66667) * 0.30;

        }
        else if (taxableIncome <= 666667) {

            tax = 40833.33 + (taxableIncome - 166667) * 0.32;

        }
        else {

            tax = 200833.33 + (taxableIncome - 666667) * 0.35;

        }

        return Math.round(tax * 100.0) / 100.0;
    }
}
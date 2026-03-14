package com.mycompany.motorph.model;

import java.time.LocalDate;

public class PayrollPeriodOption {

    public enum Type {
        SEMI_MONTHLY,
        MONTHLY
    }

    private final String key;
    private final String label;
    private final Type type;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public PayrollPeriodOption(String key, String label, Type type, LocalDate startDate, LocalDate endDate) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return label;
    }
}

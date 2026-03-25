package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.PayrollDAO;
import com.mycompany.motorph.model.Payslip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PayrollRecordService {

    private final PayrollDAO payrollDAO;

    public PayrollRecordService() {
        this(new PayrollDAO());
    }

    public PayrollRecordService(PayrollDAO payrollDAO) {
        this.payrollDAO = Objects.requireNonNull(payrollDAO, "payrollDAO");
    }

    public void savePayrolls(List<Payslip> payslips) {
        payrollDAO.savePayrolls(payslips);
    }

    public List<String[]> getPayrollHistory() {
        return getPayrollHistory(0);
    }

    public List<String[]> getPayrollHistory(int employeeNumber) {
        return new ArrayList<>(payrollDAO.getPayrollHistory(employeeNumber));
    }

    public boolean hasPayrollForPeriod(String periodStart, String periodEnd) {
        for (String[] row : payrollDAO.getPayrollHistory(0)) {
            if (row.length < 4) {
                continue;
            }
            if (row[2].trim().equals(periodStart) && row[3].trim().equals(periodEnd)) {
                return true;
            }
        }
        return false;
    }
}

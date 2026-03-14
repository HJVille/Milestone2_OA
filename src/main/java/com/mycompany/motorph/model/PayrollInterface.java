package com.mycompany.motorph.model;

import java.util.List;

/**
 *
 * @author heartvillegas
 */
public interface PayrollInterface {
    
public void savePayroll(Payslip payslip);
public List<String[]> getPayrollHistory(int employeeNumber);
    
}

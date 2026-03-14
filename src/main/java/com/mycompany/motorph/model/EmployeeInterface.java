package com.mycompany.motorph.model;

import java.util.List;

/**
 *
 * @author heartvillegas
 */
public interface EmployeeInterface {
    
public List<Employee> loadEmployees(String filePath);
public void addEmployee(Employee employee, String filePath);
public void updateEmployee(int employeeNumber,
                               Employee updatedEmployee,
                               List<Employee> employees,
                               String filePath);
public void deleteEmployee(int employeeNumber,
                               List<Employee> employees,
                               String filePath);
public void saveEmployees(List<Employee> employees, String filePath);
public String toCSV(Employee emp);
public String clean(String value);

    
}

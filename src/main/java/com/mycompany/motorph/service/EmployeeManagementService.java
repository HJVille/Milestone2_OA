package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.CsvFilePaths;
import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeManagementService {

    private final EmployeeDAO employeeDAO;
    private final String employeeFilePath;

    public EmployeeManagementService() {
        this(new EmployeeDAO(), CsvFilePaths.EMPLOYEES.toString());
    }

    public EmployeeManagementService(EmployeeDAO employeeDAO, String employeeFilePath) {
        this.employeeDAO = Objects.requireNonNull(employeeDAO, "employeeDAO");
        this.employeeFilePath = Objects.requireNonNull(employeeFilePath, "employeeFilePath");
    }

    public List<Employee> loadEmployees() {
        return new ArrayList<>(employeeDAO.loadEmployees(employeeFilePath));
    }

    public void addEmployee(Employee employee) {
        employeeDAO.addEmployee(employee, employeeFilePath);
    }

    public void updateEmployee(int employeeNumber,
                               Employee updatedEmployee,
                               List<Employee> employees) {
        employeeDAO.updateEmployee(
                employeeNumber,
                updatedEmployee,
                new ArrayList<>(employees),
                employeeFilePath
        );
    }

    public void deleteEmployee(int employeeNumber, List<Employee> employees) {
        employeeDAO.deleteEmployee(employeeNumber, new ArrayList<>(employees), employeeFilePath);
    }
}

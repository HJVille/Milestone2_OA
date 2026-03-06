/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private static final String HEADER =
            "EmployeeNumber,LastName,FirstName,BirthDate,Address,Phone,SSS,Philhealth,TIN,Pagibig,Status,Position,Supervisor,BasicSalary,RiceSubsidy,PhoneAllowance,ClothingAllowance,GrossSemiMonthlyRate,HourlyRate";

    public List<Employee> loadEmployees(String filePath) {

        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length < 19) continue;

                int employeeNumber = Integer.parseInt(clean(data[0]));

                String lastName = clean(data[1]);
                String firstName = clean(data[2]);
                String birthDate = clean(data[3]);
                String address = clean(data[4]);
                String phone = clean(data[5]);

                String sss = clean(data[6]);
                String philhealth = clean(data[7]);
                String tin = clean(data[8]);
                String pagibig = clean(data[9]);

                String status = clean(data[10]);
                String position = clean(data[11]);
                String supervisor = clean(data[12]);

                double basicSalary = parseDoubleSafe(clean(data[13]));
                double riceSubsidy = parseDoubleSafe(clean(data[14]));
                double phoneAllowance = parseDoubleSafe(clean(data[15]));
                double clothingAllowance = parseDoubleSafe(clean(data[16]));

                double grossSemiMonthlyRate = parseDoubleSafe(clean(data[17]));
                double hourlyRate = parseDoubleSafe(clean(data[18]));

                Employee emp = new Employee(
                        employeeNumber,
                        lastName,
                        firstName,
                        birthDate,
                        address,
                        phone,
                        sss,
                        philhealth,
                        tin,
                        pagibig,
                        status,
                        position,
                        supervisor,
                        basicSalary,
                        riceSubsidy,
                        phoneAllowance,
                        clothingAllowance,
                        grossSemiMonthlyRate,
                        hourlyRate
                );

                employees.add(emp);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    // ADD EMPLOYEE

    public void addEmployee(Employee employee, String filePath) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {

            bw.write("\n" + toCSV(employee));

            System.out.println("Employee added successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // UPDATE EMPLOYEE

    public void updateEmployee(int employeeNumber,
                               Employee updatedEmployee,
                               List<Employee> employees,
                               String filePath) {

        for (int i = 0; i < employees.size(); i++) {

            if (employees.get(i).getEmployeeNumber() == employeeNumber) {

                employees.set(i, updatedEmployee);
                break;

            }

        }

        saveEmployees(employees, filePath);

        System.out.println("Employee updated successfully.");

    }

    // DELETE EMPLOYEE

    public void deleteEmployee(int employeeNumber,
                               List<Employee> employees,
                               String filePath) {

        employees.removeIf(emp -> emp.getEmployeeNumber() == employeeNumber);

        saveEmployees(employees, filePath);

        System.out.println("Employee deleted successfully.");

    }

    // SAVE EMPLOYEES (FULL CSV SAFE)

    private void saveEmployees(List<Employee> employees, String filePath) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            bw.write(HEADER);

            for (Employee emp : employees) {

                bw.write("\n" + toCSV(emp));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // CONVERT EMPLOYEE → CSV ROW

    private String toCSV(Employee emp) {

        return emp.getEmployeeNumber() + ","
                + emp.getLastName() + ","
                + emp.getFirstName() + ","
                + emp.getBirthDate() + ","
                + emp.getAddress() + ","
                + emp.getPhone() + ","
                + emp.getSss() + ","
                + emp.getPhilhealth() + ","
                + emp.getTin() + ","
                + emp.getPagibig() + ","
                + emp.getStatus() + ","
                + emp.getPosition() + ","
                + emp.getSupervisor() + ","
                + emp.getBasicSalary() + ","
                + emp.getRiceSubsidy() + ","
                + emp.getPhoneAllowance() + ","
                + emp.getClothingAllowance() + ","
                + emp.getGrossSemiMonthlyRate() + ","
                + emp.getHourlyRate();

    }

    // CLEAN CSV VALUE

    private String clean(String value) {

        if (value == null) return "";

        value = value.trim();

        if (value.startsWith("\"") && value.endsWith("\"")) {

            value = value.substring(1, value.length() - 1);

        }

        return value;

    }

    // SAFE DOUBLE PARSER

    private double parseDoubleSafe(String value) {

        if (value == null) return 0.0;

        value = value.trim();

        if (value.equalsIgnoreCase("N/A")
                || value.equals("-")
                || value.isEmpty()) {

            return 0.0;

        }

        try {

            value = value.replace(",", "");

            return Double.parseDouble(value);

        } catch (Exception e) {

            return 0.0;

        }

    }

}
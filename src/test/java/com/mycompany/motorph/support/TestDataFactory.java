package com.mycompany.motorph.support;

import com.mycompany.motorph.model.Employee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestDataFactory {

    private static final String EMPLOYEE_HEADER =
            "EmployeeNumber,LastName,FirstName,BirthDate,Address,Phone,SSS,Philhealth,TIN,Pagibig,Status,Position,Supervisor,BasicSalary,RiceSubsidy,PhoneAllowance,ClothingAllowance,GrossSemiMonthlyRate,HourlyRate";
    private static final String ATTENDANCE_HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    private TestDataFactory() {
    }

    public static Employee employee(int employeeNumber, String firstName, String lastName) {
        return new Employee(
                employeeNumber,
                lastName,
                firstName,
                "01/15/1995",
                "123 Rizal Ave, Manila",
                "091-123-456",
                "12-3456789-0",
                "12-345678901-2",
                "123-456-789-000",
                "1234-5678-9012",
                "Regular",
                "Software Engineer",
                "Maria Santos",
                30000.0,
                1500.0,
                1000.0,
                1000.0,
                15000.0,
                170.45
        );
    }

    public static void writeEmployeesCsv(Path path, Employee... employees) throws IOException {
        StringBuilder builder = new StringBuilder(EMPLOYEE_HEADER);
        for (Employee employee : employees) {
            builder.append(System.lineSeparator())
                    .append(employee.getEmployeeNumber()).append(",")
                    .append(employee.getLastName()).append(",")
                    .append(employee.getFirstName()).append(",")
                    .append(employee.getBirthDate()).append(",")
                    .append(escape(employee.getAddress())).append(",")
                    .append(employee.getPhone()).append(",")
                    .append(employee.getSss()).append(",")
                    .append(employee.getPhilhealth()).append(",")
                    .append(employee.getTin()).append(",")
                    .append(employee.getPagibig()).append(",")
                    .append(employee.getStatus()).append(",")
                    .append(employee.getPosition()).append(",")
                    .append(employee.getSupervisor()).append(",")
                    .append(employee.getBasicSalary()).append(",")
                    .append(employee.getRiceSubsidy()).append(",")
                    .append(employee.getPhoneAllowance()).append(",")
                    .append(employee.getClothingAllowance()).append(",")
                    .append(employee.getGrossSemiMonthlyRate()).append(",")
                    .append(employee.getHourlyRate());
        }
        write(path, builder.toString());
    }

    public static void writeAttendanceCsv(Path path, String... rows) throws IOException {
        StringBuilder builder = new StringBuilder(ATTENDANCE_HEADER);
        for (String row : rows) {
            builder.append(System.lineSeparator()).append(row);
        }
        write(path, builder.toString());
    }

    private static void write(Path path, String content) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, content);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

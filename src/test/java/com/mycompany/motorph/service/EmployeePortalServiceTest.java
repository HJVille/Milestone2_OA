package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.EmployeeDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeePayrollSummary;
import com.mycompany.motorph.model.PayrollPeriodOption;
import com.mycompany.motorph.support.TestDataFactory;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeePortalServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void buildsPayrollPeriodsAndSummariesFromAttendanceData() throws Exception {
        Path employeesFile = tempDir.resolve("employees.csv");
        Path attendanceFile = tempDir.resolve("attendance.csv");

        Employee employee = TestDataFactory.employee(10001, "Alice", "Dela Cruz");
        TestDataFactory.writeEmployeesCsv(employeesFile, employee);
        TestDataFactory.writeAttendanceCsv(
                attendanceFile,
                "10001,Dela Cruz,Alice,03/05/2026,8:00,17:00",
                "10001,Dela Cruz,Alice,03/20/2026,8:30,17:30"
        );

        EmployeePortalService portalService = new EmployeePortalService(
                new EmployeeDAO(),
                employeesFile.toString(),
                attendanceFile,
                new PayrollComputationService()
        );

        List<PayrollPeriodOption> periods = portalService.getAvailablePayrollPeriods(employee.getEmployeeNumber());
        assertEquals(
                List.of("SEMI:2026-03-16", "MONTHLY:2026-03", "SEMI:2026-03-01"),
                periods.stream().map(PayrollPeriodOption::getKey).toList()
        );

        EmployeePayrollSummary firstHalf = portalService.getPayrollSummary(employee.getEmployeeNumber(), "SEMI:2026-03-01");
        assertEquals(15000.0, firstHalf.getGrossSalary());
        assertEquals(15000.0, firstHalf.getNetSalary());
        assertEquals(1, firstHalf.getAttendanceDays());
        assertEquals(8.0, firstHalf.getAttendanceHours());

        EmployeePayrollSummary secondHalf = portalService.getPayrollSummary(employee.getEmployeeNumber(), "SEMI:2026-03-16");
        assertEquals(18500.0, secondHalf.getGrossSalary());
        assertEquals(3713.4, secondHalf.getTotalDeductions());
        assertEquals(14786.6, secondHalf.getNetSalary());

        EmployeePayrollSummary monthly = portalService.getPayrollSummary(employee.getEmployeeNumber(), "MONTHLY:2026-03");
        assertEquals(33500.0, monthly.getGrossSalary());
        assertEquals(29786.6, monthly.getNetSalary());
        assertEquals(2, monthly.getAttendanceDays());
        assertEquals(16.0, monthly.getAttendanceHours());
        assertEquals(1, portalService.buildPayslipsForPeriod("MONTHLY:2026-03").size());
    }
}

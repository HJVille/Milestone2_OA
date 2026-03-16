package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeUser;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.support.TestDataFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void changePasswordPersistsToUserStoreAndAuditLog() throws Exception {
        Path usersFile = tempDir.resolve("users.csv");
        Path employeesFile = tempDir.resolve("employees.csv");
        Path auditFile = tempDir.resolve("password_audit.csv");

        Employee employee = TestDataFactory.employee(10001, "Alice", "Dela Cruz");
        TestDataFactory.writeEmployeesCsv(employeesFile, employee);

        UserDAO userDAO = new UserDAO(usersFile);
        List<User> users = new ArrayList<>();
        users.add(new EmployeeUser("alice", "oldPass123", employee.getEmployeeNumber()));
        userDAO.saveUsers(users);

        AuthService authService = new AuthService(userDAO, employeesFile, auditFile, fixedClock(2026, 3, 16, 9, 30));

        assertTrue(authService.changePassword(users.getFirst(), "oldPass123", "newPass456", users));
        assertEquals("newPass456", userDAO.loadUsers().getFirst().getPassword());
        assertTrue(Files.readString(auditFile).contains("alice,CHANGE_PASSWORD"));
    }

    @Test
    void resetsPasswordWhenGovernmentIdsMatch() throws Exception {
        Path usersFile = tempDir.resolve("users.csv");
        Path employeesFile = tempDir.resolve("employees.csv");
        Path auditFile = tempDir.resolve("password_audit.csv");

        Employee employee = TestDataFactory.employee(10001, "Alice", "Dela Cruz");
        TestDataFactory.writeEmployeesCsv(employeesFile, employee);

        UserDAO userDAO = new UserDAO(usersFile);
        List<User> users = new ArrayList<>();
        users.add(new EmployeeUser("alice", "emp10001", employee.getEmployeeNumber()));
        userDAO.saveUsers(users);

        AuthService authService = new AuthService(userDAO, employeesFile, auditFile, fixedClock(2026, 3, 16, 10, 0));

        assertTrue(authService.resetPasswordWithGovernmentIds(
                employee.getEmployeeNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getSss(),
                employee.getPhilhealth(),
                employee.getTin(),
                employee.getPagibig(),
                "verifiedPass789",
                users
        ));

        assertEquals("verifiedPass789", userDAO.loadUsers().getFirst().getPassword());
        assertTrue(Files.readString(auditFile).contains("alice,FORGOT_PASSWORD"));
    }

    @Test
    void rejectsBlankResetPasswordEvenWhenEmployeeMatches() {
        UserDAO userDAO = new UserDAO(tempDir.resolve("users.csv"));
        Employee employee = TestDataFactory.employee(10001, "Alice", "Dela Cruz");
        List<User> users = new ArrayList<>();
        users.add(new EmployeeUser("alice", "emp10001", employee.getEmployeeNumber()));

        AuthService authService = new AuthService(
                userDAO,
                tempDir.resolve("employees.csv"),
                tempDir.resolve("password_audit.csv"),
                fixedClock(2026, 3, 16, 11, 0)
        );

        assertFalse(authService.resetPassword(
                "alice",
                employee.getEmployeeName(),
                "   ",
                users,
                List.of(employee)
        ));
    }

    private Clock fixedClock(int year, int month, int day, int hour, int minute) {
        return Clock.fixed(
                ZonedDateTime.of(year, month, day, hour, minute, 0, 0, AppClock.zone()).toInstant(),
                AppClock.zone()
        );
    }
}

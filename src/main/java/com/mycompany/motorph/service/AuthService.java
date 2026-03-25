package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.CsvFilePaths;
import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userRepository;
    private final Path employeesPath;
    private final Path auditPath;
    private final Clock clock;
    private final PasswordHashService passwordHashService;

    public AuthService() {
        this(
                new UserDAO(),
                CsvFilePaths.EMPLOYEES,
                CsvFilePaths.resolve("password_audit.csv"),
                AppClock.clock(),
                new PasswordHashService()
        );
    }

    public AuthService(UserDAO userRepository, Path employeesPath, Path auditPath, Clock clock) {
        this(userRepository, employeesPath, auditPath, clock, new PasswordHashService());
    }

    public AuthService(UserDAO userRepository,
                       Path employeesPath,
                       Path auditPath,
                       Clock clock,
                       PasswordHashService passwordHashService) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.employeesPath = Objects.requireNonNull(employeesPath, "employeesPath");
        this.auditPath = Objects.requireNonNull(auditPath, "auditPath");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.passwordHashService = Objects.requireNonNull(passwordHashService, "passwordHashService");
    }

    public User login(String username, String password, List<User> users) {
        return login(username, password, users, 1);
    }

    public User login(String username, String password, List<User> users, int attempt) {
        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                continue;
            }
            if (!matchesStoredPassword(user, password)) {
                continue;
            }
            migratePasswordIfNeeded(user, password, users);
            return user;
        }
        return null;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword, List<User> users) {
        User storedUser = resolveStoredUser(user, users);
        User passwordSource = storedUser != null ? storedUser : user;

        if (passwordSource == null || users == null || users.isEmpty()) {
            return false;
        }
        if (!matchesSuppliedCurrentPassword(passwordSource, oldPassword)) {
            return false;
        }
        if (isBlank(newPassword) || matchesStoredPassword(passwordSource, newPassword)) {
            return false;
        }

        String hashedPassword = passwordHashService.hash(newPassword);
        passwordSource.setPassword(hashedPassword);
        if (user != null && user != passwordSource) {
            user.setPassword(hashedPassword);
        }
        userRepository.saveUsers(users);
        recordPasswordChange(passwordSource.getUsername(), "CHANGE_PASSWORD");
        return true;
    }

    public boolean resetPasswordWithGovernmentIds(int employeeNumber,
                                                  String firstName,
                                                  String lastName,
                                                  String sss,
                                                  String philhealth,
                                                  String tin,
                                                  String pagibig,
                                                  String newPassword,
                                                  List<User> users) {
        User targetUser = findEmployeeUser(employeeNumber, users);
        if (targetUser == null || isBlank(newPassword) || matchesStoredPassword(targetUser, newPassword)) {
            return false;
        }

        EmployeeIdentityRecord identityRecord = findEmployeeIdentity(employeeNumber);
        if (identityRecord == null || !identityRecord.matches(firstName, lastName, sss, philhealth, tin, pagibig)) {
            return false;
        }

        targetUser.setPassword(passwordHashService.hash(newPassword));
        userRepository.saveUsers(users);
        recordPasswordChange(targetUser.getUsername(), "FORGOT_PASSWORD");
        return true;
    }

    public boolean resetPassword(String username,
                                 String fullName,
                                 String newPassword,
                                 List<User> users,
                                 List<Employee> employees) {
        User targetUser = null;
        for (User candidate : users) {
            if (candidate.getUsername().equals(username)) {
                targetUser = candidate;
                break;
            }
        }

        if (targetUser == null
                || !"EMPLOYEE".equals(targetUser.getRole())
                || isBlank(newPassword)
                || matchesStoredPassword(targetUser, newPassword)) {
            return false;
        }

        Employee employee = null;
        for (Employee candidate : employees) {
            if (candidate.getEmployeeNumber() == targetUser.getEmployeeNumber()) {
                employee = candidate;
                break;
            }
        }

        if (employee == null) {
            return false;
        }

        String csvName = normalizeEmployeeName(employee.getEmployeeName());
        String inputName = normalizeEmployeeName(fullName);
        if (!csvName.contains(inputName) && !inputName.contains(csvName)) {
            return false;
        }

        targetUser.setPassword(passwordHashService.hash(newPassword));
        userRepository.saveUsers(users);
        recordPasswordChange(targetUser.getUsername(), "FORGOT_PASSWORD");
        return true;
    }

    public boolean isDefaultPassword(User user) {
        if (user == null) {
            return false;
        }
        return matchesStoredPassword(user, "emp" + user.getEmployeeNumber());
    }

    private boolean matchesStoredPassword(User user, String candidatePassword) {
        return user != null
                && !isBlank(candidatePassword)
                && passwordHashService.matches(candidatePassword, user.getPassword());
    }

    private boolean matchesSuppliedCurrentPassword(User user, String suppliedPassword) {
        return user != null
                && !isBlank(suppliedPassword)
                && (user.getPassword().equals(suppliedPassword) || matchesStoredPassword(user, suppliedPassword));
    }

    private void migratePasswordIfNeeded(User user, String rawPassword, List<User> users) {
        if (user == null || isBlank(rawPassword) || passwordHashService.isHashed(user.getPassword())) {
            return;
        }

        user.setPassword(passwordHashService.hash(rawPassword));
        userRepository.saveUsers(users);
    }

    private User findEmployeeUser(int employeeNumber, List<User> users) {
        for (User user : users) {
            if ("EMPLOYEE".equalsIgnoreCase(user.getRole()) && user.getEmployeeNumber() == employeeNumber) {
                return user;
            }
        }
        return null;
    }

    private User resolveStoredUser(User user, List<User> users) {
        if (user == null || users == null) {
            return null;
        }

        for (User candidate : users) {
            if (candidate == user) {
                return candidate;
            }
            if (candidate.getEmployeeNumber() == user.getEmployeeNumber()
                    && candidate.getUsername().equalsIgnoreCase(user.getUsername())
                    && candidate.getRole().equalsIgnoreCase(user.getRole())) {
                return candidate;
            }
        }
        return null;
    }

    private EmployeeIdentityRecord findEmployeeIdentity(int employeeNumber) {
        try (BufferedReader reader = Files.newBufferedReader(employeesPath)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 18) {
                    continue;
                }

                int currentEmployeeNumber = parseIntSafe(data[0]);
                if (currentEmployeeNumber != employeeNumber) {
                    continue;
                }

                int length = data.length;
                return new EmployeeIdentityRecord(
                        clean(data[2]),
                        clean(data[1]),
                        clean(data[length - 13]),
                        clean(data[length - 12]),
                        clean(data[length - 11]),
                        clean(data[length - 10])
                );
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to read employee identity records.", e);
        }
        return null;
    }

    private void recordPasswordChange(String username, String action) {
        try {
            if (auditPath.getParent() != null) {
                Files.createDirectories(auditPath.getParent());
            }

            boolean fileExists = Files.exists(auditPath);
            try (java.io.BufferedWriter writer = Files.newBufferedWriter(
                    auditPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                if (!fileExists) {
                    writer.write("timestamp,username,action");
                    writer.newLine();
                }

                writer.write(LocalDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        + "," + username + "," + action);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to record password audit entry.", e);
        }
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(clean(value));
        } catch (Exception e) {
            return -1;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replace("\"", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeEmployeeName(String value) {
        return value == null
                ? ""
                : value.toLowerCase().replace(",", " ").replaceAll("\\s+", " ").trim();
    }

    private static class EmployeeIdentityRecord {

        private final String firstName;
        private final String lastName;
        private final String sss;
        private final String philhealth;
        private final String tin;
        private final String pagibig;

        private EmployeeIdentityRecord(String firstName,
                                       String lastName,
                                       String sss,
                                       String philhealth,
                                       String tin,
                                       String pagibig) {
            this.firstName = normalizeName(firstName);
            this.lastName = normalizeName(lastName);
            this.sss = normalizeIdentifier(sss);
            this.philhealth = normalizeIdentifier(philhealth);
            this.tin = normalizeIdentifier(tin);
            this.pagibig = normalizeIdentifier(pagibig);
        }

        private boolean matches(String firstName,
                                String lastName,
                                String sss,
                                String philhealth,
                                String tin,
                                String pagibig) {
            return this.firstName.equals(normalizeName(firstName))
                    && this.lastName.equals(normalizeName(lastName))
                    && this.sss.equals(normalizeIdentifier(sss))
                    && this.philhealth.equals(normalizeIdentifier(philhealth))
                    && this.tin.equals(normalizeIdentifier(tin))
                    && this.pagibig.equals(normalizeIdentifier(pagibig));
        }

        private static String normalizeName(String value) {
            return value == null ? "" : value.trim().toLowerCase().replaceAll("\\s+", "");
        }

        private static String normalizeIdentifier(String value) {
            return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        }
    }
}

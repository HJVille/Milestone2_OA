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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userRepository = new UserDAO();

    public User login(String username, String password, List<User> users) {
        return login(username, password, users, 1);
    }

    public User login(String username, String password, List<User> users, int attempt) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword, List<User> users) {
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        if (isBlank(newPassword) || newPassword.equals(oldPassword)) {
            return false;
        }

        user.setPassword(newPassword);
        userRepository.saveUsers(users);
        recordPasswordChange(user.getUsername(), "CHANGE_PASSWORD");
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
        if (targetUser == null || isBlank(newPassword) || newPassword.equals(targetUser.getPassword())) {
            return false;
        }

        EmployeeIdentityRecord identityRecord = findEmployeeIdentity(employeeNumber);
        if (identityRecord == null || !identityRecord.matches(firstName, lastName, sss, philhealth, tin, pagibig)) {
            return false;
        }

        targetUser.setPassword(newPassword);
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

        if (targetUser == null || !"EMPLOYEE".equals(targetUser.getRole())) {
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

        targetUser.setPassword(newPassword);
        userRepository.saveUsers(users);
        recordPasswordChange(targetUser.getUsername(), "FORGOT_PASSWORD");
        return true;
    }

    public boolean isDefaultPassword(User user) {
        if (user == null) {
            return false;
        }
        return user.getPassword().equals("emp" + user.getEmployeeNumber());
    }

    private User findEmployeeUser(int employeeNumber, List<User> users) {
        for (User user : users) {
            if ("EMPLOYEE".equalsIgnoreCase(user.getRole()) && user.getEmployeeNumber() == employeeNumber) {
                return user;
            }
        }
        return null;
    }

    private EmployeeIdentityRecord findEmployeeIdentity(int employeeNumber) {
        try (BufferedReader reader = Files.newBufferedReader(CsvFilePaths.EMPLOYEES)) {
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
        Path auditPath = CsvFilePaths.resolve("password_audit.csv");
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

                writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
            this.firstName = normalize(firstName);
            this.lastName = normalize(lastName);
            this.sss = normalize(sss);
            this.philhealth = normalize(philhealth);
            this.tin = normalize(tin);
            this.pagibig = normalize(pagibig);
        }

        private boolean matches(String firstName,
                                String lastName,
                                String sss,
                                String philhealth,
                                String tin,
                                String pagibig) {
            return this.firstName.equals(normalize(firstName))
                    && this.lastName.equals(normalize(lastName))
                    && this.sss.equals(normalize(sss))
                    && this.philhealth.equals(normalize(philhealth))
                    && this.tin.equals(normalize(tin))
                    && this.pagibig.equals(normalize(pagibig));
        }

        private static String normalize(String value) {
            return value == null ? "" : value.trim().toLowerCase().replaceAll("\\s+", "");
        }
    }
}

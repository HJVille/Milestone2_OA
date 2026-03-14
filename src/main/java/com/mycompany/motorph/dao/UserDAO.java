package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.AdminUser;
import com.mycompany.motorph.model.EmployeeUser;
import com.mycompany.motorph.model.FinanceUser;
import com.mycompany.motorph.model.HRUser;
import com.mycompany.motorph.model.ItUser;
import com.mycompany.motorph.model.User;
import com.mycompany.motorph.model.UserInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO implements UserInterface {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final Path PRIMARY_FILE_PATH = CsvFilePaths.USERS;
    private static final Path LEGACY_FILE_PATH = CsvFilePaths.resolve("users.csv");

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        Path filePath = resolveFilePath();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                String username = data[0].trim();
                String password = data[1].trim();
                String role = data[2].trim();
                int employeeNumber = Integer.parseInt(data[3].trim());

                users.add(createUser(username, password, role, employeeNumber));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load users from " + filePath, e);
        }

        return users;
    }

    public void saveUsers(List<User> users) {
        Path filePath = resolveWritePath();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to prepare user file " + filePath, e);
            return;
        }

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println("username,password,role,employeeNumber");
            for (User user : users) {
                writer.println(user.getUsername() + ","
                        + user.getPassword() + ","
                        + user.getRole() + ","
                        + user.getEmployeeNumber());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to save users to " + filePath, e);
        }
    }

    public User findUser(String username, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private User createUser(String username, String password, String role, int employeeNumber) {
        switch (role.toUpperCase()) {
            case "HR":
                return new HRUser(username, password, employeeNumber);
            case "PAYROLL":
            case "FINANCE":
                return new FinanceUser(username, password, employeeNumber);
            case "IT":
                return new ItUser(username, password, employeeNumber);
            case "ADMIN":
                return new AdminUser(username, password, employeeNumber);
            case "EMPLOYEE":
            default:
                return new EmployeeUser(username, password, employeeNumber);
        }
    }

    private Path resolveFilePath() {
        if (Files.exists(PRIMARY_FILE_PATH)) {
            return PRIMARY_FILE_PATH;
        }
        return LEGACY_FILE_PATH;
    }

    private Path resolveWritePath() {
        if (Files.exists(PRIMARY_FILE_PATH) || !Files.exists(LEGACY_FILE_PATH)) {
            return PRIMARY_FILE_PATH;
        }
        return LEGACY_FILE_PATH;
    }
}

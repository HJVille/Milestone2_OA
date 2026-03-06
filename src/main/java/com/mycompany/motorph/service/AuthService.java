/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.dao.UserDAO;

import java.util.List;

public class AuthService {

    private static final int MAX_ATTEMPTS = 5;

    private UserDAO userRepository = new UserDAO();

    // =====================================================
    // LOGIN
    // =====================================================

    public User login(String username, String password, List<User> users, int attempt) {

        for (User user : users) {

            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {

                return user;

            }

        }

        System.out.println("Invalid username or password.");
        System.out.println("Attempt " + attempt + " of " + MAX_ATTEMPTS);

        return null;

    }

    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    public boolean changePassword(User user,
                                  String oldPassword,
                                  String newPassword,
                                  List<User> users) {

        if (!user.getPassword().equals(oldPassword)) {

            System.out.println("Current password incorrect.");
            return false;

        }

        if (newPassword == null || newPassword.trim().isEmpty()) {

            System.out.println("New password cannot be empty.");
            return false;

        }

        if (newPassword.equals(oldPassword)) {

            System.out.println("New password cannot be the same as current password.");
            return false;

        }

        user.setPassword(newPassword);

        userRepository.saveUsers(users);

        System.out.println("Password updated successfully.");

        return true;

    }

    // =====================================================
    // FORGOT PASSWORD (FULL NAME VERIFICATION)
    // =====================================================

    public boolean resetPassword(String username,
                                 String fullName,
                                 String newPassword,
                                 List<User> users,
                                 List<Employee> employees) {

        User targetUser = null;

        // Find user
        for (User u : users) {

            if (u.getUsername().equals(username)) {
                targetUser = u;
                break;
            }

        }

        if (targetUser == null) {

            System.out.println("User not found.");
            return false;

        }

        // Only employees can self reset
        if (!targetUser.getRole().equals("EMPLOYEE")) {

            System.out.println("This account requires administrator reset.");
            return false;

        }

        Employee employee = null;

        // Find employee
        for (Employee emp : employees) {

            if (emp.getEmployeeNumber() == targetUser.getEmployeeNumber()) {

                employee = emp;
                break;

            }

        }

        if (employee == null) {

            System.out.println("Employee record not found.");
            return false;

        }

        // ======================================
        // NORMALIZE NAME COMPARISON
        // ======================================

        String csvName = employee.getEmployeeName()
                .toLowerCase()
                .replace(",", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String inputName = fullName
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        if (!csvName.contains(inputName) && !inputName.contains(csvName)) {

            System.out.println("Identity verification failed.");
            return false;

        }

        // ======================================
        // UPDATE PASSWORD
        // ======================================

        targetUser.setPassword(newPassword);

        userRepository.saveUsers(users);

        System.out.println("Identity verified.");
        System.out.println("Password reset successfully.");

        return true;

    }

    // =====================================================
    // CHECK DEFAULT PASSWORD
    // =====================================================

    public boolean isDefaultPassword(User user) {

        if (user == null) {
            return false;
        }

        String defaultPassword = "emp" + user.getEmployeeNumber();

        return user.getPassword().equals(defaultPassword);

    }

}
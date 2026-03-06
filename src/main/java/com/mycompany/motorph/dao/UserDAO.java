/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.User;
import com.mycompany.motorph.model.EmployeeUser;
import com.mycompany.motorph.model.HRUser;
import com.mycompany.motorph.model.PayrollUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final String FILE_PATH = "users.csv";

    // ======================================
    // LOAD USERS FROM CSV
    // ======================================

    public List<User> loadUsers() {

        List<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            // skip header
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                String username = data[0].trim();
                String password = data[1].trim();
                String role = data[2].trim();
                int employeeNumber = Integer.parseInt(data[3].trim());

                User user;

                switch (role.toUpperCase()) {

                    case "HR":
                        user = new HRUser(username, password, employeeNumber);
                        break;

                    case "PAYROLL":
                        user = new PayrollUser(username, password, employeeNumber);
                        break;

                    case "EMPLOYEE":
                    default:
                        user = new EmployeeUser(username, password, employeeNumber);
                        break;
                }

                users.add(user);

            }

        } catch (Exception e) {

            System.out.println("Error loading users.csv");
            e.printStackTrace();

        }

        return users;
    }

    // ======================================
    // SAVE USERS BACK TO CSV
    // ======================================

    public void saveUsers(List<User> users) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {

            pw.println("username,password,role,employeeNumber");

            for (User user : users) {

                pw.println(
                        user.getUsername() + "," +
                        user.getPassword() + "," +
                        user.getRole() + "," +
                        user.getEmployeeNumber()
                );

            }

        } catch (Exception e) {

            System.out.println("Error saving users.csv");
            e.printStackTrace();

        }

    }

    // ======================================
    // FIND USER BY USERNAME
    // ======================================

    public User findUser(String username, List<User> users) {

        for (User user : users) {

            if (user.getUsername().equals(username)) {
                return user;
            }

        }

        return null;
    }

}
package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.UserDAO;
import com.mycompany.motorph.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserAccountService {

    private final UserDAO userDAO;
    private final PasswordHashService passwordHashService;

    public UserAccountService() {
        this(new UserDAO(), new PasswordHashService());
    }

    public UserAccountService(UserDAO userDAO) {
        this(userDAO, new PasswordHashService());
    }

    public UserAccountService(UserDAO userDAO, PasswordHashService passwordHashService) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
        this.passwordHashService = Objects.requireNonNull(passwordHashService, "passwordHashService");
    }

    public List<User> loadUsers() {
        return new ArrayList<>(userDAO.loadUsers());
    }

    public void saveUsers(List<User> users) {
        userDAO.saveUsers(users);
    }

    public void resetPasswordToDefault(User user, List<User> users) {
        user.setPassword(passwordHashService.hash("emp" + user.getEmployeeNumber()));
        saveUsers(users);
    }

    public void updatePassword(User user, List<User> users, String newPassword) {
        user.setPassword(passwordHashService.hash(newPassword));
        saveUsers(users);
    }
}

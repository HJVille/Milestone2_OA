package com.mycompany.motorph.model;

import java.util.List;

/**
 *
 * @author heartvillegas
 */
public interface UserInterface {
    
public List<User> loadUsers();
public void saveUsers(List<User> users);
public User findUser(String username, List<User> users);
    
}

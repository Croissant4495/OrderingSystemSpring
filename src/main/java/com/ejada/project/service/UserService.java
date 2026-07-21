package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.User;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    void deleteUser(Long id);
}

package com.example.gymcrm.dao;

import com.example.gymcrm.domain.User;
import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Collection<User> findAll();
    User save(User user);
    void deleteById(Long id);
}

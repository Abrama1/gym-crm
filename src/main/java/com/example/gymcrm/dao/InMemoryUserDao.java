package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserDao implements UserDao {

    @Autowired @Qualifier("userStorage")
    private Map<Long, User> storage;

    private final AtomicLong seq = new AtomicLong(0);

    @Override public Optional<User> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername()!=null && u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override public Collection<User> findAll() { return storage.values(); }

    @Override public User save(User user) {
        if (user.getId() == null) user.setId(seq.incrementAndGet());
        storage.put(user.getId(), user);
        return user;
    }

    @Override public void deleteById(Long id) { storage.remove(id); }
}

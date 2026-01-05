package com.example.gymcrm.util;

import com.example.gymcrm.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) { this.userDao = userDao; }

    public String generateUnique(String firstName, String lastName) {
        String base = (firstName + "." + lastName).replaceAll("\\s+", "");
        String candidate = base;
        int i = 0;
        while (userDao.findByUsername(candidate).isPresent()) {
            i++;
            candidate = base + i;
        }
        return candidate;
    }
}

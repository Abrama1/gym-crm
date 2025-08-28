package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.UserDao;
import com.example.gymcrm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaUserDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return em.createQuery(
                        "select u from User u where lower(u.username) = lower(:u)", User.class)
                .setParameter("u", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Collection<User> findAll() {
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    @Transactional
    public User save(User u) {
        if (u.getId() == null) {
            em.persist(u);
            return u;
        }
        return em.merge(u);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User found = em.find(User.class, id);
        if (found != null) em.remove(found);
    }
}

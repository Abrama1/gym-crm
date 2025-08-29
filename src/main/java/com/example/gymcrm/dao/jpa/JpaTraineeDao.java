package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.entity.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaTraineeDao implements TraineeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(em.find(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return em.createQuery("""
                select t from Trainee t
                  join t.user u
                 where lower(u.username) = lower(:u)
                """, Trainee.class)
                .setParameter("u", username)
                .getResultStream().findFirst();
    }

    @Override
    public Optional<Trainee> findByUserId(Long userId) {
        return em.createQuery("""
                select t from Trainee t
                  join t.user u
                 where u.id = :id
                """, Trainee.class)
                .setParameter("id", userId)
                .getResultStream().findFirst();
    }

    @Override
    public Collection<Trainee> findAll() {
        return em.createQuery("select t from Trainee t", Trainee.class).getResultList();
    }

    @Override
    @Transactional
    public Trainee save(Trainee t) {
        if (t.getId() == null) { em.persist(t); return t; }
        return em.merge(t);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    @Transactional
    public void delete(Trainee t) {
        Trainee managed = (t.getId() != null) ? em.find(Trainee.class, t.getId()) : t;
        if (managed != null) em.remove(managed);
    }
}

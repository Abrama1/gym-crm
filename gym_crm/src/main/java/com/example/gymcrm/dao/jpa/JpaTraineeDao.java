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
    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(em.find(Trainee.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        var list = em.createQuery("""
                select t from Trainee t
                  join t.user u
                 where lower(u.username) = lower(:u)
                """, Trainee.class)
                .setParameter("u", username)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUserId(Long userId) {
        var list = em.createQuery("""
                select t from Trainee t
                  join t.user u
                 where u.id = :id
                """, Trainee.class)
                .setParameter("id", userId)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
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

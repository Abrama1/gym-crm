package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaTrainerDao implements TrainerDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(em.find(Trainer.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsername(String username) {
        var list = em.createQuery("""
                select t from Trainer t
                  join t.user u
                 where lower(u.username) = lower(:u)
                """, Trainer.class)
                .setParameter("u", username)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUserId(Long userId) {
        var list = em.createQuery("""
                select t from Trainer t
                  join t.user u
                 where u.id = :id
                """, Trainer.class)
                .setParameter("id", userId)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Trainer> findAll() {
        return em.createQuery("select t from Trainer t", Trainer.class).getResultList();
    }

    @Override
    @Transactional
    public Trainer save(Trainer t) {
        if (t.getId() == null) { em.persist(t); return t; }
        return em.merge(t);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Trainer> listNotAssignedToTrainee(String traineeUsername) {
        return em.createQuery("""
            select tr from Trainer tr
             where tr.id not in (
                 select tr2.id from Trainee t
                   join t.user u
                   join t.trainers tr2
                  where lower(u.username) = lower(:u)
             )
            """, Trainer.class)
                .setParameter("u", traineeUsername)
                .getResultList();
    }
}

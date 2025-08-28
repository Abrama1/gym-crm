package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaTrainerDao implements TrainerDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(em.find(Trainer.class, id));
    }

    @Override
    public Collection<Trainer> findAll() {
        return em.createQuery("select t from Trainer t", Trainer.class).getResultList();
    }

    @Override
    @Transactional
    public Trainer save(Trainer t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        }
        return em.merge(t);
    }
}

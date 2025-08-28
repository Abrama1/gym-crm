package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaTrainingDao implements TrainingDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(em.find(Training.class, id));
    }

    @Override
    public Collection<Training> findAll() {
        return em.createQuery("select t from Training t", Training.class).getResultList();
    }

    @Override
    @Transactional
    public Training save(Training t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        }
        return em.merge(t);
    }
}

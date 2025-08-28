package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaTrainingTypeDao implements TrainingTypeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<TrainingType> findByName(String name) {
        return em.createQuery(
                        "select t from TrainingType t where t.name = :n", TrainingType.class)
                .setParameter("n", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Collection<TrainingType> findAll() {
        return em.createQuery("select t from TrainingType t", TrainingType.class).getResultList();
    }

    @Override
    @Transactional
    public TrainingType save(TrainingType t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        }
        return em.merge(t);
    }
}

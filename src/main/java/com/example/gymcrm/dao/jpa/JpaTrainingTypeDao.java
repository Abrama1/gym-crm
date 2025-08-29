package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaTrainingTypeDao implements TrainingTypeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> findByName(String name) {
        var list = em.createQuery(
                        "select t from TrainingType t where t.name = :n", TrainingType.class)
                .setParameter("n", name)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TrainingType> findAll() {
        return em.createQuery("select t from TrainingType t", TrainingType.class).getResultList();
    }

    @Override
    @Transactional
    public TrainingType save(TrainingType t) {
        if (t.getId() == null) { em.persist(t); return t; }
        return em.merge(t);
    }
}

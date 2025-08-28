package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.entity.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaTraineeDao implements TraineeDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(em.find(Trainee.class, id));
    }

    @Override
    public Collection<Trainee> findAll() {
        return em.createQuery("select t from Trainee t", Trainee.class).getResultList();
    }

    @Override
    @Transactional
    public Trainee save(Trainee t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        }
        return em.merge(t);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Trainee found = em.find(Trainee.class, id);
        if (found != null) em.remove(found);
    }
}

package com.example.gymcrm.dao.jpa;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
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
        if (t.getId() == null) { em.persist(t); return t; }
        return em.merge(t);
    }

    @Override
    public Collection<Training> listForTrainee(String traineeUsername, LocalDateTime from, LocalDateTime to,
                                               String trainerNameLike, String trainingType) {
        var q = em.createQuery("""
            select tr from Training tr
              join tr.trainee tt
              join tt.user tu
              join tr.trainer trn
              join trn.user tru
              join tr.trainingType ttype
             where lower(tu.username) = lower(:u)
               and (:from is null or tr.trainingDate >= :from)
               and (:to   is null or tr.trainingDate <  :to)
               and (:type is null or ttype.name = :type)
               and (:tname is null or lower(concat(tru.firstName,' ',tru.lastName)) like :tname)
             order by tr.trainingDate desc
            """, Training.class);
        q.setParameter("u", traineeUsername);
        q.setParameter("from", from);
        q.setParameter("to", to);
        q.setParameter("type", trainingType);
        q.setParameter("tname", trainerNameLike);
        return q.getResultList();
    }

    @Override
    public Collection<Training> listForTrainer(String trainerUsername, LocalDateTime from, LocalDateTime to,
                                               String traineeNameLike, String trainingType) {
        var q = em.createQuery("""
            select tr from Training tr
              join tr.trainer trn
              join trn.user tru
              join tr.trainee tt
              join tt.user tu
              join tr.trainingType ttype
             where lower(tru.username) = lower(:u)
               and (:from is null or tr.trainingDate >= :from)
               and (:to   is null or tr.trainingDate <  :to)
               and (:type is null or ttype.name = :type)
               and (:tname is null or lower(concat(tu.firstName,' ',tu.lastName)) like :tname)
             order by tr.trainingDate desc
            """, Training.class);
        q.setParameter("u", trainerUsername);
        q.setParameter("from", from);
        q.setParameter("to", to);
        q.setParameter("type", trainingType);
        q.setParameter("tname", traineeNameLike);
        return q.getResultList();
    }
}

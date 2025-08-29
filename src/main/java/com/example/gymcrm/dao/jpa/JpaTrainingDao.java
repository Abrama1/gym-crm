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
    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(em.find(Training.class, id));
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Collection<Training> listForTrainee(String traineeUsername,
                                               LocalDateTime from, LocalDateTime to,
                                               String trainerNameLike, String trainingType) {
        StringBuilder jpql = new StringBuilder(
                "select tr from Training tr " +
                        " join tr.trainee tt join tt.user tu " +
                        " join tr.trainer trn join trn.user tru " +
                        " join tr.trainingType ttype " +
                        " where lower(tu.username) = lower(:u)"
        );
        if (from != null)        jpql.append(" and tr.trainingDate >= :from");
        if (to != null)          jpql.append(" and tr.trainingDate < :to");
        if (trainingType != null)jpql.append(" and ttype.name = :type");
        if (trainerNameLike != null)
            jpql.append(" and lower(concat(tru.firstName,' ',tru.lastName)) like :tname");
        jpql.append(" order by tr.trainingDate desc");

        var q = em.createQuery(jpql.toString(), Training.class);
        q.setParameter("u", traineeUsername);
        if (from != null)         q.setParameter("from", from);
        if (to != null)           q.setParameter("to", to);
        if (trainingType != null) q.setParameter("type", trainingType);
        if (trainerNameLike != null) q.setParameter("tname", trainerNameLike);
        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Training> listForTrainer(String trainerUsername,
                                               LocalDateTime from, LocalDateTime to,
                                               String traineeNameLike, String trainingType) {
        StringBuilder jpql = new StringBuilder(
                "select tr from Training tr " +
                        " join tr.trainer trn join trn.user tru " +
                        " join tr.trainee tt join tt.user tu " +
                        " join tr.trainingType ttype " +
                        " where lower(tru.username) = lower(:u)"
        );
        if (from != null)         jpql.append(" and tr.trainingDate >= :from");
        if (to != null)           jpql.append(" and tr.trainingDate < :to");
        if (trainingType != null) jpql.append(" and ttype.name = :type");
        if (traineeNameLike != null)
            jpql.append(" and lower(concat(tu.firstName,' ',tu.lastName)) like :tname");
        jpql.append(" order by tr.trainingDate desc");

        var q = em.createQuery(jpql.toString(), Training.class);
        q.setParameter("u", trainerUsername);
        if (from != null)          q.setParameter("from", from);
        if (to != null)            q.setParameter("to", to);
        if (trainingType != null)  q.setParameter("type", trainingType);
        if (traineeNameLike != null) q.setParameter("tname", traineeNameLike);
        return q.getResultList();
    }
}

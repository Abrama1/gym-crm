package com.example.gymcrm.app;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.*;
import com.example.gymcrm.facade.GymFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var facade = ctx.getBean(GymFacade.class);
            var typeDao = ctx.getBean(TrainingTypeDao.class);

            TrainingType cardio = new TrainingType(); cardio.setName("Cardio");
            typeDao.save(cardio);

            Trainee trainee = new Trainee();
            trainee.setAddress("Tbilisi");
            trainee.setDateOfBirth(LocalDate.of(2003, 5, 2));
            Trainee savedTrainee = facade.createTraineeProfile(trainee, "John", "Smith", true);

            Trainer trainer = new Trainer();
            trainer.setSpecialization("Strength");
            Trainer savedTrainer = facade.createTrainerProfile(trainer, "Jane", "Doe", true);

            Training tr = new Training();
            tr.setTraineeId(savedTrainee.getId());
            tr.setTrainerId(savedTrainer.getId());
            tr.setTrainingName("Morning Run");
            tr.setTrainingType("Cardio");
            tr.setTrainingDate(LocalDateTime.now());
            tr.setDurationMinutes(45);

            facade.createTraining(tr);
        }
    }
}

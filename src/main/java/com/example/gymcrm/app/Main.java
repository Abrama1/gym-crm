package com.example.gymcrm.app;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.facade.GymFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var facade = ctx.getBean(GymFacade.class);
            var typeDao = ctx.getBean(TrainingTypeDao.class);

            // Ensure at least one training type exists
            TrainingType cardio = typeDao.findByName("Cardio").orElseGet(() -> {
                TrainingType tt = new TrainingType();
                tt.setName("Cardio");
                return typeDao.save(tt);
            });

            // Create a Trainee (user will be generated inside service)
            Trainee trainee = new Trainee();
            trainee.setAddress("Tbilisi");
            trainee.setDateOfBirth(LocalDate.of(2003, 5, 2));
            Trainee savedTrainee = facade.createTraineeProfile(trainee, "John", "Smith", true);

            // Create a Trainer (user will be generated inside service)
            Trainer trainer = new Trainer();
            trainer.setSpecialization("Strength");
            Trainer savedTrainer = facade.createTrainerProfile(trainer, "Jane", "Doe", true);

            // Create a Training referencing the saved entities and existing type
            Training training = new Training();
            training.setTrainee(savedTrainee);
            training.setTrainer(savedTrainer);
            training.setTrainingType(cardio);
            training.setTrainingName("Morning Run");
            training.setTrainingDate(LocalDateTime.now());
            training.setDurationMinutes(45);

            facade.createTraining(training);
        }
    }
}

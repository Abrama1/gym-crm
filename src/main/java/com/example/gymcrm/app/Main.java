package com.example.gymcrm.app;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.Credentials;
import com.example.gymcrm.dto.TrainingCriteria;
import com.example.gymcrm.entity.*;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            var traineeService = ctx.getBean(TraineeService.class);
            var trainerService = ctx.getBean(TrainerService.class);
            var trainingService = ctx.getBean(TrainingService.class);
            var typeDao = ctx.getBean(TrainingTypeDao.class);

            // Seed types in a straightforward way
            TrainingType cardio;
            Optional<TrainingType> found = typeDao.findByName("Cardio");
            if (found.isPresent()) {
                cardio = found.get();
            } else {
                cardio = new TrainingType();
                cardio.setName("Cardio");
                cardio = typeDao.save(cardio);
            }

            // Create profiles
            Trainee trainee = new Trainee();
            trainee.setAddress("Tbilisi");
            trainee.setDateOfBirth(LocalDate.of(2003, 5, 2));
            Trainee savedTrainee = traineeService.create(trainee, "John", "Smith", true);

            Trainer trainer = new Trainer();
            trainer.setSpecialization("Strength");
            Trainer savedTrainer = trainerService.create(trainer, "Jane", "Doe", true);

            // Credentials (don’t print passwords)
            Credentials traineeCreds = new Credentials(
                    savedTrainee.getUser().getUsername(),
                    savedTrainee.getUser().getPassword()
            );
            Credentials trainerCreds = new Credentials(
                    savedTrainer.getUser().getUsername(),
                    savedTrainer.getUser().getPassword()
            );

            // Assign trainer to trainee
            traineeService.setTrainers(traineeCreds, traineeCreds.getUsername(),
                    List.of(trainerCreds.getUsername()));

            // Create a training
            Training tr = new Training();
            tr.setTrainee(savedTrainee);
            tr.setTrainer(savedTrainer);
            tr.setTrainingType(cardio);
            tr.setTrainingName("Morning Run");
            tr.setTrainingDate(LocalDateTime.now().minusDays(1));
            tr.setDurationMinutes(40);
            trainingService.create(tr);

            // Query with criteria (trainee view)
            TrainingCriteria crit = new TrainingCriteria(
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now().plusDays(1),
                    "Cardio",
                    "%jane doe%"
            );
            var traineeTrainings =
                    trainingService.listForTrainee(traineeCreds, traineeCreds.getUsername(), crit);
            System.out.println("Trainee trainings found: " + traineeTrainings.size());

            System.out.println("Demo flow completed OK.");
        } finally {
            ctx.close(); // close ONLY after all DAO calls are done
        }
    }
}

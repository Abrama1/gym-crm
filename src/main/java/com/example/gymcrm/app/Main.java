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

public class Main {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var traineeService = ctx.getBean(TraineeService.class);
            var trainerService = ctx.getBean(TrainerService.class);
            var trainingService = ctx.getBean(TrainingService.class);
            var typeDao = ctx.getBean(TrainingTypeDao.class);

            // Ensure training types exist
            TrainingType cardio = typeDao.findByName("Cardio").orElseGet(() -> {
                TrainingType tt = new TrainingType(); tt.setName("Cardio"); return typeDao.save(tt);
            });
            typeDao.findByName("Strength").orElseGet(() -> {
                TrainingType tt = new TrainingType(); tt.setName("Strength"); return typeDao.save(tt);
            });

            // Create a trainee & trainer
            Trainee trainee = new Trainee();
            trainee.setAddress("Tbilisi");
            trainee.setDateOfBirth(LocalDate.of(2003, 5, 2));
            Trainee savedTrainee = traineeService.create(trainee, "John", "Smith", true);

            Trainer trainer = new Trainer();
            trainer.setSpecialization("Strength");
            Trainer savedTrainer = trainerService.create(trainer, "Jane", "Doe", true);

            // Prepare credentials from created users
            String traineeUsername = savedTrainee.getUser().getUsername();
            String traineePassword = savedTrainee.getUser().getPassword();
            Credentials traineeCreds = new Credentials(traineeUsername, traineePassword);

            String trainerUsername = savedTrainer.getUser().getUsername();
            String trainerPassword = savedTrainer.getUser().getPassword();
            Credentials trainerCreds = new Credentials(trainerUsername, trainerPassword);

            // Assign trainer to trainee
            traineeService.setTrainers(traineeCreds, traineeUsername, List.of(trainerUsername));

            // Create a training for that pair
            Training tr = new Training();
            tr.setTrainee(savedTrainee);
            tr.setTrainer(savedTrainer);
            tr.setTrainingType(cardio);
            tr.setTrainingName("Morning Run");
            tr.setTrainingDate(LocalDateTime.now().minusDays(1));
            tr.setDurationMinutes(40);
            trainingService.create(tr);

            // Query trainings for trainee with criteria
            TrainingCriteria crit = new TrainingCriteria(
                    LocalDateTime.now().minusDays(7),   // from
                    LocalDateTime.now().plusDays(1),    // to
                    "Cardio",                           // trainingType
                    "%jane doe%"                        // other party name like
            );
            var traineeTrainings = trainingService.listForTrainee(traineeCreds, traineeUsername, crit);
            System.out.println("Trainee view - trainings found: " + traineeTrainings.size());

            // List trainers not assigned to this trainee
            var notAssigned = trainerService.listNotAssignedToTrainee(trainerCreds, traineeUsername);
            System.out.println("Trainers NOT assigned to " + traineeUsername + ": " + notAssigned.size());

            // Change password (trainee), then deactivate and reactivate
            traineeService.changePassword(traineeCreds, traineePassword);
            traineeService.deactivate(traineeCreds);
            traineeService.activate(traineeCreds);

            System.out.println("Demo flow completed OK.");
        }
    }
}

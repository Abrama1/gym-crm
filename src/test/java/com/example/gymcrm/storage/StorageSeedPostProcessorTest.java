package com.example.gymcrm.storage;

import com.example.gymcrm.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StorageSeedPostProcessorTest {

    private StorageSeedPostProcessor pp;
    private Environment env;
    private ResourceLoader loader;

    @BeforeEach
    void setup() {
        pp = new StorageSeedPostProcessor();
        env = Mockito.mock(Environment.class);
        loader = Mockito.mock(ResourceLoader.class);
        pp.setEnvironment(env);
        pp.setResourceLoader(loader);
    }

    @Test
    void seeds_all_maps_from_csv() {
        // ----- CSVs -----
        String usersCsv =
                "firstName,lastName,username,password,active\n" +
                        "John,Smith,John.Smith,Abc123XyZ9,true\n" +
                        "Jane,Doe,Jane.Doe,Qwerty1234,true\n";
        String traineesCsv =
                "dateOfBirth,address,userId\n" +
                        "2000-01-01,Tbilisi,1\n";
        String trainersCsv =
                "specialization,userId\n" +
                        "Strength,2\n";
        String typesCsv =
                "name\n" +
                        "Cardio\n" +
                        "Strength\n";
        String trainingsCsv =
                "traineeId,trainerId,trainingName,trainingType,trainingDate,durationMinutes\n" +
                        "1,1,Morning Run,Cardio,2025-08-17T08:00,45\n";

        // ----- env paths -----
        when(env.getProperty("seed.users")).thenReturn("users");
        when(env.getProperty("seed.trainees")).thenReturn("trainees");
        when(env.getProperty("seed.trainers")).thenReturn("trainers");
        when(env.getProperty("seed.trainingTypes")).thenReturn("types");
        when(env.getProperty("seed.trainings")).thenReturn("trainings");

        // ----- loader resources -----
        when(loader.getResource("users")).thenReturn(asRes(usersCsv));
        when(loader.getResource("trainees")).thenReturn(asRes(traineesCsv));
        when(loader.getResource("trainers")).thenReturn(asRes(trainersCsv));
        when(loader.getResource("types")).thenReturn(asRes(typesCsv));
        when(loader.getResource("trainings")).thenReturn(asRes(trainingsCsv));

        // ----- storages -----
        Map<Long, User> users = new ConcurrentHashMap<>();
        Map<Long, Trainee> trainees = new ConcurrentHashMap<>();
        Map<Long, Trainer> trainers = new ConcurrentHashMap<>();
        Map<String, TrainingType> types = new ConcurrentHashMap<>();
        Map<Long, Training> trainings = new ConcurrentHashMap<>();

        // act: simulate postProcessAfterInitialization calls by bean name
        pp.postProcessAfterInitialization(users, "userStorage");
        pp.postProcessAfterInitialization(trainees, "traineeStorage");
        pp.postProcessAfterInitialization(trainers, "trainerStorage");
        pp.postProcessAfterInitialization(types, "trainingTypeStorage");
        pp.postProcessAfterInitialization(trainings, "trainingStorage");

        // assert
        assertEquals(2, users.size());
        assertEquals(1, trainees.size());
        assertEquals(1, trainers.size());
        assertEquals(2, types.size());
        assertEquals(1, trainings.size());
    }

    private Resource asRes(String csv) {
        return new ByteArrayResource(csv.getBytes());
    }
}

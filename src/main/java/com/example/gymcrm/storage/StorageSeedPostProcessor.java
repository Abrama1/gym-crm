package com.example.gymcrm.storage;

import com.example.gymcrm.domain.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class StorageSeedPostProcessor implements BeanPostProcessor, EnvironmentAware, ResourceLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(StorageSeedPostProcessor.class);

    private Environment env;
    private ResourceLoader resourceLoader;

    @Override public void setEnvironment(Environment environment) { this.env = environment; }
    @Override public void setResourceLoader(ResourceLoader resourceLoader) { this.resourceLoader = resourceLoader; }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            switch (beanName) {
                case "userStorage" -> loadUsers((Map<Long, User>) bean, env.getProperty("seed.users"));
                case "traineeStorage" -> loadTrainees((Map<Long, Trainee>) bean, env.getProperty("seed.trainees"));
                case "trainerStorage" -> loadTrainers((Map<Long, Trainer>) bean, env.getProperty("seed.trainers"));
                case "trainingTypeStorage" -> loadTrainingTypes((Map<String, TrainingType>) bean, env.getProperty("seed.trainingTypes"));
                case "trainingStorage" -> loadTrainings((Map<Long, Training>) bean, env.getProperty("seed.trainings"));
            }
        } catch (Exception e) {
            log.error("Failed to seed storage for bean '{}': {}", beanName, e.getMessage(), e);
        }
        return bean;
    }

    /* ---------- CSV loaders (simple comma-separated, header on first line) ---------- */

    private void loadUsers(Map<Long, User> storage, String location) throws Exception {
        int n = 0; long id = 0;
        for (String[] r : readCsv(location)) {
            // header: firstName,lastName,username,password,active
            User u = new User();
            u.setId(++id);
            u.setFirstName(r[0]); u.setLastName(r[1]);
            u.setUsername(emptyToNull(r[2]));
            u.setPassword(emptyToNull(r[3])); // never log
            u.setActive(Boolean.parseBoolean(r[4]));
            storage.put(u.getId(), u); n++;
        }
        if (n>0) log.info("Seeded {} users", n);
    }

    private void loadTrainees(Map<Long, Trainee> storage, String location) throws Exception {
        int n = 0; long id = 0;
        for (String[] r : readCsv(location)) {
            // header: dateOfBirth(yyyy-MM-dd),address,userId
            Trainee t = new Trainee();
            t.setId(++id);
            t.setDateOfBirth(LocalDate.parse(r[0]));
            t.setAddress(r[1]);
            t.setUserId(Long.parseLong(r[2]));
            storage.put(t.getId(), t); n++;
        }
        if (n>0) log.info("Seeded {} trainees", n);
    }

    private void loadTrainers(Map<Long, Trainer> storage, String location) throws Exception {
        int n = 0; long id = 0;
        for (String[] r : readCsv(location)) {
            // header: specialization,userId
            Trainer t = new Trainer();
            t.setId(++id);
            t.setSpecialization(r[0]);
            t.setUserId(Long.parseLong(r[1]));
            storage.put(t.getId(), t); n++;
        }
        if (n>0) log.info("Seeded {} trainers", n);
    }

    private void loadTrainingTypes(Map<String, TrainingType> storage, String location) throws Exception {
        int n = 0;
        for (String[] r : readCsv(location)) {
            // header: name
            TrainingType tt = new TrainingType();
            tt.setName(r[0]);
            storage.put(tt.getName(), tt); n++;
        }
        if (n>0) log.info("Seeded {} training types", n);
    }

    private void loadTrainings(Map<Long, Training> storage, String location) throws Exception {
        int n = 0; long id = 0;
        for (String[] r : readCsv(location)) {
            // header: traineeId,trainerId,trainingName,trainingType,trainingDate(yyyy-MM-dd'T'HH:mm),durationMinutes
            Training t = new Training();
            t.setId(++id);
            t.setTraineeId(Long.parseLong(r[0]));
            t.setTrainerId(Long.parseLong(r[1]));
            t.setTrainingName(r[2]);
            t.setTrainingType(r[3]);
            t.setTrainingDate(LocalDateTime.parse(r[4])); // ISO-8601
            t.setDurationMinutes(Integer.parseInt(r[5]));
            storage.put(t.getId(), t); n++;
        }
        if (n>0) log.info("Seeded {} trainings", n);
    }

    /* ---------- helpers ---------- */

    private Iterable<String[]> readCsv(String location) throws Exception {
        if (location == null || location.isBlank()) return java.util.List.of();
        Resource res = resourceLoader.getResource(location);
        if (!res.exists()) { log.warn("Seed file not found: {}", location); return java.util.List.of(); }
        try (var in = res.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            var lines = reader.lines().toList();
            if (lines.isEmpty()) return java.util.List.of();
            var list = new java.util.ArrayList<String[]>();
            for (int i=1; i<lines.size(); i++) { // skip header
                String line = lines.get(i).trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] cols = line.split(",", -1); // keep empties
                for (int c=0;c<cols.length;c++) cols[c] = cols[c].trim();
                list.add(cols);
            }
            return list;
        }
    }

    private String emptyToNull(String s){ return (s==null || s.isBlank()) ? null : s; }
}

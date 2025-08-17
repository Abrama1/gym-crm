package com.example.gymcrm.config;

import com.example.gymcrm.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@PropertySource("classpath:application.properties")
public class StorageConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "userStorage")
    public Map<Long, User> userStorage() { return new ConcurrentHashMap<>(); }

    @Bean(name = "traineeStorage")
    public Map<Long, Trainee> traineeStorage() { return new ConcurrentHashMap<>(); }

    @Bean(name = "trainerStorage")
    public Map<Long, Trainer> trainerStorage() { return new ConcurrentHashMap<>(); }

    @Bean(name = "trainingStorage")
    public Map<Long, Training> trainingStorage() { return new ConcurrentHashMap<>(); }

    @Bean(name = "trainingTypeStorage")
    public Map<String, TrainingType> trainingTypeStorage() { return new ConcurrentHashMap<>(); }
}

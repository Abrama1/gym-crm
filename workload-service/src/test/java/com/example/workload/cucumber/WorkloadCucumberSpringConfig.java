package com.example.workload.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WorkloadCucumberSpringConfig {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.14");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
}

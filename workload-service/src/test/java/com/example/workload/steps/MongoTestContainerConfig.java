package com.example.workload.steps;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoTestContainerConfig {

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    static {
        MONGO.start();
    }

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }
}

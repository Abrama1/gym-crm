package com.example.it;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public final class ItEnvironment {

    private static final MongoDBContainer mongo =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    // ActiveMQ classic (5.x). Provides tcp://host:61616
    private static final GenericContainer<?> activemq =
            new GenericContainer<>(DockerImageName.parse("rmohr/activemq:5.16.3"))
                    .withExposedPorts(61616, 8161);

    private static ConfigurableApplicationContext gymCrmCtx;
    private static ConfigurableApplicationContext workloadCtx;

    private static int gymPort;
    private static int workloadPort;

    private static boolean started = false;

    private ItEnvironment() {}

    public static synchronized void ensureStarted() {
        if (started) return;

        mongo.start();
        activemq.start();

        String mongoUri = mongo.getReplicaSetUrl();
        String brokerUrl = "tcp://" + activemq.getHost() + ":" + activemq.getMappedPort(61616);

        gymPort = SocketUtils.findAvailableTcpPort();
        workloadPort = SocketUtils.findAvailableTcpPort();

        gymCrmCtx = new SpringApplicationBuilder(com.example.gymcrm.GymCrmApplication.class)
                .profiles("it")
                .properties(commonProps(brokerUrl))
                .properties(Map.of(
                        "server.port", String.valueOf(gymPort),

                        // Use H2 for gym_crm integration test DB
                        "spring.datasource.url", "jdbc:h2:mem:gym;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                        "spring.datasource.username", "sa",
                        "spring.datasource.password", "",
                        "spring.jpa.hibernate.ddl-auto", "create-drop",

                        // If Eureka exists, disable it for tests
                        "eureka.client.enabled", "false",
                        "spring.cloud.discovery.enabled", "false",

                        // Make JWT not block tests
                        "security.jwt.secret", "it-secret",
                        "security.jwt.issuer", "gym-crm-it"
                ))
                .run();

        workloadCtx = new SpringApplicationBuilder(com.example.workload.WorkloadServiceApplication.class)
                .profiles("it")
                .properties(commonProps(brokerUrl))
                .properties(Map.of(
                        "server.port", String.valueOf(workloadPort),

                        // workload-service uses Mongo now
                        "spring.data.mongodb.uri", mongoUri,

                        // disable discovery for tests
                        "eureka.client.enabled", "false",
                        "spring.cloud.discovery.enabled", "false"
                ))
                .run();

        started = true;
    }

    private static Map<String, Object> commonProps(String brokerUrl) {
        return Map.of(
                // ActiveMQ connectivity
                "spring.activemq.broker-url", brokerUrl,
                "spring.activemq.user", "admin",
                "spring.activemq.password", "admin",

                // Queue name used by publisher/listener
                "workload.queue.events", "workload.events"
        );
    }

    public static String gymBaseUrl() {
        ensureStarted();
        return "http://localhost:" + gymPort;
    }

    public static String workloadBaseUrl() {
        ensureStarted();
        return "http://localhost:" + workloadPort;
    }
}

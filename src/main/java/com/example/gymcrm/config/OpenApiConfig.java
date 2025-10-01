package com.example.gymcrm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gymCrmOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Gym CRM API")
                        .description("REST API for trainees, trainers and trainings")
                        .version("v1")
                        .contact(new Contact().name("Gym CRM")));
    }
}

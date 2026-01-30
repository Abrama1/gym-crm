package com.example.it;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@ActiveProfiles("it")
public class CucumberSpringConfig {

}

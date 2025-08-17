package com.example.gymcrm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.example.gymcrm")
@Import(StorageConfig.class)
public class AppConfig { }
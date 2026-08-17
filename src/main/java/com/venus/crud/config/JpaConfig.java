package com.venus.crud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.venus.crud.repository.jpa")
public class JpaConfig {
}

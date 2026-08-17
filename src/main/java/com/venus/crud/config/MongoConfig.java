package com.venus.crud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.venus.crud.repository.mongo")
public class MongoConfig {
}

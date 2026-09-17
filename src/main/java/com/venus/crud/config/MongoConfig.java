package com.venus.crud.config;

import com.venus.crud.document.converter.AnalysisStatusReadConverter;
import com.venus.crud.document.converter.AnalysisStatusWriteConverter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.venus.crud.repository.mongo")
@EnableMongoAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new AnalysisStatusReadConverter(),
                new AnalysisStatusWriteConverter()));
    }

    @Bean
    public DateTimeProvider offsetDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}

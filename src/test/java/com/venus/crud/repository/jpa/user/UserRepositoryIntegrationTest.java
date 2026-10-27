package com.venus.crud.repository.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.venus.crud.exception.InvalidStateTransitionDetector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUrlParam("currentSchema", "venus")
            .withUrlParam("stringtype", "unspecified")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/00_schema.sql"),
                    "/docker-entrypoint-initdb.d/00_schema.sql");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void procedureMakesUserInactive() {
        Long userId = insertUser("fica-inativo", "active");

        userRepository.setActive(userId, false);

        assertThat(readStatus(userId)).isEqualTo("inactive");
    }

    @Test
    void procedureMakesUserActive() {
        Long userId = insertUser("fica-ativo", "inactive");

        userRepository.setActive(userId, true);

        assertThat(readStatus(userId)).isEqualTo("active");
    }

    @Test
    void procedureRefusesBlockedUser() {
        Long userId = insertUser("bloqueado", "blocked");

        assertThatThrownBy(() -> userRepository.setActive(userId, true))
                .satisfies(error -> assertThat(InvalidStateTransitionDetector.matches(error)).isTrue());
    }

    private Long insertUser(String firebaseUid, String status) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.users (firebase_uid, name, status) "
                + "VALUES (?, 'Usuario Teste', ?) RETURNING user_id", Long.class, firebaseUid, status);
    }

    private String readStatus(Long userId) {
        return jdbcTemplate.queryForObject("SELECT status FROM venus.users WHERE user_id = ?", String.class, userId);
    }
}

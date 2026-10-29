package com.venus.crud.repository.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.exception.InvalidStateTransitionDetector;
import java.math.BigDecimal;
import java.util.Map;
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
                    "/docker-entrypoint-initdb.d/00_schema.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/01_users_email_password_hash.sql"),
                    "/docker-entrypoint-initdb.d/01_users_email_password_hash.sql");

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

    @Test
    void saveStoresLocation() {
        User user = new User();
        user.setFirebaseUid("grava-local");
        user.setName("Usuario Teste");
        user.setStatus(UserStatus.ACTIVE);
        user.setLatitude(new BigDecimal("-23.55"));
        user.setLongitude(new BigDecimal("-46.63"));
        user.setCity("Sao Paulo");
        user.setState("Sao Paulo");

        userRepository.saveAndFlush(user);

        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT latitude, longitude, city, state FROM venus.users WHERE firebase_uid = 'grava-local'");
        assertThat(row.get("latitude")).isEqualTo(new BigDecimal("-23.55"));
        assertThat(row.get("longitude")).isEqualTo(new BigDecimal("-46.63"));
        assertThat(row.get("city")).isEqualTo("Sao Paulo");
        assertThat(row.get("state")).isEqualTo("Sao Paulo");
    }

    @Test
    void findReadsLocation() {
        Long userId = insertUser("le-local", "active");
        jdbcTemplate.update("UPDATE venus.users SET latitude = -22.90, longitude = -43.17, "
                + "city = 'Rio de Janeiro', state = 'Rio de Janeiro' WHERE user_id = ?", userId);

        User user = userRepository.findById(userId).orElseThrow();

        assertThat(user.getLatitude()).isEqualByComparingTo("-22.90");
        assertThat(user.getLongitude()).isEqualByComparingTo("-43.17");
        assertThat(user.getCity()).isEqualTo("Rio de Janeiro");
        assertThat(user.getState()).isEqualTo("Rio de Janeiro");
    }

    private Long insertUser(String firebaseUid, String status) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.users (firebase_uid, name, status) "
                + "VALUES (?, 'Usuario Teste', ?) RETURNING user_id", Long.class, firebaseUid, status);
    }

    private String readStatus(Long userId) {
        return jdbcTemplate.queryForObject("SELECT status FROM venus.users WHERE user_id = ?", String.class, userId);
    }
}

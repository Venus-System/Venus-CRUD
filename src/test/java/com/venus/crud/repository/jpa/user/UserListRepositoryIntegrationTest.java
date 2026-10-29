package com.venus.crud.repository.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.enums.ListCoverKey;
import com.venus.crud.entity.enums.ListType;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class UserListRepositoryIntegrationTest {

    private static final String COVER_URL = "https://res.cloudinary.com/venus/image/upload/v1/user-lists/capa-atual.jpg";
    private static final String OLD_COVER_URL = "https://res.cloudinary.com/venus/image/upload/v1/user-lists/capa-antiga.jpg";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUrlParam("currentSchema", "venus")
            .withUrlParam("stringtype", "unspecified")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/00_schema.sql"),
                    "/docker-entrypoint-initdb.d/00_schema.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/05_cloudinary_images.sql"),
                    "/docker-entrypoint-initdb.d/05_cloudinary_images.sql");

    @Autowired
    private UserListRepository userListRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void coverUrlComesFromTheLiveCover() {
        Long userListId = insertUserList(insertUser("dona-da-capa"), "Com capa");
        insertCover(userListId, "user-lists/capa-antiga", OLD_COVER_URL, "deleted");
        insertCover(userListId, "user-lists/capa-atual", COVER_URL, "active");

        assertThat(userListRepository.findById(userListId).orElseThrow().getCoverUrl()).isEqualTo(COVER_URL);
    }

    @Test
    void coverUrlIsNullWhenTheOnlyCoverWasDeleted() {
        Long userListId = insertUserList(insertUser("capa-apagada"), "Sem capa viva");
        insertCover(userListId, "user-lists/apagada", OLD_COVER_URL, "deleted");

        assertThat(userListRepository.findById(userListId).orElseThrow().getCoverUrl()).isNull();
    }

    @Test
    void coverUrlIsNullWhenTheListHasNoCover() {
        Long userListId = insertUserList(insertUser("sem-capa"), "Nunca teve capa");

        assertThat(userListRepository.findById(userListId).orElseThrow().getCoverUrl()).isNull();
    }

    @Test
    void descriptionAndCoverKeyAreReadFromTheDatabase() {
        Long userId = insertUser("capa-padrao");
        Long userListId = jdbcTemplate.queryForObject("INSERT INTO venus.user_lists (fk_user_id, name, description, cover_key) "
                + "VALUES (?, 'Skincare', 'Rotina da manha', 'skincare') RETURNING user_list_id", Long.class, userId);

        UserList userList = userListRepository.findById(userListId).orElseThrow();

        assertThat(userList.getDescription()).isEqualTo("Rotina da manha");
        assertThat(userList.getCoverKey()).isEqualTo(ListCoverKey.SKINCARE);
    }

    @Test
    void coverKeyIsWrittenInLowercase() {
        UserList userList = new UserList();
        userList.setUser(entityManager.getEntityManager().getReference(User.class, insertUser("grava-capa")));
        userList.setName("Favoritos");
        userList.setListType(ListType.FAVORITES);
        userList.setCoverKey(ListCoverKey.FAVORITOS);
        userListRepository.saveAndFlush(userList);

        String stored = jdbcTemplate.queryForObject("SELECT cover_key FROM venus.user_lists WHERE user_list_id = ?",
                String.class, userList.getId());
        assertThat(stored).isEqualTo("favoritos");
    }

    private Long insertUser(String firebaseUid) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.users (firebase_uid, name) VALUES (?, 'Usuario Teste') "
                + "RETURNING user_id", Long.class, firebaseUid);
    }

    private Long insertUserList(Long userId, String name) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.user_lists (fk_user_id, name) VALUES (?, ?) "
                + "RETURNING user_list_id", Long.class, userId, name);
    }

    private void insertCover(Long userListId, String publicId, String secureUrl, String status) {
        jdbcTemplate.update("INSERT INTO venus.media_assets (fk_user_list_id, purpose, public_id, secure_url, status) "
                + "VALUES (?, 'list_cover', ?, ?, ?)", userListId, publicId, secureUrl, status);
    }
}

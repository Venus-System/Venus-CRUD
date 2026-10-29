package com.venus.crud.repository.jpa.review;

import static org.assertj.core.api.Assertions.assertThat;

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
class ReviewRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUrlParam("currentSchema", "venus")
            .withUrlParam("stringtype", "unspecified")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/00_schema.sql"),
                    "/docker-entrypoint-initdb.d/00_schema.sql");

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void usefulVotesCountsOnlyTheUsefulVotesOfTheReview() {
        Long productVersionId = insertProductVersion();
        Long authorId = insertUser("autor");
        Long otherAuthorId = insertUser("outro-autor");
        Long firstVoterId = insertUser("votante-1");
        Long secondVoterId = insertUser("votante-2");
        Long reviewId = insertReview(authorId, productVersionId);
        Long otherReviewId = insertReview(otherAuthorId, productVersionId);
        insertVote(reviewId, firstVoterId, "useful");
        insertVote(reviewId, secondVoterId, "useful");
        insertVote(reviewId, otherAuthorId, "not_useful");
        insertVote(otherReviewId, firstVoterId, "useful");

        assertThat(reviewRepository.findById(reviewId).orElseThrow().getUsefulVotes()).isEqualTo(2);
    }

    private Long insertProductVersion() {
        Long brandId = jdbcTemplate.queryForObject(
                "INSERT INTO venus.brands (name) VALUES ('Marca Avaliacao') RETURNING brand_id", Long.class);
        Long categoryId = jdbcTemplate.queryForObject(
                "INSERT INTO venus.product_categories (name) VALUES ('Categoria Avaliacao') RETURNING product_category_id",
                Long.class);
        Long productId = jdbcTemplate.queryForObject("INSERT INTO venus.products (fk_brand_id, fk_product_category_id, name, slug) "
                + "VALUES (?, ?, 'Hidratante Avaliacao', 'marca-avaliacao-hidratante') RETURNING product_id",
                Long.class, brandId, categoryId);
        return jdbcTemplate.queryForObject("INSERT INTO venus.product_versions "
                + "(fk_product_id, version_name, display_name, formula_signature) "
                + "VALUES (?, 'Original', 'Hidratante Avaliacao', 'assinatura-avaliacao') RETURNING product_version_id",
                Long.class, productId);
    }

    private Long insertUser(String firebaseUid) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.users (firebase_uid, name) VALUES (?, 'Usuario Teste') "
                + "RETURNING user_id", Long.class, firebaseUid);
    }

    private Long insertReview(Long userId, Long productVersionId) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.reviews (fk_user_id, fk_product_version_id) "
                + "VALUES (?, ?) RETURNING review_id", Long.class, userId, productVersionId);
    }

    private void insertVote(Long reviewId, Long userId, String voteType) {
        jdbcTemplate.update("INSERT INTO venus.review_votes (fk_review_id, fk_user_id, vote_type) VALUES (?, ?, ?)",
                reviewId, userId, voteType);
    }
}

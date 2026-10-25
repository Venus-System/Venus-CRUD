package com.venus.crud.repository.jpa.product;

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
class ProductRepositoryIntegrationTest {

    private static final String FRONT_URL = "https://res.cloudinary.com/venus/image/upload/v1/products/frente.jpg";
    private static final String BACK_URL = "https://res.cloudinary.com/venus/image/upload/v1/products/verso.jpg";
    private static final String OLD_URL = "https://res.cloudinary.com/venus/image/upload/v1/products/antiga.jpg";

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
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void imageUrlIsFirstPhoto() {
        Long productId = insertProduct("Shampoo Com Fotos");
        Long versionId = insertVersion(productId, "v1", true);
        insertPhoto(versionId, "products/apagada", OLD_URL, 0, "deleted");
        insertPhoto(versionId, "products/verso", BACK_URL, 2, "active");
        insertPhoto(versionId, "products/frente", FRONT_URL, 1, "active");

        assertThat(productRepository.findById(productId).orElseThrow().getImageUrl()).isEqualTo(FRONT_URL);
    }

    @Test
    void imageUrlSkipsOldVersion() {
        Long productId = insertProduct("Creme Reformulado");
        Long oldVersionId = insertVersion(productId, "v1", false);
        insertPhoto(oldVersionId, "products/antiga", OLD_URL, 0, "active");
        Long currentVersionId = insertVersion(productId, "v2", true);
        insertPhoto(currentVersionId, "products/frente", FRONT_URL, 0, "active");

        assertThat(productRepository.findById(productId).orElseThrow().getImageUrl()).isEqualTo(FRONT_URL);
    }

    @Test
    void imageUrlIsNullWithoutPhoto() {
        Long productId = insertProduct("Sabonete Sem Foto");
        insertVersion(productId, "v1", true);

        assertThat(productRepository.findById(productId).orElseThrow().getImageUrl()).isNull();
    }

    private Long insertProduct(String name) {
        Long brandId = jdbcTemplate.queryForObject("INSERT INTO venus.brands (name) VALUES (?) RETURNING brand_id",
                Long.class, name);
        Long productCategoryId = jdbcTemplate.queryForObject("INSERT INTO venus.product_categories (name) VALUES (?) "
                + "RETURNING product_category_id", Long.class, name);
        return jdbcTemplate.queryForObject("INSERT INTO venus.products (fk_brand_id, fk_product_category_id, name, slug) "
                + "VALUES (?, ?, ?, ?) RETURNING product_id", Long.class, brandId, productCategoryId, name, name);
    }

    private Long insertVersion(Long productId, String versionName, boolean isCurrent) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.product_versions (fk_product_id, version_name, display_name, "
                + "is_current, formula_signature) VALUES (?, ?, ?, ?, ?) RETURNING product_version_id", Long.class,
                productId, versionName, versionName, isCurrent, versionName);
    }

    private void insertPhoto(Long productVersionId, String publicId, String secureUrl, int sortOrder, String status) {
        jdbcTemplate.update("INSERT INTO venus.media_assets (fk_product_version_id, purpose, public_id, secure_url, "
                + "sort_order, status) VALUES (?, 'product_photo', ?, ?, ?, ?)",
                productVersionId, publicId, secureUrl, sortOrder, status);
    }
}

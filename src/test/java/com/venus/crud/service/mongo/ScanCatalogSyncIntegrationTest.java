package com.venus.crud.service.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.config.MediaProperties;
import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanBackExtracted;
import com.venus.crud.document.ScanFrontExtracted;
import com.venus.crud.document.ScanImage;
import com.venus.crud.document.ScanImages;
import com.venus.crud.document.ScanIngredientDecision;
import com.venus.crud.document.ScanOcr;
import com.venus.crud.document.ScanOcrBack;
import com.venus.crud.document.ScanOcrFront;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSnapshotProduct;
import com.venus.crud.entity.enums.IngredientDecisionAction;
import com.venus.crud.exception.DataConstraintException;
import com.venus.crud.mapper.jpa.media.MediaAssetMapperImpl;
import com.venus.crud.service.jpa.media.MediaAssetWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "venus.media.allowed-image-types=image/jpeg",
        "venus.media.default-delivery-type=upload",
        "venus.media.default-resource-type=image",
        "venus.media.avatar.max-bytes=5242880",
        "venus.media.avatar.max-width=2048",
        "venus.media.avatar.max-height=2048",
        "venus.media.product.max-bytes=10485760",
        "venus.media.product.max-width=4096",
        "venus.media.product.max-height=4096"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@EnableConfigurationProperties(MediaProperties.class)
@Import({ScanCatalogSync.class, MediaAssetWriter.class, MediaAssetMapperImpl.class})
class ScanCatalogSyncIntegrationTest {

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
    private ScanCatalogSync scanCatalogSync;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void approvedScanOfANewProductCreatesProductVersionIngredientsLabelAndPhoto() {
        Long brandId = insertBrand("Natura Teste");
        Long categoryId = insertProductCategory("Serum Teste");
        Long unclassifiedId = insertIngredientCategory("Não classificado");
        Long aquaId = insertIngredient(unclassifiedId, "AQUA TESTE");
        ScanSession scan = scan("scan-integracao-1", newProduct("Sérum Vitamina C", brandId, categoryId),
                link(1, aquaId), create(2, "COUMARIN TESTE"));
        scan.setImages(frontImage("scans/scan-integracao-1/front"));
        scan.setOcr(ocr("30 ml", "Aqua, Coumarin"));

        ScanCatalogSync.Result result = scanCatalogSync.synchronize(scan);
        entityManager.flush();

        Long coumarinId = jdbcTemplate.queryForObject(
                "SELECT ingredient_id FROM venus.ingredients WHERE inci_name = 'COUMARIN TESTE'", Long.class);
        assertThat(jdbcTemplate.queryForObject("SELECT slug FROM venus.products WHERE product_id = ?", String.class,
                result.productId())).isEqualTo("natura-teste-serum-vitamina-c");
        Map<String, Object> version = jdbcTemplate.queryForMap(
                "SELECT status::text AS status, is_current, formula_signature, detected_by::text AS detected_by, display_name "
                        + "FROM venus.product_versions WHERE product_version_id = ?", result.productVersionId());
        assertThat(version)
                .containsEntry("status", "verified")
                .containsEntry("is_current", true)
                .containsEntry("detected_by", "ocr")
                .containsEntry("display_name", "Sérum Vitamina C 30 ml")
                .containsEntry("formula_signature", ScanSyncKeys.formulaSignature(List.of(aquaId, coumarinId)));
        assertThat(jdbcTemplate.queryForList("SELECT fk_ingredient_id FROM venus.product_ingredients "
                + "WHERE fk_product_version_id = ? ORDER BY position", Long.class, result.productVersionId()))
                .containsExactly(aquaId, coumarinId);
        Map<String, Object> created = jdbcTemplate.queryForMap(
                "SELECT fk_ingredient_category_id, scientific_confidence, source_type::text AS source_type, source_reference "
                        + "FROM venus.ingredients WHERE ingredient_id = ?", coumarinId);
        assertThat(created)
                .containsEntry("fk_ingredient_category_id", unclassifiedId)
                .containsEntry("source_type", "admin")
                .containsEntry("source_reference", "scan:scan-integracao-1");
        assertThat(((Number) created.get("scientific_confidence")).intValue()).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT normalized_text FROM venus.product_labels "
                + "WHERE fk_product_version_id = ?", String.class, result.productVersionId())).isEqualTo("Aqua, Coumarin");
        assertThat(jdbcTemplate.queryForObject("SELECT public_id FROM venus.media_assets "
                + "WHERE fk_product_version_id = ? AND purpose = 'product_photo'", String.class, result.productVersionId()))
                .isEqualTo("scans/scan-integracao-1/front");
    }

    @Test
    void newVersionOfAnExistingProductBecomesTheOnlyCurrentOne() {
        Long brandId = insertBrand("Marca Versao");
        Long categoryId = insertProductCategory("Categoria Versao");
        Long ingredientCategoryId = insertIngredientCategory("Categoria Ingrediente Versao");
        Long glycerinId = insertIngredient(ingredientCategoryId, "GLYCERIN TESTE");
        Long niacinamideId = insertIngredient(ingredientCategoryId, "NIACINAMIDE TESTE");
        Long productId = jdbcTemplate.queryForObject("INSERT INTO venus.products (fk_brand_id, fk_product_category_id, name, slug) "
                + "VALUES (?, ?, 'Hidratante Versao', 'marca-versao-hidratante-versao') RETURNING product_id",
                Long.class, brandId, categoryId);
        Long oldVersionId = jdbcTemplate.queryForObject("INSERT INTO venus.product_versions "
                + "(fk_product_id, version_name, display_name, is_current, formula_signature) "
                + "VALUES (?, 'Original', 'Hidratante Versao', TRUE, 'assinatura-da-versao-antiga') RETURNING product_version_id",
                Long.class, productId);
        ScanSession scan = scan("scan-integracao-2", existingProduct(productId), link(1, glycerinId), link(2, niacinamideId));

        ScanCatalogSync.Result result = scanCatalogSync.synchronize(scan);
        entityManager.flush();

        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.productVersionId()).isNotEqualTo(oldVersionId);
        assertThat(jdbcTemplate.queryForList("SELECT product_version_id FROM venus.product_versions "
                + "WHERE fk_product_id = ? AND is_current", Long.class, productId))
                .containsExactly(result.productVersionId());
    }

    @Test
    void syncingTheSameScanTwiceReusesProductVersionAndCreatedIngredient() {
        Long brandId = insertBrand("Marca Repetida");
        Long categoryId = insertProductCategory("Categoria Repetida");
        Long unclassifiedId = insertIngredientCategory("Não classificado");
        Long waterId = insertIngredient(unclassifiedId, "WATER TESTE");
        ScanSession scan = scan("scan-integracao-3", newProduct("Tonico Repetido", brandId, categoryId),
                link(1, waterId), create(2, "INCI NOVO TESTE"));

        ScanCatalogSync.Result first = scanCatalogSync.synchronize(scan);
        ScanCatalogSync.Result second = scanCatalogSync.synchronize(scan);
        entityManager.flush();

        assertThat(second).isEqualTo(first);
        assertThat(count("SELECT count(*) FROM venus.products WHERE slug = 'marca-repetida-tonico-repetido'")).isEqualTo(1);
        assertThat(count("SELECT count(*) FROM venus.product_versions WHERE fk_product_id = ?", first.productId()))
                .isEqualTo(1);
        assertThat(count("SELECT count(*) FROM venus.ingredients WHERE inci_name = 'INCI NOVO TESTE'")).isEqualTo(1);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void twoConcurrentSyncsOfTheSameNewProductNeverDuplicateIt() throws Exception {
        Long brandId = insertBrand("Marca Corrida");
        Long categoryId = insertProductCategory("Categoria Corrida");
        Long ingredientCategoryId = insertIngredientCategory("Categoria Ingrediente Corrida");
        Long firstId = insertIngredient(ingredientCategoryId, "INCI CORRIDA A");
        Long secondId = insertIngredient(ingredientCategoryId, "INCI CORRIDA B");
        ScanSession firstScan = scan("scan-corrida-1", newProduct("Creme Corrida", brandId, categoryId),
                link(1, firstId), link(2, secondId));
        ScanSession secondScan = scan("scan-corrida-2", newProduct("Creme Corrida", brandId, categoryId),
                link(1, firstId), link(2, secondId));

        List<ScanCatalogSync.Result> results = synchronizeTogether(firstScan, secondScan);

        Long productId = results.get(0).productId();
        assertThat(results).extracting(ScanCatalogSync.Result::productVersionId)
                .containsOnly(results.get(0).productVersionId());
        assertThat(count("SELECT count(*) FROM venus.products WHERE slug = 'marca-corrida-creme-corrida'")).isEqualTo(1);
        assertThat(count("SELECT count(*) FROM venus.product_versions WHERE fk_product_id = ?", productId)).isEqualTo(1);
        assertThat(count("SELECT count(*) FROM venus.product_versions WHERE fk_product_id = ? AND is_current", productId))
                .isEqualTo(1);
    }

    private List<ScanCatalogSync.Result> synchronizeTogether(ScanSession... scans) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(scans.length);
        try {
            List<Future<ScanCatalogSync.Result>> futures = new ArrayList<>();
            for (ScanSession scan : scans) {
                futures.add(pool.submit(() -> {
                    start.await();
                    return scanCatalogSync.synchronize(scan);
                }));
            }
            start.countDown();
            List<ScanCatalogSync.Result> results = new ArrayList<>();
            for (int index = 0; index < scans.length; index++) {
                results.add(resultOrRetry(futures.get(index), scans[index]));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
    }

    private ScanCatalogSync.Result resultOrRetry(Future<ScanCatalogSync.Result> future, ScanSession scan) throws Exception {
        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException ex) {
            assertThat(ex.getCause()).isInstanceOf(DataConstraintException.class);
            assertThat(((DataConstraintException) ex.getCause()).getStatus()).isEqualTo(HttpStatus.CONFLICT);
            return scanCatalogSync.synchronize(scan);
        }
    }

    private Long insertBrand(String name) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.brands (name) VALUES (?) RETURNING brand_id", Long.class, name);
    }

    private Long insertProductCategory(String name) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO venus.product_categories (name) VALUES (?) RETURNING product_category_id", Long.class, name);
    }

    private Long insertIngredientCategory(String name) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO venus.ingredient_categories (name) VALUES (?) RETURNING ingredient_category_id", Long.class, name);
    }

    private Long insertIngredient(Long ingredientCategoryId, String inciName) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.ingredients (fk_ingredient_category_id, inci_name) "
                + "VALUES (?, ?) RETURNING ingredient_id", Long.class, ingredientCategoryId, inciName);
    }

    private long count(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, Long.class, args);
    }

    private ScanSession scan(String scanId, ScanSnapshotProduct product, ScanIngredientDecision... decisions) {
        ScanApprovedSnapshot snapshot = new ScanApprovedSnapshot();
        snapshot.setProduct(product);
        snapshot.setIngredients(List.of(decisions));
        ScanSession scanSession = new ScanSession();
        scanSession.setScanId(scanId);
        scanSession.setApprovedSnapshot(snapshot);
        return scanSession;
    }

    private ScanSnapshotProduct newProduct(String name, Long brandId, Long productCategoryId) {
        ScanSnapshotProduct product = new ScanSnapshotProduct();
        product.setName(name);
        product.setBrandId(brandId);
        product.setProductCategoryId(productCategoryId);
        return product;
    }

    private ScanSnapshotProduct existingProduct(Long productId) {
        ScanSnapshotProduct product = new ScanSnapshotProduct();
        product.setProductId(productId);
        return product;
    }

    private ScanIngredientDecision link(int position, Long ingredientId) {
        ScanIngredientDecision decision = new ScanIngredientDecision();
        decision.setPosition(position);
        decision.setAction(IngredientDecisionAction.LINK);
        decision.setIngredientId(ingredientId);
        return decision;
    }

    private ScanIngredientDecision create(int position, String inciName) {
        ScanIngredientDecision decision = new ScanIngredientDecision();
        decision.setPosition(position);
        decision.setAction(IngredientDecisionAction.CREATE);
        decision.setInciName(inciName);
        return decision;
    }

    private ScanImages frontImage(String publicId) {
        ScanImage front = new ScanImage();
        front.setPublicId(publicId);
        front.setSecureUrl("https://res.cloudinary.com/venus/image/upload/" + publicId + ".jpg");
        front.setFormat("jpg");
        front.setWidth(1200);
        front.setHeight(1600);
        front.setBytes(245000L);
        ScanImages images = new ScanImages();
        images.setFront(front);
        return images;
    }

    private ScanOcr ocr(String capacity, String ingredientsText) {
        ScanFrontExtracted frontExtracted = new ScanFrontExtracted();
        frontExtracted.setCapacity(capacity);
        ScanOcrFront front = new ScanOcrFront();
        front.setExtracted(frontExtracted);
        ScanBackExtracted backExtracted = new ScanBackExtracted();
        backExtracted.setIngredientsText(ingredientsText);
        ScanOcrBack back = new ScanOcrBack();
        back.setExtracted(backExtracted);
        ScanOcr ocr = new ScanOcr();
        ocr.setFront(front);
        ocr.setBack(back);
        return ocr;
    }
}

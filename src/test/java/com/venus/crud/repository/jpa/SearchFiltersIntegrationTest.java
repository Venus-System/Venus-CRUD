package com.venus.crud.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairPattern;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.product.Brand;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.review.Report;
import com.venus.crud.entity.user.User;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import com.venus.crud.repository.jpa.ingredient.AllergyIngredientRepository;
import com.venus.crud.repository.jpa.ingredient.CompatibilityRuleRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientAliasRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientEffectRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import com.venus.crud.repository.jpa.ingredient.RegulationRepository;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.PackagingRepository;
import com.venus.crud.repository.jpa.product.ProductClaimRepository;
import com.venus.crud.repository.jpa.product.ProductLabelRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.repository.jpa.review.ReportRepository;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
import com.venus.crud.repository.jpa.scoring.RecommendationRepository;
import com.venus.crud.repository.jpa.user.UserPreferenceRepository;
import com.venus.crud.repository.jpa.user.UserProfileRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class SearchFiltersIntegrationTest {

    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20);

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUrlParam("currentSchema", "venus")
            .withUrlParam("stringtype", "unspecified")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/00_schema.sql"),
                    "/docker-entrypoint-initdb.d/00_schema.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/01_users_email_password_hash.sql"),
                    "/docker-entrypoint-initdb.d/01_users_email_password_hash.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/02_unaccent.sql"),
                    "/docker-entrypoint-initdb.d/02_unaccent.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("venus-banco/05_cloudinary_images.sql"),
                    "/docker-entrypoint-initdb.d/05_cloudinary_images.sql");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AllergyIngredientRepository allergyIngredientRepository;

    @Autowired
    private CompatibilityRuleRepository compatibilityRuleRepository;

    @Autowired
    private IngredientAliasRepository ingredientAliasRepository;

    @Autowired
    private IngredientEffectRepository ingredientEffectRepository;

    @Autowired
    private RegulationRepository regulationRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PackagingRepository packagingRepository;

    @Autowired
    private ProductClaimRepository productClaimRepository;

    @Autowired
    private ProductLabelRepository productLabelRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVersionRepository productVersionRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private RuleEvaluationRepository ruleEvaluationRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void brandSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> brandRepository.search(null, null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void brandSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> brandRepository.search("natura", "Brasil", true, true, true, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void brandSearchCombinesNameAndIsBrazilian() {
        insertBrand("Natura Teste", "Brasil", true);
        insertBrand("Natura Teste Global", "Franca", false);
        insertBrand("Boticario Teste", "Brasil", true);

        Slice<Brand> brands = brandRepository.search("natura teste", null, null, null, true, FIRST_PAGE);

        assertThat(brands.getContent()).extracting(Brand::getName).containsExactly("Natura Teste");
    }

    @Test
    void brandSearchWithIsBrazilianFalseBringsOnlyBrandsFromOutside() {
        insertBrand("Marca Nacional Teste", "Brasil", true);
        insertBrand("Marca Importada Teste", "Franca", false);

        Slice<Brand> brands = brandRepository.search("teste", null, null, null, false, FIRST_PAGE);

        assertThat(brands.getContent()).extracting(Brand::getName).containsExactly("Marca Importada Teste");
    }

    @Test
    void brandSearchWithoutFiltersKeepsBrandWithoutCountry() {
        insertBrandWithoutCountry("Marca Sem Pais Teste");

        Slice<Brand> brands = brandRepository.search(null, null, null, null, null, FIRST_PAGE);

        assertThat(brands.getContent()).extracting(Brand::getName).contains("Marca Sem Pais Teste");
    }

    @Test
    void adminUserSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> adminUserRepository.search(null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void adminUserSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> adminUserRepository.search("ana", AdminRole.ADMIN, true, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void adminUserSearchCombinesNameRoleAndIsActive() {
        insertAdminUser("Ana Teste", "ana@teste.com", "admin", true);
        insertAdminUser("Bia Teste", "bia@teste.com", "admin", false);
        insertAdminUser("Caio Teste", "caio@teste.com", "analyst", true);

        Slice<AdminUser> adminUsers = adminUserRepository.search("teste", AdminRole.ADMIN, true, FIRST_PAGE);

        assertThat(adminUsers.getContent()).extracting(AdminUser::getName).containsExactly("Ana Teste");
    }

    @Test
    void allergyIngredientSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> allergyIngredientRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void allergyIngredientSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> allergyIngredientRepository.search(1L, SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void compatibilityRuleSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> compatibilityRuleRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void compatibilityRuleSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> compatibilityRuleRepository.search(EffectType.BONUS, SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientAliasSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> ingredientAliasRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientAliasSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> ingredientAliasRepository.search("pt", SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientEffectSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> ingredientEffectRepository.search(null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientEffectSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> ingredientEffectRepository.search(1L, EffectCategory.BENEFIT, ReviewStatus.PENDING,
                SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void regulationSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> regulationRepository.search(null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void regulationSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> regulationRepository.search("cosmeticos", "Brasil", "ANVISA", RegulationStatus.ACTIVE, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> ingredientRepository.search(null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> ingredientRepository.search("agua", 1L, 1L, (short) 3, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientSearchAcceptsSortByInciName() {
        Pageable firstPageSortedByInciName = PageRequest.of(0, 20, Sort.by("inciName"));

        assertThatCode(() -> ingredientRepository.search(null, null, null, null, firstPageSortedByInciName))
                .doesNotThrowAnyException();
    }

    @Test
    void ingredientSearchByCategoryTreeBringsTheCategoryAndItsChildren() {
        Long surfactantCategoryId = insertIngredientCategory("Tensoativo Teste");
        Long anionicCategoryId = insertChildIngredientCategory("Tensoativo Anionico Teste", surfactantCategoryId);
        Long emollientCategoryId = insertIngredientCategory("Emoliente Teste");
        insertIngredient("SURFACTANT TEST", "Espuma", surfactantCategoryId);
        insertIngredient("ANIONIC TEST", "Espuma forte", anionicCategoryId);
        insertIngredient("EMOLLIENT TEST", "Oleo", emollientCategoryId);

        Slice<Ingredient> ingredients = ingredientRepository.search(null, null, surfactantCategoryId, null, FIRST_PAGE);

        assertThat(ingredients.getContent()).extracting(Ingredient::getInciName)
                .containsExactlyInAnyOrder("SURFACTANT TEST", "ANIONIC TEST");
    }

    @Test
    void ingredientSearchCombinesCategoryTreeAndCommonName() {
        Long surfactantCategoryId = insertIngredientCategory("Tensoativo Teste");
        Long anionicCategoryId = insertChildIngredientCategory("Tensoativo Anionico Teste", surfactantCategoryId);
        insertIngredient("SURFACTANT TEST", "Espuma", surfactantCategoryId);
        insertIngredient("ANIONIC TEST", "Limpeza", anionicCategoryId);

        Slice<Ingredient> ingredients = ingredientRepository.search("limp", null, surfactantCategoryId, null, FIRST_PAGE);

        assertThat(ingredients.getContent()).extracting(Ingredient::getInciName).containsExactly("ANIONIC TEST");
    }

    @Test
    void packagingSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> packagingRepository.search(null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void packagingSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> packagingRepository.search(PackagingMaterial.PLASTIC, true, false, true, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productClaimSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> productClaimRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productClaimSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> productClaimRepository.search(1L, SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productLabelSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> productLabelRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productLabelSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> productLabelRepository.search("pt", SourceType.OCR, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> productRepository.search(null, null, null, null, null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> productRepository.search("hidratante", 1L, 1L, true, "lavand", true, true, true, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productSearchByIngredientLooksOnlyAtTheCurrentVersion() {
        Long brandId = insertBrandWithClaims("Marca Produto Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        Long ingredientCategoryId = insertIngredientCategory("Agente Perfumante Teste");
        Long lavenderId = insertIngredient("LAVANDULA TEST OIL", "Oleo de lavanda", ingredientCategoryId);
        Long waterId = insertIngredient("AQUA TEST", "Agua", ingredientCategoryId);
        Long lavenderProductId = insertProduct("Hidratante Lavanda Teste", brandId, productCategoryId);
        insertProductVersion(lavenderProductId, "v1", true, lavenderId);
        Long reformulatedProductId = insertProduct("Hidratante Reformulado Teste", brandId, productCategoryId);
        insertProductVersion(reformulatedProductId, "v1", false, lavenderId);
        insertProductVersion(reformulatedProductId, "v2", true, waterId);

        Slice<Product> products = productRepository.search(null, null, null, null, "lavand", null, null, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName).containsExactly("Hidratante Lavanda Teste");
    }

    @Test
    void productSearchByIngredientFindsTheCommonName() {
        Long brandId = insertBrandWithClaims("Marca Produto Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        Long ingredientCategoryId = insertIngredientCategory("Agente Perfumante Teste");
        Long fennelId = insertIngredient("FOENICULUM VULGARE TEST OIL", "Oleo de erva-doce", ingredientCategoryId);
        Long fennelProductId = insertProduct("Sabonete Erva-Doce Teste", brandId, productCategoryId);
        insertProductVersion(fennelProductId, "v1", true, fennelId);

        Slice<Product> products = productRepository.search(null, null, null, null, "erva-doce", null, null, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName).containsExactly("Sabonete Erva-Doce Teste");
    }

    @Test
    void productSearchByIngredientBringsTheProductOnce() {
        Long brandId = insertBrandWithClaims("Marca Produto Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        Long ingredientCategoryId = insertIngredientCategory("Agente Perfumante Teste");
        Long lavenderOilId = insertIngredient("LAVANDULA TEST OIL", "Oleo de lavanda", ingredientCategoryId);
        Long lavenderWaterId = insertIngredient("LAVANDULA TEST WATER", "Agua de lavanda", ingredientCategoryId);
        Long lavenderProductId = insertProduct("Hidratante Lavanda Teste", brandId, productCategoryId);
        insertProductVersion(lavenderProductId, "v1", true, lavenderOilId, lavenderWaterId);

        Slice<Product> products = productRepository.search(null, null, null, null, "lavand", null, null, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName).containsExactly("Hidratante Lavanda Teste");
    }

    @Test
    void productSearchCombinesIngredientAndBrandClaim() {
        Long veganBrandId = insertBrandWithClaims("Marca Vegana Teste", false, true, false);
        Long commonBrandId = insertBrandWithClaims("Marca Comum Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        Long ingredientCategoryId = insertIngredientCategory("Agente Perfumante Teste");
        Long lavenderId = insertIngredient("LAVANDULA TEST OIL", "Oleo de lavanda", ingredientCategoryId);
        Long veganProductId = insertProduct("Hidratante Vegano Teste", veganBrandId, productCategoryId);
        insertProductVersion(veganProductId, "v1", true, lavenderId);
        Long commonProductId = insertProduct("Hidratante Comum Teste", commonBrandId, productCategoryId);
        insertProductVersion(commonProductId, "v1", true, lavenderId);

        Slice<Product> products = productRepository.search(null, null, null, null, "lavand", null, true, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName).containsExactly("Hidratante Vegano Teste");
    }

    @Test
    void productSearchCombinesTheThreeBrandClaims() {
        Long productCategoryId = insertProductCategory("Corpo Teste");
        insertProduct("Produto Certo Teste", insertBrandWithClaims("Marca Certa Teste", true, true, false), productCategoryId);
        insertProduct("Produto Sem Cruelty Free Teste",
                insertBrandWithClaims("Marca Sem Cruelty Free Teste", false, true, false), productCategoryId);
        insertProduct("Produto Sem Vegano Teste",
                insertBrandWithClaims("Marca Sem Vegano Teste", true, false, false), productCategoryId);
        insertProduct("Produto Brasileiro Teste",
                insertBrandWithClaims("Marca Brasileira Teste", true, true, true), productCategoryId);

        Slice<Product> products = productRepository.search(null, null, null, null, null, true, true, false, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName).containsExactly("Produto Certo Teste");
    }

    @Test
    void productNameIgnoresAccent() {
        Long brandId = insertBrandWithClaims("Marca Produto Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        insertProduct("Loção Hidratante Teste", brandId, productCategoryId);
        insertProduct("Locao Corporal Teste", brandId, productCategoryId);
        insertProduct("Sabonete Teste", brandId, productCategoryId);

        Slice<Product> products = productRepository.search("LOÇÃO", null, null, null, null, null, null, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName)
                .containsExactlyInAnyOrder("Loção Hidratante Teste", "Locao Corporal Teste");
    }

    @Test
    void productIngredientIgnoresAccent() {
        Long brandId = insertBrandWithClaims("Marca Produto Teste", false, false, false);
        Long productCategoryId = insertProductCategory("Corpo Teste");
        Long ingredientCategoryId = insertIngredientCategory("Solvente Teste");
        Long roseWaterId = insertIngredient("ROSA TEST WATER", "Água de rosas", ingredientCategoryId);
        Long thermalWaterId = insertIngredient("AQUA TEST THERMAL", "Agua termal", ingredientCategoryId);
        Long lavenderId = insertIngredient("LAVANDULA TEST OIL", "Oleo de lavanda", ingredientCategoryId);
        Long roseProductId = insertProduct("Tonico Rosas Teste", brandId, productCategoryId);
        insertProductVersion(roseProductId, "v1", true, roseWaterId);
        Long thermalProductId = insertProduct("Bruma Termal Teste", brandId, productCategoryId);
        insertProductVersion(thermalProductId, "v1", true, thermalWaterId);
        Long lavenderProductId = insertProduct("Hidratante Lavanda Teste", brandId, productCategoryId);
        insertProductVersion(lavenderProductId, "v1", true, lavenderId);

        Slice<Product> products = productRepository.search(null, null, null, null, "água", null, null, null, FIRST_PAGE);

        assertThat(products.getContent()).extracting(Product::getName)
                .containsExactlyInAnyOrder("Tonico Rosas Teste", "Bruma Termal Teste");
    }

    @Test
    void productVersionSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> productVersionRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void productVersionSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> productVersionRepository.search(VersionStatus.PENDING, "assinatura", FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void reportSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> reportRepository.search(null, null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void reportSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> reportRepository.search(1L, ReportStatus.OPEN, ReportTargetType.PRODUCT, 1L, 1L, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void analysisResultSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> analysisResultRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void analysisResultSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> analysisResultRepository.search(AnalysisStatus.COMPLETED, 50, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ruleEvaluationSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> ruleEvaluationRepository.search(null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void ruleEvaluationSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> ruleEvaluationRepository.search(1L, 1L, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void recommendationSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> recommendationRepository.search(null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void recommendationSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> recommendationRepository.search(1L, RecommendationType.IDEAL, 1L, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void reportSearchWithoutAdminFilterKeepsReportsWithoutAdmin() {
        Long userId = insertUser("uid-denuncia-teste", "Usuario Denuncia", "active");
        jdbcTemplate.update("INSERT INTO venus.reports (fk_user_id, target_type, target_id) VALUES (?, 'product', 99)", userId);

        Slice<Report> reports = reportRepository.search(userId, null, null, null, null, FIRST_PAGE);

        assertThat(reports.getContent()).hasSize(1);
    }

    @Test
    void userPreferenceSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> userPreferenceRepository.search(null, null, null, null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userPreferenceSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> userPreferenceRepository.search(true, true, false, true, false, true, false, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userProfileSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> userProfileRepository.search(null, null, null, null, null, null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userProfileSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> userProfileRepository.search(SkinType.OILY, HairPattern.TYPE_1A, SensitivityLevel.HIGH,
                true, false, false, AgeRange.AGE_18_24, Gender.FEMALE, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userSearchAcceptsAllFiltersEmpty() {
        assertThatCode(() -> userRepository.search(null, null, null, FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userSearchAcceptsAllFiltersFilled() {
        assertThatCode(() -> userRepository.search(UserStatus.ACTIVE, "ana", "uid-ana", FIRST_PAGE))
                .doesNotThrowAnyException();
    }

    @Test
    void userSearchWithOnlyFirebaseUidBringsThatUser() {
        insertUser("uid-ana-teste", "Ana", "active");
        insertUser("uid-bia-teste", "Bia", "active");

        Slice<User> users = userRepository.search(null, null, "uid-bia-teste", FIRST_PAGE);

        assertThat(users.getContent()).extracting(User::getFirebaseUid).containsExactly("uid-bia-teste");
        assertThat(users.isLast()).isTrue();
    }

    @Test
    void userSearchWithUnknownFirebaseUidBringsNothing() {
        insertUser("uid-ana-teste", "Ana", "active");

        Slice<User> users = userRepository.search(null, null, "uid-nao-existe", FIRST_PAGE);

        assertThat(users.getContent()).isEmpty();
    }

    @Test
    void userSearchCombinesFirebaseUidAndStatus() {
        insertUser("uid-ana-teste", "Ana", "active");

        Slice<User> users = userRepository.search(UserStatus.BLOCKED, null, "uid-ana-teste", FIRST_PAGE);

        assertThat(users.getContent()).isEmpty();
    }

    private void insertBrand(String name, String country, boolean isBrazilian) {
        jdbcTemplate.update("INSERT INTO venus.brands (name, country, is_brazilian) VALUES (?, ?, ?)",
                name, country, isBrazilian);
    }

    private void insertBrandWithoutCountry(String name) {
        jdbcTemplate.update("INSERT INTO venus.brands (name) VALUES (?)", name);
    }

    private void insertAdminUser(String name, String email, String role, boolean isActive) {
        jdbcTemplate.update("INSERT INTO venus.admin_users (name, email, role, is_active) VALUES (?, ?, ?, ?)",
                name, email, role, isActive);
    }

    private Long insertIngredientCategory(String name) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.ingredient_categories (name) VALUES (?) "
                + "RETURNING ingredient_category_id", Long.class, name);
    }

    private Long insertChildIngredientCategory(String name, Long parentCategoryId) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.ingredient_categories (name, parent_ingredient_category_id) "
                + "VALUES (?, ?) RETURNING ingredient_category_id", Long.class, name, parentCategoryId);
    }

    private Long insertIngredient(String inciName, String commonName, Long categoryId) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.ingredients (fk_ingredient_category_id, inci_name, common_name) "
                + "VALUES (?, ?, ?) RETURNING ingredient_id", Long.class, categoryId, inciName, commonName);
    }

    private Long insertBrandWithClaims(String name, boolean hasCrueltyFreeClaim, boolean hasVeganClaim,
            boolean isBrazilian) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.brands (name, has_cruelty_free_claim, has_vegan_claim, "
                + "is_brazilian) VALUES (?, ?, ?, ?) RETURNING brand_id", Long.class,
                name, hasCrueltyFreeClaim, hasVeganClaim, isBrazilian);
    }

    private Long insertProductCategory(String name) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.product_categories (name) VALUES (?) "
                + "RETURNING product_category_id", Long.class, name);
    }

    private Long insertProduct(String name, Long brandId, Long productCategoryId) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.products (fk_brand_id, fk_product_category_id, name, slug) "
                + "VALUES (?, ?, ?, ?) RETURNING product_id", Long.class, brandId, productCategoryId, name, name);
    }

    private void insertProductVersion(Long productId, String versionName, boolean isCurrent, Long... ingredientIds) {
        Long productVersionId = jdbcTemplate.queryForObject("INSERT INTO venus.product_versions (fk_product_id, "
                + "version_name, display_name, is_current, formula_signature) VALUES (?, ?, ?, ?, ?) "
                + "RETURNING product_version_id", Long.class, productId, versionName, versionName, isCurrent, versionName);
        for (int position = 1; position <= ingredientIds.length; position++) {
            jdbcTemplate.update("INSERT INTO venus.product_ingredients (fk_product_version_id, fk_ingredient_id, position) "
                    + "VALUES (?, ?, ?)", productVersionId, ingredientIds[position - 1], position);
        }
    }

    private Long insertUser(String firebaseUid, String name, String status) {
        return jdbcTemplate.queryForObject("INSERT INTO venus.users (firebase_uid, name, status) VALUES (?, ?, ?) "
                + "RETURNING user_id", Long.class, firebaseUid, name, status);
    }
}

package com.venus.crud.service.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.venus.crud.document.IngredientCandidate;
import com.venus.crud.document.IngredientMatch;
import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanIngredient;
import com.venus.crud.document.ScanIngredientDecision;
import com.venus.crud.document.ScanSession;
import com.venus.crud.dto.mongo.request.ScanApproveRequest;
import com.venus.crud.dto.mongo.request.ScanIngredientDecisionRequest;
import com.venus.crud.dto.mongo.request.ScanProductDecisionRequest;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.IngredientDecisionAction;
import com.venus.crud.entity.enums.IngredientMatchStatus;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.exception.DataConstraintException;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ScanReviewValidatorTest {

    private static final long ADMIN_ID = 3L;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    private ScanReviewValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ScanReviewValidator(adminUserRepository, productRepository, brandRepository,
                productCategoryRepository, ingredientRepository);
    }

    @Test
    void validApprovalResolvesKnownIngredientsAndAppliesTheDecisions() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        when(ingredientRepository.findAllById(any())).thenReturn(List.of(ingredient(100L, "Aqua"), ingredient(201L, "Tocopherol")));
        ScanSession scan = scan(known(1, 100L), ambiguous(2, 200L, 201L), newOne(3, "Coumarln"));

        ScanApprovedSnapshot snapshot = validator.validate(scan, approve(newProduct(),
                decision(2, IngredientDecisionAction.LINK, 201L, null),
                decision(3, IngredientDecisionAction.CREATE, null, "Coumarin")), ADMIN_ID);

        assertThat(snapshot.getProduct().getName()).isEqualTo("Sérum Vitamina C");
        assertThat(snapshot.getProduct().getBrandId()).isEqualTo(12L);
        assertThat(snapshot.getProduct().getProductCategoryId()).isEqualTo(4L);
        assertThat(snapshot.getIngredients())
                .extracting(ScanIngredientDecision::getPosition, ScanIngredientDecision::getAction,
                        ScanIngredientDecision::getIngredientId, ScanIngredientDecision::getInciName)
                .containsExactly(
                        tuple(1, IngredientDecisionAction.LINK, 100L, null),
                        tuple(2, IngredientDecisionAction.LINK, 201L, null),
                        tuple(3, IngredientDecisionAction.CREATE, null, "Coumarin"));
    }

    @Test
    void inactiveAdminIsRefused() {
        reviewer(AdminRole.MODERATOR, false);
        newProductDataExists();
        assertViolations(scan(known(1, 100L)), approve(newProduct()), "administrador 3 nao encontrado ou inativo");
    }

    @Test
    void analystCannotReview() {
        reviewer(AdminRole.ANALYST, true);
        newProductDataExists();
        assertViolations(scan(known(1, 100L)), approve(newProduct()), "nao pode aprovar nem recusar");
    }

    @Test
    void existingAndNewProductTogetherAreRefused() {
        reviewer(AdminRole.MODERATOR, true);
        ScanProductDecisionRequest both = new ScanProductDecisionRequest(7L, "Sérum Vitamina C", 12L, 4L);
        assertViolations(scan(known(1, 100L)), approve(both), "nao os dois");
    }

    @Test
    void unknownAndRepeatedPositionsAreRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(known(1, 100L)),
                approve(newProduct(),
                        decision(9, IngredientDecisionAction.DISCARD, null, null),
                        decision(1, IngredientDecisionAction.DISCARD, null, null),
                        decision(1, IngredientDecisionAction.LINK, 100L, null)),
                "posicao 9 nao existe no scan", "mais de uma decisao para a posicao 1");
    }

    @Test
    void ambiguousWithoutDecisionIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(ambiguous(2, 200L, 201L)), approve(newProduct()), "e ambigua; escolha um dos candidatos [200, 201]");
    }

    @Test
    void ambiguousLinkedOutsideTheCandidatesIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(ambiguous(2, 200L, 201L)),
                approve(newProduct(), decision(2, IngredientDecisionAction.LINK, 999L, null)),
                "o ingrediente 999 nao esta entre os candidatos [200, 201]");
    }

    @Test
    void newWithoutDecisionIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(newOne(3, "Coumarln")), approve(newProduct()), "nao existe no catalogo; decida LINK, CREATE ou DISCARD");
    }

    @Test
    void knownIngredientDeletedFromTheCatalogIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(known(1, 100L)), approve(newProduct()), "ingrediente 100 nao existe no catalogo");
    }

    @Test
    void creatingAnExistingNameAsksForLink() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        when(ingredientRepository.findByUpperInciNameIn(any())).thenReturn(List.of(ingredient(55L, "Coumarin")));
        assertViolations(scan(newOne(3, "Coumarln")),
                approve(newProduct(), decision(3, IngredientDecisionAction.CREATE, null, "coumarin")),
                "o ingrediente Coumarin ja existe com id 55; use LINK");
    }

    @Test
    void sameIngredientInTwoPositionsIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        when(ingredientRepository.findAllById(any())).thenReturn(List.of(ingredient(100L, "Aqua")));
        assertViolations(scan(known(1, 100L), known(2, 100L)), approve(newProduct()),
                "o mesmo ingrediente (id 100) aparece nas posicoes [1, 2]");
    }

    @Test
    void scanWithoutIngredientsIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(new ScanSession(), approve(newProduct()), "o produto precisa de pelo menos um ingrediente");
    }

    @Test
    void everythingDiscardedIsRefused() {
        reviewer(AdminRole.MODERATOR, true);
        newProductDataExists();
        assertViolations(scan(known(1, 100L)),
                approve(newProduct(), decision(1, IngredientDecisionAction.DISCARD, null, null)),
                "o produto precisa de pelo menos um ingrediente");
    }

    private void assertViolations(ScanSession scan, ScanApproveRequest request, String... expected) {
        assertThatThrownBy(() -> validator.validate(scan, request, ADMIN_ID))
                .isInstanceOfSatisfying(DataConstraintException.class, ex -> {
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    for (String fragment : expected) {
                        assertThat(ex.getDetails()).anyMatch(detail -> detail.contains(fragment));
                    }
                });
    }

    private void reviewer(AdminRole role, boolean active) {
        AdminUser admin = new AdminUser();
        admin.setId(ADMIN_ID);
        admin.setRole(role);
        admin.setIsActive(active);
        lenient().when(adminUserRepository.findById(ADMIN_ID)).thenReturn(Optional.of(admin));
    }

    private void newProductDataExists() {
        lenient().when(brandRepository.existsById(12L)).thenReturn(true);
        lenient().when(productCategoryRepository.existsById(4L)).thenReturn(true);
    }

    private ScanProductDecisionRequest newProduct() {
        return new ScanProductDecisionRequest(null, "Sérum Vitamina C", 12L, 4L);
    }

    private ScanApproveRequest approve(ScanProductDecisionRequest product, ScanIngredientDecisionRequest... decisions) {
        return new ScanApproveRequest(product, List.of(decisions), null);
    }

    private ScanIngredientDecisionRequest decision(int position, IngredientDecisionAction action, Long ingredientId,
            String inciName) {
        return new ScanIngredientDecisionRequest(position, action, ingredientId, inciName);
    }

    private ScanSession scan(ScanIngredient... ingredients) {
        ScanSession scanSession = new ScanSession();
        scanSession.setIngredients(List.of(ingredients));
        return scanSession;
    }

    private ScanIngredient known(int position, long ingredientId) {
        return scanned(position, "Aqua", IngredientMatchStatus.KNOWN, ingredientId);
    }

    private ScanIngredient ambiguous(int position, Long... candidateIds) {
        ScanIngredient ingredient = scanned(position, "Vitamin E", IngredientMatchStatus.AMBIGUOUS, null);
        ingredient.getMatch().setCandidates(Arrays.stream(candidateIds).map(id -> {
            IngredientCandidate candidate = new IngredientCandidate();
            candidate.setIngredientId(id);
            return candidate;
        }).toList());
        return ingredient;
    }

    private ScanIngredient newOne(int position, String rawName) {
        return scanned(position, rawName, IngredientMatchStatus.NEW, null);
    }

    private ScanIngredient scanned(int position, String rawName, IngredientMatchStatus status, Long ingredientId) {
        IngredientMatch match = new IngredientMatch();
        match.setStatus(status);
        match.setIngredientId(ingredientId);
        match.setCandidates(List.of());
        ScanIngredient ingredient = new ScanIngredient();
        ingredient.setPosition(position);
        ingredient.setRawName(rawName);
        ingredient.setMatch(match);
        return ingredient;
    }

    private Ingredient ingredient(Long id, String inciName) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setInciName(inciName);
        return ingredient;
    }
}

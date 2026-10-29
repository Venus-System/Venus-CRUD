package com.venus.crud.service.mongo;

import com.venus.crud.document.IngredientCandidate;
import com.venus.crud.document.IngredientMatch;
import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanIngredient;
import com.venus.crud.document.ScanIngredientDecision;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSnapshotProduct;
import com.venus.crud.dto.mongo.request.ScanApproveRequest;
import com.venus.crud.dto.mongo.request.ScanIngredientDecisionRequest;
import com.venus.crud.dto.mongo.request.ScanProductDecisionRequest;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.IngredientDecisionAction;
import com.venus.crud.entity.enums.IngredientMatchStatus;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataConstraintException;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ScanReviewValidator {

    private static final Logger log = LoggerFactory.getLogger(ScanReviewValidator.class);

    private static final String INVALID_DECISIONS_MESSAGE = "Decisoes da revisao do scan invalidas";
    private static final Set<AdminRole> REVIEWER_ROLES = EnumSet.of(AdminRole.ADMIN, AdminRole.MODERATOR);

    private final AdminUserRepository adminUserRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final IngredientRepository ingredientRepository;

    public ScanReviewValidator(AdminUserRepository adminUserRepository, ProductRepository productRepository,
            BrandRepository brandRepository, ProductCategoryRepository productCategoryRepository,
            IngredientRepository ingredientRepository) {
        this.adminUserRepository = adminUserRepository;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public ScanApprovedSnapshot validate(ScanSession scanSession, ScanApproveRequest request, Long adminUserId) {
        List<String> violations = new ArrayList<>();
        checkReviewer(adminUserId, violations);
        ScanSnapshotProduct product = resolveProduct(request.product(), violations);
        List<ScanIngredientDecision> ingredients = resolveIngredients(scanSession.getIngredients(), request.ingredients(), violations);
        throwIfAny(violations);

        ScanApprovedSnapshot snapshot = new ScanApprovedSnapshot();
        snapshot.setProduct(product);
        snapshot.setIngredients(ingredients);
        return snapshot;
    }

    public void validateReviewer(Long adminUserId) {
        List<String> violations = new ArrayList<>();
        checkReviewer(adminUserId, violations);
        throwIfAny(violations);
    }

    private void checkReviewer(Long adminUserId, List<String> violations) {
        Optional<AdminUser> admin = find(() -> adminUserRepository.findById(adminUserId), "Falha ao consultar o administrador");
        if (admin.isEmpty() || !Boolean.TRUE.equals(admin.get().getIsActive())) {
            violations.add("adminUserId: administrador " + adminUserId + " nao encontrado ou inativo");
            return;
        }
        if (!REVIEWER_ROLES.contains(admin.get().getRole())) {
            violations.add("adminUserId: o papel " + admin.get().getRole() + " nao pode aprovar nem recusar scan");
        }
    }

    private ScanSnapshotProduct resolveProduct(ScanProductDecisionRequest request, List<String> violations) {
        ScanSnapshotProduct product = new ScanSnapshotProduct();
        boolean hasNewProductData = StringUtils.hasText(request.name()) || request.brandId() != null
                || request.productCategoryId() != null;
        if (request.productId() != null) {
            if (hasNewProductData) {
                violations.add("product: informe o productId de um produto existente ou os dados de um produto novo, nao os dois");
            } else if (!exists(() -> productRepository.existsById(request.productId()))) {
                violations.add("product.productId: produto " + request.productId() + " nao encontrado");
            }
            product.setProductId(request.productId());
            return product;
        }
        if (!StringUtils.hasText(request.name())) {
            violations.add("product.name: obrigatorio para produto novo");
        }
        if (request.brandId() == null) {
            violations.add("product.brandId: obrigatorio para produto novo");
        } else if (!exists(() -> brandRepository.existsById(request.brandId()))) {
            violations.add("product.brandId: marca " + request.brandId() + " nao encontrada");
        }
        if (request.productCategoryId() == null) {
            violations.add("product.productCategoryId: obrigatorio para produto novo");
        } else if (!exists(() -> productCategoryRepository.existsById(request.productCategoryId()))) {
            violations.add("product.productCategoryId: categoria " + request.productCategoryId() + " nao encontrada");
        }
        product.setName(StringUtils.hasText(request.name()) ? request.name().trim() : null);
        product.setBrandId(request.brandId());
        product.setProductCategoryId(request.productCategoryId());
        return product;
    }

    private List<ScanIngredientDecision> resolveIngredients(List<ScanIngredient> scanned,
            List<ScanIngredientDecisionRequest> decisions, List<String> violations) {
        List<ScanIngredient> ingredients = scanned == null ? List.of()
                : scanned.stream().sorted(Comparator.comparing(ScanIngredient::getPosition)).toList();
        Set<Integer> positions = ingredients.stream().map(ScanIngredient::getPosition).collect(Collectors.toSet());
        Map<Integer, ScanIngredientDecisionRequest> decisionsByPosition = indexDecisions(decisions, positions, violations);

        int violationsBefore = violations.size();
        List<ScanIngredientDecision> resolved = new ArrayList<>();
        for (ScanIngredient ingredient : ingredients) {
            ScanIngredientDecision decision = resolve(ingredient, decisionsByPosition.get(ingredient.getPosition()), violations);
            if (decision != null) {
                resolved.add(decision);
            }
        }
        if (resolved.isEmpty() && violations.size() == violationsBefore) {
            violations.add("ingredients: o produto precisa de pelo menos um ingrediente");
        }
        checkLinkedIngredientsExist(resolved, violations);
        checkCreatedNamesAreNew(resolved, violations);
        checkNoRepeatedIngredient(resolved, violations);
        return resolved;
    }

    private Map<Integer, ScanIngredientDecisionRequest> indexDecisions(List<ScanIngredientDecisionRequest> decisions,
            Set<Integer> positions, List<String> violations) {
        Map<Integer, ScanIngredientDecisionRequest> byPosition = new HashMap<>();
        if (decisions == null) {
            return byPosition;
        }
        Set<Integer> unknown = new TreeSet<>();
        Set<Integer> repeated = new TreeSet<>();
        for (ScanIngredientDecisionRequest decision : decisions) {
            if (!positions.contains(decision.position())) {
                unknown.add(decision.position());
            } else if (byPosition.putIfAbsent(decision.position(), decision) != null) {
                repeated.add(decision.position());
            }
        }
        unknown.forEach(position -> violations.add("ingredients: posicao " + position + " nao existe no scan"));
        repeated.forEach(position -> violations.add("ingredients: mais de uma decisao para a posicao " + position));
        return byPosition;
    }

    private ScanIngredientDecision resolve(ScanIngredient ingredient, ScanIngredientDecisionRequest decision,
            List<String> violations) {
        IngredientMatch match = ingredient.getMatch();
        IngredientMatchStatus status = match == null || match.getStatus() == null ? IngredientMatchStatus.NEW : match.getStatus();
        String where = "ingredients: posicao " + ingredient.getPosition() + " (" + ingredient.getRawName() + ")";
        if (decision == null) {
            if (status == IngredientMatchStatus.KNOWN || status == IngredientMatchStatus.ALIAS) {
                return link(ingredient.getPosition(), match.getIngredientId());
            }
            violations.add(status == IngredientMatchStatus.AMBIGUOUS
                    ? where + " e ambigua; escolha um dos candidatos " + candidateIds(match)
                    : where + " nao existe no catalogo; decida LINK, CREATE ou DISCARD");
            return null;
        }
        return switch (decision.action()) {
            case DISCARD -> null;
            case LINK -> linkDecision(ingredient, decision, status, match, where, violations);
            case CREATE -> createDecision(ingredient, decision, where, violations);
        };
    }

    private ScanIngredientDecision linkDecision(ScanIngredient ingredient, ScanIngredientDecisionRequest decision,
            IngredientMatchStatus status, IngredientMatch match, String where, List<String> violations) {
        if (decision.ingredientId() == null) {
            violations.add(where + ": LINK precisa de ingredientId");
            return null;
        }
        if (status == IngredientMatchStatus.AMBIGUOUS && !candidateIds(match).contains(decision.ingredientId())) {
            violations.add(where + ": o ingrediente " + decision.ingredientId() + " nao esta entre os candidatos "
                    + candidateIds(match));
            return null;
        }
        return link(ingredient.getPosition(), decision.ingredientId());
    }

    private ScanIngredientDecision createDecision(ScanIngredient ingredient, ScanIngredientDecisionRequest decision,
            String where, List<String> violations) {
        if (!StringUtils.hasText(decision.inciName())) {
            violations.add(where + ": CREATE precisa de inciName");
            return null;
        }
        ScanIngredientDecision created = new ScanIngredientDecision();
        created.setPosition(ingredient.getPosition());
        created.setAction(IngredientDecisionAction.CREATE);
        created.setInciName(decision.inciName().trim());
        return created;
    }

    private ScanIngredientDecision link(Integer position, Long ingredientId) {
        ScanIngredientDecision linked = new ScanIngredientDecision();
        linked.setPosition(position);
        linked.setAction(IngredientDecisionAction.LINK);
        linked.setIngredientId(ingredientId);
        return linked;
    }

    private List<Long> candidateIds(IngredientMatch match) {
        if (match == null || match.getCandidates() == null) {
            return List.of();
        }
        return match.getCandidates().stream().map(IngredientCandidate::getIngredientId).toList();
    }

    private void checkLinkedIngredientsExist(List<ScanIngredientDecision> resolved, List<String> violations) {
        Set<Long> linkedIds = resolved.stream()
                .filter(decision -> decision.getAction() == IngredientDecisionAction.LINK)
                .map(ScanIngredientDecision::getIngredientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        if (linkedIds.isEmpty()) {
            return;
        }
        Set<Long> found = find(() -> ingredientRepository.findAllById(linkedIds), "Falha ao consultar os ingredientes da revisao")
                .stream().map(Ingredient::getId).collect(Collectors.toSet());
        linkedIds.stream()
                .filter(id -> !found.contains(id))
                .forEach(id -> violations.add("ingredients: ingrediente " + id + " nao existe no catalogo"));
    }

    private void checkCreatedNamesAreNew(List<ScanIngredientDecision> resolved, List<String> violations) {
        Set<String> createdNames = resolved.stream()
                .filter(decision -> decision.getAction() == IngredientDecisionAction.CREATE)
                .map(decision -> decision.getInciName().toUpperCase(Locale.ROOT))
                .collect(Collectors.toCollection(TreeSet::new));
        if (createdNames.isEmpty()) {
            return;
        }
        find(() -> ingredientRepository.findByUpperInciNameIn(createdNames), "Falha ao consultar os ingredientes da revisao")
                .forEach(existing -> violations.add("ingredients: o ingrediente " + existing.getInciName()
                        + " ja existe com id " + existing.getId() + "; use LINK"));
    }

    private void checkNoRepeatedIngredient(List<ScanIngredientDecision> resolved, List<String> violations) {
        Map<String, List<Integer>> positionsByIngredient = new LinkedHashMap<>();
        for (ScanIngredientDecision decision : resolved) {
            String key = decision.getAction() == IngredientDecisionAction.LINK
                    ? "id " + decision.getIngredientId()
                    : "nome " + decision.getInciName().toUpperCase(Locale.ROOT);
            positionsByIngredient.computeIfAbsent(key, ignored -> new ArrayList<>()).add(decision.getPosition());
        }
        positionsByIngredient.forEach((key, samePositions) -> {
            if (samePositions.size() > 1) {
                violations.add("ingredients: o mesmo ingrediente (" + key + ") aparece nas posicoes " + samePositions);
            }
        });
    }

    private boolean exists(Supplier<Boolean> check) {
        return Boolean.TRUE.equals(find(check, "Falha ao conferir o cadastro informado na revisao"));
    }

    private void throwIfAny(List<String> violations) {
        if (!violations.isEmpty()) {
            throw new DataConstraintException(HttpStatus.UNPROCESSABLE_ENTITY, INVALID_DECISIONS_MESSAGE, List.copyOf(violations));
        }
    }

    private <T> T find(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}

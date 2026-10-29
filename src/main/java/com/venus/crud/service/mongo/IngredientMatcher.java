package com.venus.crud.service.mongo;

import com.venus.crud.document.IngredientCandidate;
import com.venus.crud.document.IngredientMatch;
import com.venus.crud.document.ScanIngredient;
import com.venus.crud.entity.enums.IngredientMatchStatus;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientAlias;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.repository.jpa.ingredient.IngredientAliasRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientMatcher {

    private static final Logger log = LoggerFactory.getLogger(IngredientMatcher.class);

    private final IngredientRepository ingredientRepository;
    private final IngredientAliasRepository ingredientAliasRepository;

    public IngredientMatcher(IngredientRepository ingredientRepository, IngredientAliasRepository ingredientAliasRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientAliasRepository = ingredientAliasRepository;
    }

    @Transactional(readOnly = true)
    public void matchAll(List<ScanIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }
        Set<String> names = new LinkedHashSet<>();
        for (ScanIngredient ingredient : ingredients) {
            ingredient.setNormalizedName(IngredientNameNormalizer.normalize(ingredient.getRawName()));
            names.add(ingredient.getNormalizedName());
        }

        Map<String, List<Ingredient>> byInciName = findByInciName(names);
        Set<String> notFoundByInci = new LinkedHashSet<>(names);
        notFoundByInci.removeAll(byInciName.keySet());
        Map<String, List<Ingredient>> byAlias = findByAlias(notFoundByInci);

        for (ScanIngredient ingredient : ingredients) {
            ingredient.setMatch(resolve(ingredient.getNormalizedName(), byInciName, byAlias));
        }
    }

    private Map<String, List<Ingredient>> findByInciName(Set<String> names) {
        Map<String, List<Ingredient>> grouped = new LinkedHashMap<>();
        List<Ingredient> found = executeOrFail(() -> ingredientRepository.findByUpperInciNameIn(names),
                "Falha ao consultar ingredientes pelo nome INCI");
        for (Ingredient ingredient : found) {
            addOnce(grouped, upper(ingredient.getInciName()), ingredient);
        }
        return grouped;
    }

    private Map<String, List<Ingredient>> findByAlias(Set<String> names) {
        Map<String, List<Ingredient>> grouped = new LinkedHashMap<>();
        if (names.isEmpty()) {
            return grouped;
        }
        List<IngredientAlias> found = executeOrFail(() -> ingredientAliasRepository.findWithIngredientByUpperAliasNameIn(names),
                "Falha ao consultar ingredientes pelo apelido");
        for (IngredientAlias alias : found) {
            addOnce(grouped, upper(alias.getAliasName()), alias.getIngredient());
        }
        return grouped;
    }

    private void addOnce(Map<String, List<Ingredient>> grouped, String name, Ingredient ingredient) {
        List<Ingredient> sameName = grouped.computeIfAbsent(name, key -> new ArrayList<>());
        boolean alreadyListed = sameName.stream().anyMatch(listed -> listed.getId().equals(ingredient.getId()));
        if (!alreadyListed) {
            sameName.add(ingredient);
        }
    }

    private IngredientMatch resolve(String normalizedName, Map<String, List<Ingredient>> byInciName,
            Map<String, List<Ingredient>> byAlias) {
        List<Ingredient> inciMatches = byInciName.getOrDefault(normalizedName, List.of());
        if (!inciMatches.isEmpty()) {
            return inciMatches.size() == 1 ? found(IngredientMatchStatus.KNOWN, inciMatches.get(0)) : ambiguous(inciMatches);
        }
        List<Ingredient> aliasMatches = byAlias.getOrDefault(normalizedName, List.of());
        if (!aliasMatches.isEmpty()) {
            return aliasMatches.size() == 1 ? found(IngredientMatchStatus.ALIAS, aliasMatches.get(0)) : ambiguous(aliasMatches);
        }
        return notFound();
    }

    private IngredientMatch found(IngredientMatchStatus status, Ingredient ingredient) {
        IngredientMatch match = new IngredientMatch();
        match.setStatus(status);
        match.setIngredientId(ingredient.getId());
        match.setMatchedName(ingredient.getInciName());
        match.setCandidates(List.of());
        return match;
    }

    private IngredientMatch ambiguous(List<Ingredient> ingredients) {
        IngredientMatch match = new IngredientMatch();
        match.setStatus(IngredientMatchStatus.AMBIGUOUS);
        match.setCandidates(ingredients.stream().map(this::toCandidate).toList());
        return match;
    }

    private IngredientMatch notFound() {
        IngredientMatch match = new IngredientMatch();
        match.setStatus(IngredientMatchStatus.NEW);
        match.setCandidates(List.of());
        return match;
    }

    private IngredientCandidate toCandidate(Ingredient ingredient) {
        IngredientCandidate candidate = new IngredientCandidate();
        candidate.setIngredientId(ingredient.getId());
        candidate.setInciName(ingredient.getInciName());
        return candidate;
    }

    private String upper(String value) {
        return value.toUpperCase(Locale.ROOT);
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}

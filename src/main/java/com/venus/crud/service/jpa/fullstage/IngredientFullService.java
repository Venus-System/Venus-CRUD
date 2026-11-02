package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.IngredientEffectDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.IngredientFullResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientEffect;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.ingredient.IngredientAliasMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientCategoryMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientPropertyMapper;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientAliasRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientCategoryRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientEffectRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientPropertyRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientFullService {

    private static final Logger log = LoggerFactory.getLogger(IngredientFullService.class);

    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IngredientAliasRepository ingredientAliasRepository;
    private final IngredientPropertyRepository ingredientPropertyRepository;
    private final IngredientEffectRepository ingredientEffectRepository;
    private final IngredientMapper ingredientMapper;
    private final IngredientCategoryMapper ingredientCategoryMapper;
    private final IngredientAliasMapper ingredientAliasMapper;
    private final IngredientPropertyMapper ingredientPropertyMapper;
    private final ProfileTagMapper profileTagMapper;

    public IngredientFullService(IngredientRepository ingredientRepository, IngredientCategoryRepository ingredientCategoryRepository,
            IngredientAliasRepository ingredientAliasRepository, IngredientPropertyRepository ingredientPropertyRepository,
            IngredientEffectRepository ingredientEffectRepository, IngredientMapper ingredientMapper,
            IngredientCategoryMapper ingredientCategoryMapper, IngredientAliasMapper ingredientAliasMapper,
            IngredientPropertyMapper ingredientPropertyMapper, ProfileTagMapper profileTagMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.ingredientAliasRepository = ingredientAliasRepository;
        this.ingredientPropertyRepository = ingredientPropertyRepository;
        this.ingredientEffectRepository = ingredientEffectRepository;
        this.ingredientMapper = ingredientMapper;
        this.ingredientCategoryMapper = ingredientCategoryMapper;
        this.ingredientAliasMapper = ingredientAliasMapper;
        this.ingredientPropertyMapper = ingredientPropertyMapper;
        this.profileTagMapper = profileTagMapper;
    }

    @Transactional(readOnly = true)
    public IngredientFullResponse findById(Long id) {
        Ingredient ingredient = getOrThrow(id);
        IngredientCategoryResponse category = findCategory(ingredient);
        List<IngredientAliasResponse> aliases = findAliases(id);
        List<IngredientPropertyResponse> properties = findProperties(id);
        List<IngredientEffectDetailResponse> effects = findEffects(id);

        return new IngredientFullResponse(ingredientMapper.toResponse(ingredient), category, aliases, properties, effects);
    }

    private Ingredient getOrThrow(Long id) {
        return executeOrFail(() -> ingredientRepository.findById(id), "Falha ao consultar ingrediente no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado com id " + id));
    }

    private IngredientCategoryResponse findCategory(Ingredient ingredient) {
        return executeOrFail(
                () -> ingredientCategoryRepository.findById(ingredient.getIngredientCategory().getId()),
                "Falha ao consultar categoria do ingrediente")
                .map(ingredientCategoryMapper::toResponse)
                .orElse(null);
    }

    private List<IngredientAliasResponse> findAliases(Long ingredientId) {
        return executeOrFail(() -> ingredientAliasRepository.findByIngredientId(ingredientId), "Falha ao consultar apelidos do ingrediente")
                .stream()
                .map(ingredientAliasMapper::toResponse)
                .toList();
    }

    private List<IngredientPropertyResponse> findProperties(Long ingredientId) {
        return executeOrFail(() -> ingredientPropertyRepository.findByIngredientId(ingredientId), "Falha ao consultar propriedades do ingrediente")
                .stream()
                .map(ingredientPropertyMapper::toResponse)
                .toList();
    }

    private List<IngredientEffectDetailResponse> findEffects(Long ingredientId) {
        return executeOrFail(() -> ingredientEffectRepository.findByIngredientId(ingredientId), "Falha ao consultar efeitos do ingrediente")
                .stream()
                .map(this::toEffectDetail)
                .toList();
    }

    private IngredientEffectDetailResponse toEffectDetail(IngredientEffect effect) {
        return new IngredientEffectDetailResponse(
                effect.getId(),
                profileTagMapper.toResponse(effect.getProfileTag()),
                effect.getEffectCategory(),
                effect.getEffectName(),
                effect.getEffectDescription(),
                effect.getEffectStrength(),
                effect.getEvidenceLevel(),
                effect.getReviewStatus(),
                effect.getSourceType(),
                effect.getSourceReference());
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
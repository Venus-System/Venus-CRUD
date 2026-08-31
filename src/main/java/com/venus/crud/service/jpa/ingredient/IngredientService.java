package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class IngredientService {

    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @Transactional(readOnly = true)
    public List<IngredientResponse> findAll() {
        return executeOrFail(ingredientRepository::findAll, "Falha ao consultar ingredientes no banco de dados").stream()
                .map(ingredientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredientResponse findById(Long id) {
        return ingredientMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public IngredientResponse findByInciName(String inciName) {
        return executeOrFail(() -> ingredientRepository.findByInciName(inciName), "Falha ao consultar ingrediente por nome INCI")
                .map(ingredientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado com o nome INCI " + inciName));
    }

    @Transactional(readOnly = true)
    public IngredientResponse findBySourceReference(String sourceReference) {
        return executeOrFail(() -> ingredientRepository.findBySourceReference(sourceReference), "Falha ao consultar ingrediente por referencia de origem")
                .map(ingredientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado com a referencia de origem " + sourceReference));
    }

    @Transactional(readOnly = true)
    public Slice<IngredientResponse> search(String commonName, Long ingredientCategoryId, Short minIrritationRiskLevel, Pageable pageable) {
        Slice<Ingredient> result;
        if (StringUtils.hasText(commonName)) {
            result = executeOrFail(() -> ingredientRepository.findByCommonNameContainingIgnoreCase(commonName, pageable),
                    "Falha ao consultar ingredientes por nome comum");
        } else if (ingredientCategoryId != null) {
            result = executeOrFail(() -> ingredientRepository.findByIngredientCategoryId(ingredientCategoryId, pageable),
                    "Falha ao consultar ingredientes por categoria");
        } else if (minIrritationRiskLevel != null) {
            result = executeOrFail(() -> ingredientRepository.findByIrritationRiskLevelGreaterThanEqual(minIrritationRiskLevel, pageable),
                    "Falha ao consultar ingredientes por nivel de irritacao");
        } else {
            result = executeOrFail(() -> ingredientRepository.findAllBy(pageable), "Falha ao consultar ingredientes");
        }

        return result.map(ingredientMapper::toResponse);
    }

    @Transactional
    public IngredientResponse create(IngredientRequest request) {
        ensureInciNameAvailable(request.inciName(), null);

        Ingredient ingredient = ingredientMapper.toEntity(request);
        Ingredient saved = executeOrFail(() -> ingredientRepository.save(ingredient), "Falha ao criar ingrediente no banco de dados");
        return ingredientMapper.toResponse(saved);
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = getOrThrow(id);
        ensureInciNameAvailable(request.inciName(), id);

        ingredientMapper.updateEntity(request, ingredient);
        Ingredient saved = executeOrFail(() -> ingredientRepository.save(ingredient), "Falha ao atualizar ingrediente no banco de dados");
        return ingredientMapper.toResponse(saved);
    }

    @Transactional
    public IngredientResponse patch(Long id, IngredientPatchRequest request) {
        Ingredient ingredient = getOrThrow(id);
        if (StringUtils.hasText(request.inciName())) {
            ensureInciNameAvailable(request.inciName(), id);
        }

        ingredientMapper.patchEntity(request, ingredient);
        Ingredient saved = executeOrFail(() -> ingredientRepository.save(ingredient), "Falha ao atualizar ingrediente no banco de dados");
        return ingredientMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Ingredient ingredient = getOrThrow(id);
        executeOrFail(() -> {
            ingredientRepository.delete(ingredient);
            return null;
        }, "Falha ao remover ingrediente no banco de dados");
    }

    private Ingredient getOrThrow(Long id) {
        Optional<Ingredient> ingredient = executeOrFail(() -> ingredientRepository.findById(id), "Falha ao consultar ingrediente no banco de dados");
        return ingredient.orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado com id " + id));
    }

    private void ensureInciNameAvailable(String inciName, Long excludeId) {
        boolean inUse = executeOrFail(() -> ingredientRepository.findByInciName(inciName), "Falha ao verificar nome INCI do ingrediente")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um ingrediente com o nome INCI " + inciName);
        }
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violacao de integridade de dados: {}", ex.getMessage());
            throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }
}
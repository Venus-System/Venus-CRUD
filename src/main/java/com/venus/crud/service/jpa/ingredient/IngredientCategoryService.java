package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientCategoryRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.entity.ingredient.IngredientCategory;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientCategoryMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientCategoryRepository;
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
public class IngredientCategoryService {

    private static final Logger log = LoggerFactory.getLogger(IngredientCategoryService.class);

    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IngredientCategoryMapper ingredientCategoryMapper;

    public IngredientCategoryService(IngredientCategoryRepository ingredientCategoryRepository,
            IngredientCategoryMapper ingredientCategoryMapper) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.ingredientCategoryMapper = ingredientCategoryMapper;
    }

    @Transactional(readOnly = true)
    public List<IngredientCategoryResponse> findAll() {
        return executeOrFail(ingredientCategoryRepository::findAll, "Falha ao consultar categorias de ingrediente no banco de dados").stream()
                .map(ingredientCategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredientCategoryResponse findById(Long id) {
        return ingredientCategoryMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<IngredientCategoryResponse> search(String name, Pageable pageable) {
        Slice<IngredientCategory> result = StringUtils.hasText(name)
                ? executeOrFail(() -> ingredientCategoryRepository.findByNameContainingIgnoreCase(name, pageable),
                        "Falha ao consultar categorias de ingrediente por nome")
                : executeOrFail(() -> ingredientCategoryRepository.findAllBy(pageable), "Falha ao consultar categorias de ingrediente");

        return result.map(ingredientCategoryMapper::toResponse);
    }

    @Transactional
    public IngredientCategoryResponse create(IngredientCategoryRequest request) {
        ensureNameAvailable(request.name(), null);

        IngredientCategory ingredientCategory = ingredientCategoryMapper.toEntity(request);
        IngredientCategory saved = executeOrFail(() -> ingredientCategoryRepository.save(ingredientCategory),
                "Falha ao criar categoria de ingrediente no banco de dados");
        return ingredientCategoryMapper.toResponse(saved);
    }

    @Transactional
    public IngredientCategoryResponse update(Long id, IngredientCategoryRequest request) {
        IngredientCategory ingredientCategory = getOrThrow(id);
        ensureNameAvailable(request.name(), id);

        ingredientCategoryMapper.updateEntity(request, ingredientCategory);
        IngredientCategory saved = executeOrFail(() -> ingredientCategoryRepository.save(ingredientCategory),
                "Falha ao atualizar categoria de ingrediente no banco de dados");
        return ingredientCategoryMapper.toResponse(saved);
    }

    @Transactional
    public IngredientCategoryResponse patch(Long id, IngredientCategoryPatchRequest request) {
        IngredientCategory ingredientCategory = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }

        ingredientCategoryMapper.patchEntity(request, ingredientCategory);
        IngredientCategory saved = executeOrFail(() -> ingredientCategoryRepository.save(ingredientCategory),
                "Falha ao atualizar categoria de ingrediente no banco de dados");
        return ingredientCategoryMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        IngredientCategory ingredientCategory = getOrThrow(id);
        executeOrFail(() -> {
            ingredientCategoryRepository.delete(ingredientCategory);
            return null;
        }, "Falha ao remover categoria de ingrediente no banco de dados");
    }

    private IngredientCategory getOrThrow(Long id) {
        Optional<IngredientCategory> ingredientCategory = executeOrFail(() -> ingredientCategoryRepository.findById(id),
                "Falha ao consultar categoria de ingrediente no banco de dados");
        return ingredientCategory.orElseThrow(() -> new ResourceNotFoundException("Categoria de ingrediente nao encontrada com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> ingredientCategoryRepository.findByNameIgnoreCase(name), "Falha ao verificar nome da categoria de ingrediente")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma categoria de ingrediente com o nome " + name);
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
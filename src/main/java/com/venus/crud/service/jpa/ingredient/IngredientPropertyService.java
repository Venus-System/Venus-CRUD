package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientPropertyPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientPropertyRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientProperty;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientPropertyMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientPropertyRepository;
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
public class IngredientPropertyService {

    private static final Logger log = LoggerFactory.getLogger(IngredientPropertyService.class);

    private final IngredientPropertyRepository ingredientPropertyRepository;
    private final IngredientPropertyMapper ingredientPropertyMapper;

    public IngredientPropertyService(IngredientPropertyRepository ingredientPropertyRepository,
            IngredientPropertyMapper ingredientPropertyMapper) {
        this.ingredientPropertyRepository = ingredientPropertyRepository;
        this.ingredientPropertyMapper = ingredientPropertyMapper;
    }

    @Transactional(readOnly = true)
    public List<IngredientPropertyResponse> findAll() {
        return executeOrFail(ingredientPropertyRepository::findAll, "Falha ao consultar propriedades de ingrediente no banco de dados").stream()
                .map(ingredientPropertyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredientPropertyResponse findById(Long id) {
        return ingredientPropertyMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<IngredientPropertyResponse> findByIngredientId(Long ingredientId, String propertyName) {
        List<IngredientProperty> result = StringUtils.hasText(propertyName)
                ? executeOrFail(() -> ingredientPropertyRepository.findByIngredientIdAndPropertyName(ingredientId, propertyName),
                        "Falha ao consultar propriedades do ingrediente por nome")
                : executeOrFail(() -> ingredientPropertyRepository.findByIngredientId(ingredientId),
                        "Falha ao consultar propriedades do ingrediente");

        return result.stream().map(ingredientPropertyMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Slice<IngredientPropertyResponse> search(SourceType sourceType, Pageable pageable) {
        Slice<IngredientProperty> result = sourceType != null
                ? executeOrFail(() -> ingredientPropertyRepository.findBySourceType(sourceType, pageable),
                        "Falha ao consultar propriedades de ingrediente por origem")
                : executeOrFail(() -> ingredientPropertyRepository.findAllBy(pageable), "Falha ao consultar propriedades de ingrediente");

        return result.map(ingredientPropertyMapper::toResponse);
    }

    @Transactional
    public IngredientPropertyResponse create(IngredientPropertyRequest request) {
        IngredientProperty ingredientProperty = ingredientPropertyMapper.toEntity(request);
        IngredientProperty saved = executeOrFail(() -> ingredientPropertyRepository.save(ingredientProperty),
                "Falha ao criar propriedade de ingrediente no banco de dados");
        return ingredientPropertyMapper.toResponse(saved);
    }

    @Transactional
    public IngredientPropertyResponse update(Long id, IngredientPropertyRequest request) {
        IngredientProperty ingredientProperty = getOrThrow(id);
        ingredientPropertyMapper.updateEntity(request, ingredientProperty);

        IngredientProperty saved = executeOrFail(() -> ingredientPropertyRepository.save(ingredientProperty),
                "Falha ao atualizar propriedade de ingrediente no banco de dados");
        return ingredientPropertyMapper.toResponse(saved);
    }

    @Transactional
    public IngredientPropertyResponse patch(Long id, IngredientPropertyPatchRequest request) {
        IngredientProperty ingredientProperty = getOrThrow(id);
        ingredientPropertyMapper.patchEntity(request, ingredientProperty);

        IngredientProperty saved = executeOrFail(() -> ingredientPropertyRepository.save(ingredientProperty),
                "Falha ao atualizar propriedade de ingrediente no banco de dados");
        return ingredientPropertyMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        IngredientProperty ingredientProperty = getOrThrow(id);
        executeOrFail(() -> {
            ingredientPropertyRepository.delete(ingredientProperty);
            return null;
        }, "Falha ao remover propriedade de ingrediente no banco de dados");
    }

    private IngredientProperty getOrThrow(Long id) {
        Optional<IngredientProperty> ingredientProperty = executeOrFail(() -> ingredientPropertyRepository.findById(id),
                "Falha ao consultar propriedade de ingrediente no banco de dados");
        return ingredientProperty.orElseThrow(() -> new ResourceNotFoundException("Propriedade de ingrediente nao encontrada com id " + id));
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
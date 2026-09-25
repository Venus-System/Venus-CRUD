package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.AllergyIngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.AllergyIngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.AllergyIngredientResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.AllergyIngredient;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.ingredient.AllergyIngredientMapper;
import com.venus.crud.repository.jpa.ingredient.AllergyIngredientRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AllergyIngredientService {

    private static final Logger log = LoggerFactory.getLogger(AllergyIngredientService.class);

    private final AllergyIngredientRepository allergyIngredientRepository;
    private final AllergyIngredientMapper allergyIngredientMapper;

    public AllergyIngredientService(AllergyIngredientRepository allergyIngredientRepository, AllergyIngredientMapper allergyIngredientMapper) {
        this.allergyIngredientRepository = allergyIngredientRepository;
        this.allergyIngredientMapper = allergyIngredientMapper;
    }

    @Transactional(readOnly = true)
    public List<AllergyIngredientResponse> findAll() {
        return executeOrFail(allergyIngredientRepository::findAll, "Falha ao consultar ingredientes de alergia no banco de dados").stream()
                .map(allergyIngredientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AllergyIngredientResponse> findByAllergyId(Long allergyId) {
        return executeOrFail(() -> allergyIngredientRepository.findByAllergyId(allergyId), "Falha ao consultar ingredientes da alergia").stream()
                .map(allergyIngredientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<AllergyIngredientResponse> search(Long ingredientId, SourceType sourceType, Pageable pageable) {
        Slice<AllergyIngredient> result;
        if (ingredientId != null) {
            result = executeOrFail(() -> allergyIngredientRepository.findByIngredientId(ingredientId, pageable), "Falha ao consultar alergias por ingrediente");
        } else if (sourceType != null) {
            result = executeOrFail(() -> allergyIngredientRepository.findBySourceType(sourceType, pageable), "Falha ao consultar ingredientes de alergia por origem");
        } else {
            result = executeOrFail(() -> allergyIngredientRepository.findAllBy(pageable), "Falha ao consultar ingredientes de alergia");
        }

        return result.map(allergyIngredientMapper::toResponse);
    }

    @Transactional
    public AllergyIngredientResponse create(AllergyIngredientRequest request) {
        ensureNotAssigned(request.allergyId(), request.ingredientId());

        AllergyIngredient allergyIngredient = allergyIngredientMapper.toEntity(request);
        AllergyIngredient saved = executeOrFail(() -> allergyIngredientRepository.save(allergyIngredient), "Falha ao associar ingrediente a alergia");
        return allergyIngredientMapper.toResponse(saved);
    }

    @Transactional
    public AllergyIngredientResponse patch(Long allergyId, Long ingredientId, AllergyIngredientPatchRequest request) {
        AllergyIngredient allergyIngredient = getOrThrow(allergyId, ingredientId);
        allergyIngredientMapper.patchEntity(request, allergyIngredient);

        AllergyIngredient saved = executeOrFail(() -> allergyIngredientRepository.save(allergyIngredient), "Falha ao atualizar ingrediente da alergia");
        return allergyIngredientMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long allergyId, Long ingredientId) {
        getOrThrow(allergyId, ingredientId);
        executeOrFail(() -> {
            allergyIngredientRepository.deleteByAllergyIdAndIngredientId(allergyId, ingredientId);
            return null;
        }, "Falha ao remover ingrediente da alergia");
    }

    private AllergyIngredient getOrThrow(Long allergyId, Long ingredientId) {
        var allergyIngredient = executeOrFail(() -> allergyIngredientRepository.findByAllergyIdAndIngredientId(allergyId, ingredientId),
                "Falha ao consultar ingrediente da alergia");
        return allergyIngredient.orElseThrow(
                () -> new ResourceNotFoundException("O ingrediente " + ingredientId + " nao esta associado a alergia " + allergyId));
    }

    private void ensureNotAssigned(Long allergyId, Long ingredientId) {
        boolean exists = executeOrFail(() -> allergyIngredientRepository.existsByAllergyIdAndIngredientId(allergyId, ingredientId),
                "Falha ao verificar ingrediente ja associado a alergia");
        if (exists) {
            throw new DuplicateResourceException("O ingrediente " + ingredientId + " ja esta associado a alergia " + allergyId);
        }
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

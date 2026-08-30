package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientEffectPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientEffectRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientEffectResponse;
import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientEffect;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientEffectMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientEffectRepository;
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

@Service
public class IngredientEffectService {

    private static final Logger log = LoggerFactory.getLogger(IngredientEffectService.class);

    private final IngredientEffectRepository ingredientEffectRepository;
    private final IngredientEffectMapper ingredientEffectMapper;

    public IngredientEffectService(IngredientEffectRepository ingredientEffectRepository, IngredientEffectMapper ingredientEffectMapper) {
        this.ingredientEffectRepository = ingredientEffectRepository;
        this.ingredientEffectMapper = ingredientEffectMapper;
    }

    @Transactional(readOnly = true)
    public List<IngredientEffectResponse> findAll() {
        return executeOrFail(ingredientEffectRepository::findAll, "Falha ao consultar efeitos de ingrediente no banco de dados").stream()
                .map(ingredientEffectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredientEffectResponse findById(Long id) {
        return ingredientEffectMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<IngredientEffectResponse> findByIngredientId(Long ingredientId, Long profileTagId) {
        List<IngredientEffect> result = profileTagId != null
                ? executeOrFail(() -> ingredientEffectRepository.findByIngredientIdAndProfileTagId(ingredientId, profileTagId),
                        "Falha ao consultar efeitos do ingrediente por tag de perfil")
                : executeOrFail(() -> ingredientEffectRepository.findByIngredientId(ingredientId), "Falha ao consultar efeitos do ingrediente");

        return result.stream().map(ingredientEffectMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Slice<IngredientEffectResponse> search(Long profileTagId, EffectCategory effectCategory, ReviewStatus reviewStatus,
            SourceType sourceType, Pageable pageable) {
        Slice<IngredientEffect> result;
        if (profileTagId != null) {
            result = executeOrFail(() -> ingredientEffectRepository.findByProfileTagId(profileTagId, pageable),
                    "Falha ao consultar efeitos por tag de perfil");
        } else if (effectCategory != null) {
            result = executeOrFail(() -> ingredientEffectRepository.findByEffectCategory(effectCategory, pageable),
                    "Falha ao consultar efeitos por categoria");
        } else if (reviewStatus != null) {
            result = executeOrFail(() -> ingredientEffectRepository.findByReviewStatus(reviewStatus, pageable),
                    "Falha ao consultar efeitos por status de revisao");
        } else if (sourceType != null) {
            result = executeOrFail(() -> ingredientEffectRepository.findBySourceType(sourceType, pageable),
                    "Falha ao consultar efeitos por origem");
        } else {
            result = executeOrFail(() -> ingredientEffectRepository.findAllBy(pageable), "Falha ao consultar efeitos de ingrediente");
        }

        return result.map(ingredientEffectMapper::toResponse);
    }

    @Transactional
    public IngredientEffectResponse create(IngredientEffectRequest request) {
        IngredientEffect ingredientEffect = ingredientEffectMapper.toEntity(request);
        IngredientEffect saved = executeOrFail(() -> ingredientEffectRepository.save(ingredientEffect),
                "Falha ao criar efeito de ingrediente no banco de dados");
        return ingredientEffectMapper.toResponse(saved);
    }

    @Transactional
    public IngredientEffectResponse update(Long id, IngredientEffectRequest request) {
        IngredientEffect ingredientEffect = getOrThrow(id);
        ingredientEffectMapper.updateEntity(request, ingredientEffect);

        IngredientEffect saved = executeOrFail(() -> ingredientEffectRepository.save(ingredientEffect),
                "Falha ao atualizar efeito de ingrediente no banco de dados");
        return ingredientEffectMapper.toResponse(saved);
    }

    @Transactional
    public IngredientEffectResponse patch(Long id, IngredientEffectPatchRequest request) {
        IngredientEffect ingredientEffect = getOrThrow(id);
        ingredientEffectMapper.patchEntity(request, ingredientEffect);

        IngredientEffect saved = executeOrFail(() -> ingredientEffectRepository.save(ingredientEffect),
                "Falha ao atualizar efeito de ingrediente no banco de dados");
        return ingredientEffectMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        IngredientEffect ingredientEffect = getOrThrow(id);
        executeOrFail(() -> {
            ingredientEffectRepository.delete(ingredientEffect);
            return null;
        }, "Falha ao remover efeito de ingrediente no banco de dados");
    }

    private IngredientEffect getOrThrow(Long id) {
        Optional<IngredientEffect> ingredientEffect = executeOrFail(() -> ingredientEffectRepository.findById(id),
                "Falha ao consultar efeito de ingrediente no banco de dados");
        return ingredientEffect.orElseThrow(() -> new ResourceNotFoundException("Efeito de ingrediente nao encontrado com id " + id));
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
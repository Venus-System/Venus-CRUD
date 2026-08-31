package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientAliasPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientAliasRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientAlias;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientAliasMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientAliasRepository;
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
public class IngredientAliasService {

    private static final Logger log = LoggerFactory.getLogger(IngredientAliasService.class);

    private final IngredientAliasRepository ingredientAliasRepository;
    private final IngredientAliasMapper ingredientAliasMapper;

    public IngredientAliasService(IngredientAliasRepository ingredientAliasRepository, IngredientAliasMapper ingredientAliasMapper) {
        this.ingredientAliasRepository = ingredientAliasRepository;
        this.ingredientAliasMapper = ingredientAliasMapper;
    }

    @Transactional(readOnly = true)
    public List<IngredientAliasResponse> findAll() {
        return executeOrFail(ingredientAliasRepository::findAll, "Falha ao consultar apelidos de ingrediente no banco de dados").stream()
                .map(ingredientAliasMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredientAliasResponse findById(Long id) {
        return ingredientAliasMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public IngredientAliasResponse findByAliasName(String aliasName) {
        return executeOrFail(() -> ingredientAliasRepository.findByAliasNameIgnoreCase(aliasName), "Falha ao consultar apelido de ingrediente por nome")
                .map(ingredientAliasMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Apelido de ingrediente nao encontrado com o nome " + aliasName));
    }

    @Transactional(readOnly = true)
    public List<IngredientAliasResponse> findByIngredientId(Long ingredientId) {
        return executeOrFail(() -> ingredientAliasRepository.findByIngredientId(ingredientId), "Falha ao consultar apelidos do ingrediente").stream()
                .map(ingredientAliasMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<IngredientAliasResponse> search(String aliasLanguage, SourceType sourceType, Pageable pageable) {
        Slice<IngredientAlias> result;
        if (StringUtils.hasText(aliasLanguage)) {
            result = executeOrFail(() -> ingredientAliasRepository.findByAliasLanguage(aliasLanguage, pageable),
                    "Falha ao consultar apelidos de ingrediente por idioma");
        } else if (sourceType != null) {
            result = executeOrFail(() -> ingredientAliasRepository.findBySourceType(sourceType, pageable),
                    "Falha ao consultar apelidos de ingrediente por origem");
        } else {
            result = executeOrFail(() -> ingredientAliasRepository.findAllBy(pageable), "Falha ao consultar apelidos de ingrediente");
        }

        return result.map(ingredientAliasMapper::toResponse);
    }

    @Transactional
    public IngredientAliasResponse create(IngredientAliasRequest request) {
        ensureAliasNameAvailable(request.aliasName(), null);

        IngredientAlias ingredientAlias = ingredientAliasMapper.toEntity(request);
        IngredientAlias saved = executeOrFail(() -> ingredientAliasRepository.save(ingredientAlias),
                "Falha ao criar apelido de ingrediente no banco de dados");
        return ingredientAliasMapper.toResponse(saved);
    }

    @Transactional
    public IngredientAliasResponse update(Long id, IngredientAliasRequest request) {
        IngredientAlias ingredientAlias = getOrThrow(id);
        ensureAliasNameAvailable(request.aliasName(), id);

        ingredientAliasMapper.updateEntity(request, ingredientAlias);
        IngredientAlias saved = executeOrFail(() -> ingredientAliasRepository.save(ingredientAlias),
                "Falha ao atualizar apelido de ingrediente no banco de dados");
        return ingredientAliasMapper.toResponse(saved);
    }

    @Transactional
    public IngredientAliasResponse patch(Long id, IngredientAliasPatchRequest request) {
        IngredientAlias ingredientAlias = getOrThrow(id);
        if (StringUtils.hasText(request.aliasName())) {
            ensureAliasNameAvailable(request.aliasName(), id);
        }

        ingredientAliasMapper.patchEntity(request, ingredientAlias);
        IngredientAlias saved = executeOrFail(() -> ingredientAliasRepository.save(ingredientAlias),
                "Falha ao atualizar apelido de ingrediente no banco de dados");
        return ingredientAliasMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        IngredientAlias ingredientAlias = getOrThrow(id);
        executeOrFail(() -> {
            ingredientAliasRepository.delete(ingredientAlias);
            return null;
        }, "Falha ao remover apelido de ingrediente no banco de dados");
    }

    private IngredientAlias getOrThrow(Long id) {
        Optional<IngredientAlias> ingredientAlias = executeOrFail(() -> ingredientAliasRepository.findById(id),
                "Falha ao consultar apelido de ingrediente no banco de dados");
        return ingredientAlias.orElseThrow(() -> new ResourceNotFoundException("Apelido de ingrediente nao encontrado com id " + id));
    }

    private void ensureAliasNameAvailable(String aliasName, Long excludeId) {
        boolean inUse = executeOrFail(() -> ingredientAliasRepository.findByAliasNameIgnoreCase(aliasName), "Falha ao verificar nome do apelido")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um apelido de ingrediente com o nome " + aliasName);
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
package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.CompatibilityRulePatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.CompatibilityRuleRequest;
import com.venus.crud.dto.jpa.response.ingredient.CompatibilityRuleResponse;
import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.CompatibilityRuleMapper;
import com.venus.crud.repository.jpa.ingredient.CompatibilityRuleRepository;
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
public class CompatibilityRuleService {

    private static final Logger log = LoggerFactory.getLogger(CompatibilityRuleService.class);

    private final CompatibilityRuleRepository compatibilityRuleRepository;
    private final CompatibilityRuleMapper compatibilityRuleMapper;

    public CompatibilityRuleService(CompatibilityRuleRepository compatibilityRuleRepository,
            CompatibilityRuleMapper compatibilityRuleMapper) {
        this.compatibilityRuleRepository = compatibilityRuleRepository;
        this.compatibilityRuleMapper = compatibilityRuleMapper;
    }

    @Transactional(readOnly = true)
    public List<CompatibilityRuleResponse> findAll() {
        return executeOrFail(compatibilityRuleRepository::findAll, "Falha ao consultar regras de compatibilidade no banco de dados").stream()
                .map(compatibilityRuleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompatibilityRuleResponse findById(Long id) {
        return compatibilityRuleMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CompatibilityRuleResponse> findByIngredientEffectId(Long ingredientEffectId) {
        return executeOrFail(() -> compatibilityRuleRepository.findByIngredientEffectId(ingredientEffectId),
                "Falha ao consultar regras de compatibilidade do efeito").stream()
                .map(compatibilityRuleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompatibilityRuleResponse> findEnabledByScoringModelId(Long scoringModelId) {
        return executeOrFail(() -> compatibilityRuleRepository.findByScoringModelIdAndIsEnabledTrueOrderByPriority(scoringModelId),
                "Falha ao consultar regras de compatibilidade ativas do modelo de scoring").stream()
                .map(compatibilityRuleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<CompatibilityRuleResponse> search(EffectType effectType, SourceType sourceType, Pageable pageable) {
        Slice<CompatibilityRule> result;
        if (effectType != null) {
            result = executeOrFail(() -> compatibilityRuleRepository.findByEffectType(effectType, pageable),
                    "Falha ao consultar regras de compatibilidade por tipo de efeito");
        } else if (sourceType != null) {
            result = executeOrFail(() -> compatibilityRuleRepository.findBySourceType(sourceType, pageable),
                    "Falha ao consultar regras de compatibilidade por origem");
        } else {
            result = executeOrFail(() -> compatibilityRuleRepository.findAllBy(pageable), "Falha ao consultar regras de compatibilidade");
        }

        return result.map(compatibilityRuleMapper::toResponse);
    }

    @Transactional
    public CompatibilityRuleResponse create(CompatibilityRuleRequest request) {
        CompatibilityRule compatibilityRule = compatibilityRuleMapper.toEntity(request);
        CompatibilityRule saved = executeOrFail(() -> compatibilityRuleRepository.save(compatibilityRule),
                "Falha ao criar regra de compatibilidade no banco de dados");
        return compatibilityRuleMapper.toResponse(saved);
    }

    @Transactional
    public CompatibilityRuleResponse update(Long id, CompatibilityRuleRequest request) {
        CompatibilityRule compatibilityRule = getOrThrow(id);
        compatibilityRuleMapper.updateEntity(request, compatibilityRule);

        CompatibilityRule saved = executeOrFail(() -> compatibilityRuleRepository.save(compatibilityRule),
                "Falha ao atualizar regra de compatibilidade no banco de dados");
        return compatibilityRuleMapper.toResponse(saved);
    }

    @Transactional
    public CompatibilityRuleResponse patch(Long id, CompatibilityRulePatchRequest request) {
        CompatibilityRule compatibilityRule = getOrThrow(id);
        compatibilityRuleMapper.patchEntity(request, compatibilityRule);

        CompatibilityRule saved = executeOrFail(() -> compatibilityRuleRepository.save(compatibilityRule),
                "Falha ao atualizar regra de compatibilidade no banco de dados");
        return compatibilityRuleMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        CompatibilityRule compatibilityRule = getOrThrow(id);
        executeOrFail(() -> {
            compatibilityRuleRepository.delete(compatibilityRule);
            return null;
        }, "Falha ao remover regra de compatibilidade no banco de dados");
    }

    private CompatibilityRule getOrThrow(Long id) {
        Optional<CompatibilityRule> compatibilityRule = executeOrFail(() -> compatibilityRuleRepository.findById(id),
                "Falha ao consultar regra de compatibilidade no banco de dados");
        return compatibilityRule.orElseThrow(() -> new ResourceNotFoundException("Regra de compatibilidade nao encontrada com id " + id));
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
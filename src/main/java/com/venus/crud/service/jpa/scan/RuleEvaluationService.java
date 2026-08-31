package com.venus.crud.service.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.RuleEvaluationPatchRequest;
import com.venus.crud.dto.jpa.request.scan.RuleEvaluationRequest;
import com.venus.crud.dto.jpa.response.scan.RuleEvaluationResponse;
import com.venus.crud.entity.scan.RuleEvaluation;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scan.RuleEvaluationMapper;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
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
public class RuleEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(RuleEvaluationService.class);

    private final RuleEvaluationRepository ruleEvaluationRepository;
    private final RuleEvaluationMapper ruleEvaluationMapper;

    public RuleEvaluationService(RuleEvaluationRepository ruleEvaluationRepository, RuleEvaluationMapper ruleEvaluationMapper) {
        this.ruleEvaluationRepository = ruleEvaluationRepository;
        this.ruleEvaluationMapper = ruleEvaluationMapper;
    }

    @Transactional(readOnly = true)
    public List<RuleEvaluationResponse> findAll() {
        return executeOrFail(ruleEvaluationRepository::findAll, "Falha ao consultar avaliacoes de regra no banco de dados").stream()
                .map(ruleEvaluationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RuleEvaluationResponse findById(Long id) {
        return ruleEvaluationMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<RuleEvaluationResponse> findByAnalysisResultId(Long analysisResultId, Boolean wasMatched) {
        List<RuleEvaluation> result = Boolean.TRUE.equals(wasMatched)
                ? executeOrFail(() -> ruleEvaluationRepository.findByAnalysisResultIdAndWasMatchedTrue(analysisResultId),
                        "Falha ao consultar avaliacoes de regra aplicadas na analise")
                : executeOrFail(() -> ruleEvaluationRepository.findByAnalysisResultId(analysisResultId),
                        "Falha ao consultar avaliacoes de regra da analise");

        return result.stream().map(ruleEvaluationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Slice<RuleEvaluationResponse> search(Long ingredientId, Long compatibilityRuleId, Pageable pageable) {
        Slice<RuleEvaluation> result;
        if (ingredientId != null) {
            result = executeOrFail(() -> ruleEvaluationRepository.findByIngredientId(ingredientId, pageable),
                    "Falha ao consultar avaliacoes de regra por ingrediente");
        } else if (compatibilityRuleId != null) {
            result = executeOrFail(() -> ruleEvaluationRepository.findByCompatibilityRuleId(compatibilityRuleId, pageable),
                    "Falha ao consultar avaliacoes por regra de compatibilidade");
        } else {
            result = executeOrFail(() -> ruleEvaluationRepository.findAllBy(pageable), "Falha ao consultar avaliacoes de regra");
        }

        return result.map(ruleEvaluationMapper::toResponse);
    }

    @Transactional
    public RuleEvaluationResponse create(RuleEvaluationRequest request) {
        RuleEvaluation ruleEvaluation = ruleEvaluationMapper.toEntity(request);
        RuleEvaluation saved = executeOrFail(() -> ruleEvaluationRepository.save(ruleEvaluation),
                "Falha ao criar avaliacao de regra no banco de dados");
        return ruleEvaluationMapper.toResponse(saved);
    }

    @Transactional
    public RuleEvaluationResponse update(Long id, RuleEvaluationRequest request) {
        RuleEvaluation ruleEvaluation = getOrThrow(id);
        ruleEvaluationMapper.updateEntity(request, ruleEvaluation);

        RuleEvaluation saved = executeOrFail(() -> ruleEvaluationRepository.save(ruleEvaluation),
                "Falha ao atualizar avaliacao de regra no banco de dados");
        return ruleEvaluationMapper.toResponse(saved);
    }

    @Transactional
    public RuleEvaluationResponse patch(Long id, RuleEvaluationPatchRequest request) {
        RuleEvaluation ruleEvaluation = getOrThrow(id);
        ruleEvaluationMapper.patchEntity(request, ruleEvaluation);

        RuleEvaluation saved = executeOrFail(() -> ruleEvaluationRepository.save(ruleEvaluation),
                "Falha ao atualizar avaliacao de regra no banco de dados");
        return ruleEvaluationMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        RuleEvaluation ruleEvaluation = getOrThrow(id);
        executeOrFail(() -> {
            ruleEvaluationRepository.delete(ruleEvaluation);
            return null;
        }, "Falha ao remover avaliacao de regra no banco de dados");
    }

    private RuleEvaluation getOrThrow(Long id) {
        Optional<RuleEvaluation> ruleEvaluation = executeOrFail(() -> ruleEvaluationRepository.findById(id),
                "Falha ao consultar avaliacao de regra no banco de dados");
        return ruleEvaluation.orElseThrow(() -> new ResourceNotFoundException("Avaliacao de regra nao encontrada com id " + id));
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
package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.dto.jpa.response.fullstage.RuleEvaluationDetailResponse;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.mapper.jpa.scan.AnalysisResultMapper;
import com.venus.crud.mapper.jpa.scan.PersonalizedScoreMapper;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
import com.venus.crud.repository.jpa.scan.PersonalizedScoreRepository;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisResultFullService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisResultFullService.class);

    private final AnalysisResultRepository analysisResultRepository;
    private final PersonalizedScoreRepository personalizedScoreRepository;
    private final RuleEvaluationRepository ruleEvaluationRepository;
    private final AnalysisResultMapper analysisResultMapper;
    private final PersonalizedScoreMapper personalizedScoreMapper;
    private final IngredientMapper ingredientMapper;
    private final ProfileTagMapper profileTagMapper;

    public AnalysisResultFullService(AnalysisResultRepository analysisResultRepository,
            PersonalizedScoreRepository personalizedScoreRepository, RuleEvaluationRepository ruleEvaluationRepository,
            AnalysisResultMapper analysisResultMapper, PersonalizedScoreMapper personalizedScoreMapper,
            IngredientMapper ingredientMapper, ProfileTagMapper profileTagMapper) {
        this.analysisResultRepository = analysisResultRepository;
        this.personalizedScoreRepository = personalizedScoreRepository;
        this.ruleEvaluationRepository = ruleEvaluationRepository;
        this.analysisResultMapper = analysisResultMapper;
        this.personalizedScoreMapper = personalizedScoreMapper;
        this.ingredientMapper = ingredientMapper;
        this.profileTagMapper = profileTagMapper;
    }

    @Transactional(readOnly = true)
    public AnalysisResultFullResponse findById(Long id) {
        AnalysisResult analysisResult = executeOrFail(() -> analysisResultRepository.findById(id),
                "Falha ao consultar analise no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Analise nao encontrada com id " + id));

        PersonalizedScoreResponse personalizedScore = executeOrFail(() -> personalizedScoreRepository.findByAnalysisResultId(id),
                "Falha ao consultar score personalizado da analise")
                .map(personalizedScoreMapper::toResponse)
                .orElse(null);

        var ruleEvaluations = executeOrFail(() -> ruleEvaluationRepository.findByAnalysisResultId(id),
                "Falha ao consultar avaliacoes de regra da analise")
                .stream()
                .map(evaluation -> new RuleEvaluationDetailResponse(
                        evaluation.getId(),
                        evaluation.getCompatibilityRule().getId(),
                        ingredientMapper.toResponse(evaluation.getIngredient()),
                        profileTagMapper.toResponse(evaluation.getProfileTag()),
                        evaluation.getWasMatched(),
                        evaluation.getScoreDelta(),
                        evaluation.getFinalDelta(),
                        evaluation.getExplanation()))
                .toList();

        return new AnalysisResultFullResponse(analysisResultMapper.toResponse(analysisResult), personalizedScore, ruleEvaluations);
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
package com.venus.crud.service.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.PersonalizedScorePatchRequest;
import com.venus.crud.dto.jpa.request.scan.PersonalizedScoreRequest;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.scan.PersonalizedScore;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scan.PersonalizedScoreMapper;
import com.venus.crud.repository.jpa.scan.PersonalizedScoreRepository;
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
public class PersonalizedScoreService {

    private static final Logger log = LoggerFactory.getLogger(PersonalizedScoreService.class);

    private final PersonalizedScoreRepository personalizedScoreRepository;
    private final PersonalizedScoreMapper personalizedScoreMapper;

    public PersonalizedScoreService(PersonalizedScoreRepository personalizedScoreRepository,
            PersonalizedScoreMapper personalizedScoreMapper) {
        this.personalizedScoreRepository = personalizedScoreRepository;
        this.personalizedScoreMapper = personalizedScoreMapper;
    }

    @Transactional(readOnly = true)
    public List<PersonalizedScoreResponse> findAll() {
        return executeOrFail(personalizedScoreRepository::findAll, "Falha ao consultar scores personalizados no banco de dados").stream()
                .map(personalizedScoreMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonalizedScoreResponse findByAnalysisResultId(Long analysisResultId) {
        return personalizedScoreMapper.toResponse(getOrThrow(analysisResultId));
    }

    @Transactional(readOnly = true)
    public Slice<PersonalizedScoreResponse> findByUserId(Long userId, Long productVersionId, RiskLevel riskLevel,
            RecommendationLevel recommendationLevel, Pageable pageable) {
        Slice<PersonalizedScore> result;
        if (productVersionId != null) {
            result = executeOrFail(() -> personalizedScoreRepository.findByUserIdAndProductVersionId(userId, productVersionId, pageable),
                    "Falha ao consultar scores personalizados do usuario para o produto");
        } else if (riskLevel != null) {
            result = executeOrFail(() -> personalizedScoreRepository.findByUserIdAndRiskLevel(userId, riskLevel, pageable),
                    "Falha ao consultar scores personalizados do usuario por nivel de risco");
        } else if (recommendationLevel != null) {
            result = executeOrFail(() -> personalizedScoreRepository.findByUserIdAndRecommendationLevel(userId, recommendationLevel, pageable),
                    "Falha ao consultar scores personalizados do usuario por nivel de recomendacao");
        } else {
            result = executeOrFail(() -> personalizedScoreRepository.findByUserId(userId, pageable),
                    "Falha ao consultar scores personalizados do usuario");
        }

        return result.map(personalizedScoreMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<PersonalizedScoreResponse> search(Pageable pageable) {
        return executeOrFail(() -> personalizedScoreRepository.findAllBy(pageable), "Falha ao consultar scores personalizados")
                .map(personalizedScoreMapper::toResponse);
    }

    @Transactional
    public PersonalizedScoreResponse create(PersonalizedScoreRequest request) {
        ensureScoreNotCalculated(request.analysisResultId());

        PersonalizedScore personalizedScore = personalizedScoreMapper.toEntity(request);
        PersonalizedScore saved = executeOrFail(() -> personalizedScoreRepository.save(personalizedScore),
                "Falha ao criar score personalizado no banco de dados");
        return personalizedScoreMapper.toResponse(saved);
    }

    @Transactional
    public PersonalizedScoreResponse update(Long analysisResultId, PersonalizedScoreRequest request) {
        PersonalizedScore personalizedScore = getOrThrow(analysisResultId);
        personalizedScoreMapper.updateEntity(request, personalizedScore);

        PersonalizedScore saved = executeOrFail(() -> personalizedScoreRepository.save(personalizedScore),
                "Falha ao atualizar score personalizado no banco de dados");
        return personalizedScoreMapper.toResponse(saved);
    }

    @Transactional
    public PersonalizedScoreResponse patch(Long analysisResultId, PersonalizedScorePatchRequest request) {
        PersonalizedScore personalizedScore = getOrThrow(analysisResultId);
        personalizedScoreMapper.patchEntity(request, personalizedScore);

        PersonalizedScore saved = executeOrFail(() -> personalizedScoreRepository.save(personalizedScore),
                "Falha ao atualizar score personalizado no banco de dados");
        return personalizedScoreMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long analysisResultId) {
        getOrThrow(analysisResultId);
        executeOrFail(() -> {
            personalizedScoreRepository.deleteByAnalysisResultId(analysisResultId);
            return null;
        }, "Falha ao remover score personalizado no banco de dados");
    }

    private PersonalizedScore getOrThrow(Long analysisResultId) {
        var personalizedScore = executeOrFail(() -> personalizedScoreRepository.findByAnalysisResultId(analysisResultId),
                "Falha ao consultar score personalizado no banco de dados");
        return personalizedScore.orElseThrow(
                () -> new ResourceNotFoundException("Score personalizado nao encontrado para a analise com id " + analysisResultId));
    }

    private void ensureScoreNotCalculated(Long analysisResultId) {
        boolean exists = executeOrFail(() -> personalizedScoreRepository.existsByAnalysisResultId(analysisResultId),
                "Falha ao verificar score personalizado existente");
        if (exists) {
            throw new DuplicateResourceException("A analise " + analysisResultId + " ja tem score personalizado calculado");
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
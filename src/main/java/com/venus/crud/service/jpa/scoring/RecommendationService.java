package com.venus.crud.service.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.RecommendationPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.RecommendationRequest;
import com.venus.crud.dto.jpa.response.scoring.RecommendationResponse;
import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.entity.scoring.Recommendation;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.RecommendationMapper;
import com.venus.crud.repository.jpa.scoring.RecommendationRepository;
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
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationService(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> findAll() {
        return executeOrFail(recommendationRepository::findAll, "Falha ao consultar recomendacoes no banco de dados").stream()
                .map(recommendationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponse findById(Long id) {
        return recommendationMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> findByUserId(Long userId) {
        return executeOrFail(() -> recommendationRepository.findByUserIdOrderByRankingPosition(userId),
                "Falha ao consultar recomendacoes do usuario").stream()
                .map(recommendationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> findByAnalysisResultId(Long analysisResultId) {
        return executeOrFail(() -> recommendationRepository.findByAnalysisResultId(analysisResultId),
                "Falha ao consultar recomendacoes da analise").stream()
                .map(recommendationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<RecommendationResponse> search(Long userId, RecommendationType recommendationType, Long productVersionId, Pageable pageable) {
        Slice<Recommendation> result;
        if (userId != null && recommendationType != null) {
            result = executeOrFail(() -> recommendationRepository.findByUserIdAndRecommendationType(userId, recommendationType, pageable),
                    "Falha ao consultar recomendacoes do usuario por tipo");
        } else if (productVersionId != null) {
            result = executeOrFail(() -> recommendationRepository.findByProductVersionId(productVersionId, pageable),
                    "Falha ao consultar recomendacoes da versao de produto");
        } else {
            result = executeOrFail(() -> recommendationRepository.findAllBy(pageable), "Falha ao consultar recomendacoes");
        }

        return result.map(recommendationMapper::toResponse);
    }

    @Transactional
    public RecommendationResponse create(RecommendationRequest request) {
        Recommendation recommendation = recommendationMapper.toEntity(request);
        Recommendation saved = executeOrFail(() -> recommendationRepository.save(recommendation),
                "Falha ao criar recomendacao no banco de dados");
        return recommendationMapper.toResponse(saved);
    }

    @Transactional
    public RecommendationResponse update(Long id, RecommendationRequest request) {
        Recommendation recommendation = getOrThrow(id);
        recommendationMapper.updateEntity(request, recommendation);

        Recommendation saved = executeOrFail(() -> recommendationRepository.save(recommendation),
                "Falha ao atualizar recomendacao no banco de dados");
        return recommendationMapper.toResponse(saved);
    }

    @Transactional
    public RecommendationResponse patch(Long id, RecommendationPatchRequest request) {
        Recommendation recommendation = getOrThrow(id);
        recommendationMapper.patchEntity(request, recommendation);

        Recommendation saved = executeOrFail(() -> recommendationRepository.save(recommendation),
                "Falha ao atualizar recomendacao no banco de dados");
        return recommendationMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Recommendation recommendation = getOrThrow(id);
        executeOrFail(() -> {
            recommendationRepository.delete(recommendation);
            return null;
        }, "Falha ao remover recomendacao no banco de dados");
    }

    private Recommendation getOrThrow(Long id) {
        Optional<Recommendation> recommendation = executeOrFail(() -> recommendationRepository.findById(id),
                "Falha ao consultar recomendacao no banco de dados");
        return recommendation.orElseThrow(() -> new ResourceNotFoundException("Recomendacao nao encontrada com id " + id));
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
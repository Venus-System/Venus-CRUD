package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductScoreFullResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import com.venus.crud.entity.scoring.ProductScore;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ProductScoreMapper;
import com.venus.crud.mapper.jpa.scoring.ScoreCategoryMapper;
import com.venus.crud.mapper.jpa.scoring.ScoringModelMapper;
import com.venus.crud.repository.jpa.scoring.ProductScoreRepository;
import com.venus.crud.repository.jpa.scoring.ScoreCategoryRepository;
import com.venus.crud.repository.jpa.scoring.ScoringModelRepository;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductScoreFullService {

    private static final Logger log = LoggerFactory.getLogger(ProductScoreFullService.class);

    private final ProductScoreRepository productScoreRepository;
    private final ScoringModelRepository scoringModelRepository;
    private final ScoreCategoryRepository scoreCategoryRepository;
    private final ProductScoreMapper productScoreMapper;
    private final ScoringModelMapper scoringModelMapper;
    private final ScoreCategoryMapper scoreCategoryMapper;

    public ProductScoreFullService(ProductScoreRepository productScoreRepository, ScoringModelRepository scoringModelRepository,
            ScoreCategoryRepository scoreCategoryRepository, ProductScoreMapper productScoreMapper,
            ScoringModelMapper scoringModelMapper, ScoreCategoryMapper scoreCategoryMapper) {
        this.productScoreRepository = productScoreRepository;
        this.scoringModelRepository = scoringModelRepository;
        this.scoreCategoryRepository = scoreCategoryRepository;
        this.productScoreMapper = productScoreMapper;
        this.scoringModelMapper = scoringModelMapper;
        this.scoreCategoryMapper = scoreCategoryMapper;
    }

    @Transactional(readOnly = true)
    public ProductScoreFullResponse findByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId) {
        ProductScore productScore = executeOrFail(
                () -> productScoreRepository.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId),
                "Falha ao consultar score de produto no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum score encontrado para a versao de produto " + productVersionId + " no modelo de scoring " + scoringModelId));

        ScoringModelResponse scoringModel = executeOrFail(() -> scoringModelRepository.findById(scoringModelId),
                "Falha ao consultar modelo de scoring do score")
                .map(scoringModelMapper::toResponse)
                .orElse(null);

        var categories = executeOrFail(scoreCategoryRepository::findAll, "Falha ao consultar categorias de score").stream()
                .map(scoreCategoryMapper::toResponse)
                .toList();

        return new ProductScoreFullResponse(productScoreMapper.toResponse(productScore), scoringModel, categories);
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
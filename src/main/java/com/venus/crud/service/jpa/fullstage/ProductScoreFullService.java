package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductScoreFullResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import com.venus.crud.entity.scoring.ProductScore;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ProductScoreMapper;
import com.venus.crud.mapper.jpa.scoring.ScoringModelCategoryMapper;
import com.venus.crud.mapper.jpa.scoring.ScoringModelMapper;
import com.venus.crud.repository.jpa.scoring.ProductScoreRepository;
import com.venus.crud.repository.jpa.scoring.ScoringModelCategoryRepository;
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
    private final ScoringModelCategoryRepository scoringModelCategoryRepository;
    private final ProductScoreMapper productScoreMapper;
    private final ScoringModelMapper scoringModelMapper;
    private final ScoringModelCategoryMapper scoringModelCategoryMapper;

    public ProductScoreFullService(ProductScoreRepository productScoreRepository, ScoringModelRepository scoringModelRepository,
            ScoringModelCategoryRepository scoringModelCategoryRepository, ProductScoreMapper productScoreMapper,
            ScoringModelMapper scoringModelMapper, ScoringModelCategoryMapper scoringModelCategoryMapper) {
        this.productScoreRepository = productScoreRepository;
        this.scoringModelRepository = scoringModelRepository;
        this.scoringModelCategoryRepository = scoringModelCategoryRepository;
        this.productScoreMapper = productScoreMapper;
        this.scoringModelMapper = scoringModelMapper;
        this.scoringModelCategoryMapper = scoringModelCategoryMapper;
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

        var categories = executeOrFail(() -> scoringModelCategoryRepository.findByScoringModelId(scoringModelId),
                "Falha ao consultar categorias do modelo de scoring").stream()
                .map(scoringModelCategoryMapper::toResponse)
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
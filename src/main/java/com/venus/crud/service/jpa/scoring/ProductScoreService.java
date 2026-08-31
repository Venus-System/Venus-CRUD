package com.venus.crud.service.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ProductScorePatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ProductScoreRequest;
import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import com.venus.crud.entity.scoring.ProductScore;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ProductScoreMapper;
import com.venus.crud.repository.jpa.scoring.ProductScoreRepository;
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
public class ProductScoreService {

    private static final Logger log = LoggerFactory.getLogger(ProductScoreService.class);

    private final ProductScoreRepository productScoreRepository;
    private final ProductScoreMapper productScoreMapper;

    public ProductScoreService(ProductScoreRepository productScoreRepository, ProductScoreMapper productScoreMapper) {
        this.productScoreRepository = productScoreRepository;
        this.productScoreMapper = productScoreMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductScoreResponse> findAll() {
        return executeOrFail(productScoreRepository::findAll, "Falha ao consultar scores de produto no banco de dados").stream()
                .map(productScoreMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductScoreResponse findByProductVersionIdAndScoringModelId(Long productVersionId, Long scoringModelId) {
        return productScoreMapper.toResponse(getOrThrow(productVersionId, scoringModelId));
    }

    @Transactional(readOnly = true)
    public Slice<ProductScoreResponse> findByProductVersionId(Long productVersionId, Pageable pageable) {
        return executeOrFail(() -> productScoreRepository.findByProductVersionId(productVersionId, pageable),
                "Falha ao consultar scores da versao de produto")
                .map(productScoreMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<ProductScoreResponse> search(Integer minOverallScore, Pageable pageable) {
        Slice<ProductScore> result = minOverallScore != null
                ? executeOrFail(() -> productScoreRepository.findByOverallScoreGreaterThanEqual(minOverallScore, pageable),
                        "Falha ao consultar scores de produto por nota minima")
                : executeOrFail(() -> productScoreRepository.findAllBy(pageable), "Falha ao consultar scores de produto");

        return result.map(productScoreMapper::toResponse);
    }

    @Transactional
    public ProductScoreResponse create(ProductScoreRequest request) {
        ensureScoreNotCalculated(request.productVersionId(), request.scoringModelId());

        ProductScore productScore = productScoreMapper.toEntity(request);
        ProductScore saved = executeOrFail(() -> productScoreRepository.save(productScore),
                "Falha ao criar score de produto no banco de dados");
        return productScoreMapper.toResponse(saved);
    }

    @Transactional
    public ProductScoreResponse patch(Long productVersionId, Long scoringModelId, ProductScorePatchRequest request) {
        ProductScore productScore = getOrThrow(productVersionId, scoringModelId);
        productScoreMapper.patchEntity(request, productScore);

        ProductScore saved = executeOrFail(() -> productScoreRepository.save(productScore),
                "Falha ao atualizar score de produto no banco de dados");
        return productScoreMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long productVersionId, Long scoringModelId) {
        getOrThrow(productVersionId, scoringModelId);
        executeOrFail(() -> {
            productScoreRepository.deleteByProductVersionIdAndScoringModelId(productVersionId, scoringModelId);
            return null;
        }, "Falha ao remover score de produto no banco de dados");
    }

    private ProductScore getOrThrow(Long productVersionId, Long scoringModelId) {
        var productScore = executeOrFail(
                () -> productScoreRepository.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId),
                "Falha ao consultar score de produto no banco de dados");
        return productScore.orElseThrow(() -> new ResourceNotFoundException(
                "Nenhum score encontrado para a versao de produto " + productVersionId + " no modelo de scoring " + scoringModelId));
    }

    private void ensureScoreNotCalculated(Long productVersionId, Long scoringModelId) {
        boolean exists = executeOrFail(
                () -> productScoreRepository.existsByProductVersionIdAndScoringModelId(productVersionId, scoringModelId),
                "Falha ao verificar score ja calculado");
        if (exists) {
            throw new DuplicateResourceException(
                    "A versao de produto " + productVersionId + " ja tem score calculado no modelo de scoring " + scoringModelId);
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
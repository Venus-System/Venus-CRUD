package com.venus.crud.service.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoringModelCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelCategoryResponse;
import com.venus.crud.entity.scoring.ScoringModelCategory;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ScoringModelCategoryMapper;
import com.venus.crud.repository.jpa.scoring.ScoringModelCategoryRepository;
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
public class ScoringModelCategoryService {

    private static final Logger log = LoggerFactory.getLogger(ScoringModelCategoryService.class);

    private final ScoringModelCategoryRepository scoringModelCategoryRepository;
    private final ScoringModelCategoryMapper scoringModelCategoryMapper;

    public ScoringModelCategoryService(ScoringModelCategoryRepository scoringModelCategoryRepository,
            ScoringModelCategoryMapper scoringModelCategoryMapper) {
        this.scoringModelCategoryRepository = scoringModelCategoryRepository;
        this.scoringModelCategoryMapper = scoringModelCategoryMapper;
    }

    @Transactional(readOnly = true)
    public List<ScoringModelCategoryResponse> findAll() {
        return executeOrFail(scoringModelCategoryRepository::findAll, "Falha ao consultar categorias dos modelos de scoring no banco de dados").stream()
                .map(scoringModelCategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScoringModelCategoryResponse> findByScoringModelId(Long scoringModelId) {
        return executeOrFail(() -> scoringModelCategoryRepository.findByScoringModelId(scoringModelId),
                "Falha ao consultar categorias do modelo de scoring").stream()
                .map(scoringModelCategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<ScoringModelCategoryResponse> search(Long scoreCategoryId, Pageable pageable) {
        Slice<ScoringModelCategory> result;
        if (scoreCategoryId != null) {
            result = executeOrFail(() -> scoringModelCategoryRepository.findByScoreCategoryId(scoreCategoryId, pageable),
                    "Falha ao consultar modelos de scoring por categoria");
        } else {
            result = executeOrFail(() -> scoringModelCategoryRepository.findAllBy(pageable),
                    "Falha ao consultar categorias dos modelos de scoring");
        }

        return result.map(scoringModelCategoryMapper::toResponse);
    }

    @Transactional
    public ScoringModelCategoryResponse create(ScoringModelCategoryRequest request) {
        ensureNotAssigned(request.scoringModelId(), request.scoreCategoryId());

        ScoringModelCategory scoringModelCategory = scoringModelCategoryMapper.toEntity(request);
        ScoringModelCategory saved = executeOrFail(() -> scoringModelCategoryRepository.save(scoringModelCategory),
                "Falha ao associar categoria ao modelo de scoring");
        return scoringModelCategoryMapper.toResponse(saved);
    }

    @Transactional
    public ScoringModelCategoryResponse patch(Long scoringModelId, Long scoreCategoryId, ScoringModelCategoryPatchRequest request) {
        ScoringModelCategory scoringModelCategory = getOrThrow(scoringModelId, scoreCategoryId);
        scoringModelCategoryMapper.patchEntity(request, scoringModelCategory);

        ScoringModelCategory saved = executeOrFail(() -> scoringModelCategoryRepository.save(scoringModelCategory),
                "Falha ao atualizar categoria do modelo de scoring");
        return scoringModelCategoryMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long scoringModelId, Long scoreCategoryId) {
        getOrThrow(scoringModelId, scoreCategoryId);
        executeOrFail(() -> {
            scoringModelCategoryRepository.deleteByScoringModelIdAndScoreCategoryId(scoringModelId, scoreCategoryId);
            return null;
        }, "Falha ao remover categoria do modelo de scoring");
    }

    private ScoringModelCategory getOrThrow(Long scoringModelId, Long scoreCategoryId) {
        var scoringModelCategory = executeOrFail(
                () -> scoringModelCategoryRepository.findByScoringModelIdAndScoreCategoryId(scoringModelId, scoreCategoryId),
                "Falha ao consultar categoria do modelo de scoring");
        return scoringModelCategory.orElseThrow(() -> new ResourceNotFoundException(
                "A categoria " + scoreCategoryId + " nao esta associada ao modelo de scoring " + scoringModelId));
    }

    private void ensureNotAssigned(Long scoringModelId, Long scoreCategoryId) {
        boolean exists = executeOrFail(
                () -> scoringModelCategoryRepository.existsByScoringModelIdAndScoreCategoryId(scoringModelId, scoreCategoryId),
                "Falha ao verificar categoria ja associada ao modelo de scoring");
        if (exists) {
            throw new DuplicateResourceException(
                    "A categoria " + scoreCategoryId + " ja esta associada ao modelo de scoring " + scoringModelId);
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

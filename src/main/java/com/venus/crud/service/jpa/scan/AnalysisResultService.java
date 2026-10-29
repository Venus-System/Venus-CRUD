package com.venus.crud.service.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.AnalysisResultPatchRequest;
import com.venus.crud.dto.jpa.request.scan.AnalysisResultRequest;
import com.venus.crud.dto.jpa.response.scan.AnalysisResultResponse;
import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.InvalidStateTransitionDetector;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.scan.AnalysisResultMapper;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
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
public class AnalysisResultService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisResultService.class);

    private static final String INVALID_TRANSITION_MESSAGE =
            "Transicao de status invalida. A analise vai de PROCESSING para COMPLETED, FAILED ou PENDING_REVIEW, "
                    + "e de PENDING_REVIEW para COMPLETED ou FAILED.";

    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisResultMapper analysisResultMapper;

    public AnalysisResultService(AnalysisResultRepository analysisResultRepository, AnalysisResultMapper analysisResultMapper) {
        this.analysisResultRepository = analysisResultRepository;
        this.analysisResultMapper = analysisResultMapper;
    }

    @Transactional(readOnly = true)
    public List<AnalysisResultResponse> findAll() {
        return executeOrFail(analysisResultRepository::findAll, "Falha ao consultar analises no banco de dados").stream()
                .map(analysisResultMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnalysisResultResponse findById(Long id) {
        return analysisResultMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<AnalysisResultResponse> findByUserId(Long userId, Long productVersionId, Pageable pageable) {
        Slice<AnalysisResult> result = productVersionId != null
                ? executeOrFail(() -> analysisResultRepository.findByUserIdAndProductVersionId(userId, productVersionId, pageable),
                        "Falha ao consultar analises do usuario para o produto")
                : executeOrFail(() -> analysisResultRepository.findByUserId(userId, pageable), "Falha ao consultar analises do usuario");

        return result.map(analysisResultMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<AnalysisResultResponse> search(AnalysisStatus status, Integer minOverallScore, Pageable pageable) {
        Slice<AnalysisResult> analysisResults = executeOrFail(
                () -> analysisResultRepository.search(status, minOverallScore, pageable),
                "Falha ao consultar analises");

        return analysisResults.map(analysisResultMapper::toResponse);
    }

    @Transactional
    public AnalysisResultResponse create(AnalysisResultRequest request) {
        AnalysisResult analysisResult = analysisResultMapper.toEntity(request);
        AnalysisResult saved = executeOrFail(() -> analysisResultRepository.save(analysisResult), "Falha ao criar analise no banco de dados");
        return analysisResultMapper.toResponse(saved);
    }

    @Transactional
    public AnalysisResultResponse update(Long id, AnalysisResultRequest request) {
        AnalysisResult analysisResult = getOrThrow(id);
        analysisResultMapper.updateEntity(request, analysisResult);

        AnalysisResult saved = executeOrFail(() -> analysisResultRepository.saveAndFlush(analysisResult), "Falha ao atualizar analise no banco de dados");
        return analysisResultMapper.toResponse(saved);
    }

    @Transactional
    public AnalysisResultResponse patch(Long id, AnalysisResultPatchRequest request) {
        AnalysisResult analysisResult = getOrThrow(id);
        analysisResultMapper.patchEntity(request, analysisResult);

        AnalysisResult saved = executeOrFail(() -> analysisResultRepository.saveAndFlush(analysisResult), "Falha ao atualizar analise no banco de dados");
        return analysisResultMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        AnalysisResult analysisResult = getOrThrow(id);
        executeOrFail(() -> {
            analysisResultRepository.delete(analysisResult);
            return null;
        }, "Falha ao remover analise no banco de dados");
    }

    private AnalysisResult getOrThrow(Long id) {
        Optional<AnalysisResult> analysisResult = executeOrFail(() -> analysisResultRepository.findById(id),
                "Falha ao consultar analise no banco de dados");
        return analysisResult.orElseThrow(() -> new ResourceNotFoundException("Analise nao encontrada com id " + id));
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            if (InvalidStateTransitionDetector.matches(ex)) {
                throw new InvalidStateTransitionException(INVALID_TRANSITION_MESSAGE);
            }
            if (ex instanceof DataIntegrityViolationException integrityViolation) {
                throw DataIntegrityViolationTranslator.translate(integrityViolation);
            }
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}
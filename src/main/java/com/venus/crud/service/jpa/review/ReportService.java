package com.venus.crud.service.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReportPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReportRequest;
import com.venus.crud.dto.jpa.response.review.ReportResponse;
import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.entity.review.Report;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.review.ReportMapper;
import com.venus.crud.repository.jpa.review.ReportRepository;
import java.sql.SQLException;
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
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private static final String INVALID_STATE_TRANSITION_SQL_STATE = "VE001";

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> findAll() {
        return executeOrFail(reportRepository::findAll, "Falha ao consultar denuncias no banco de dados").stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse findById(Long id) {
        return reportMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ReportResponse> search(Long userId, ReportStatus status, ReportTargetType targetType, Long targetId,
            Long adminUserId, Pageable pageable) {
        Slice<Report> result;
        if (targetType != null && targetId != null) {
            result = executeOrFail(() -> reportRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable),
                    "Falha ao consultar denuncias do alvo");
        } else if (userId != null) {
            result = executeOrFail(() -> reportRepository.findByUserId(userId, pageable), "Falha ao consultar denuncias do usuario");
        } else if (status != null) {
            result = executeOrFail(() -> reportRepository.findByStatus(status, pageable), "Falha ao consultar denuncias por status");
        } else if (adminUserId != null) {
            result = executeOrFail(() -> reportRepository.findByAdminUserId(adminUserId, pageable),
                    "Falha ao consultar denuncias do administrador");
        } else {
            result = executeOrFail(() -> reportRepository.findAllBy(pageable), "Falha ao consultar denuncias");
        }

        return result.map(reportMapper::toResponse);
    }

    @Transactional
    public ReportResponse create(ReportRequest request) {
        Report report = reportMapper.toEntity(request);
        Report saved = executeOrFail(() -> reportRepository.save(report), "Falha ao criar denuncia no banco de dados");
        return reportMapper.toResponse(saved);
    }

    @Transactional
    public ReportResponse update(Long id, ReportRequest request) {
        Report report = getOrThrow(id);
        reportMapper.updateEntity(request, report);

        Report saved = executeOrFail(() -> reportRepository.save(report), "Falha ao atualizar denuncia no banco de dados");
        return reportMapper.toResponse(saved);
    }

    @Transactional
    public ReportResponse patch(Long id, ReportPatchRequest request) {
        Report report = getOrThrow(id);
        reportMapper.patchEntity(request, report);

        Report saved = executeOrFail(() -> reportRepository.save(report), "Falha ao atualizar denuncia no banco de dados");
        return reportMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Report report = getOrThrow(id);
        executeOrFail(() -> {
            reportRepository.delete(report);
            return null;
        }, "Falha ao remover denuncia no banco de dados");
    }

    private Report getOrThrow(Long id) {
        Optional<Report> report = executeOrFail(() -> reportRepository.findById(id), "Falha ao consultar denuncia no banco de dados");
        return report.orElseThrow(() -> new ResourceNotFoundException("Denuncia nao encontrada com id " + id));
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            if (isInvalidStateTransition(ex)) {
                throw new InvalidStateTransitionException(
                        "Transicao de status invalida. Denuncias resolvidas ou rejeitadas nao mudam de status.");
            }
            if (ex instanceof DataIntegrityViolationException) {
                log.warn("Violacao de integridade de dados: {}", ex.getMessage());
                throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
            }
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }

    private boolean isInvalidStateTransition(DataAccessException ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException
                    && INVALID_STATE_TRANSITION_SQL_STATE.equals(sqlException.getSQLState())) {
                log.warn("Transicao de status rejeitada pelo banco: {}", sqlException.getMessage());
                return true;
            }
        }
        return false;
    }
}
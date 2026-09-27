package com.venus.crud.service.mongo;

import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanIngredientDecision;
import com.venus.crud.document.ScanReview;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSync;
import com.venus.crud.dto.mongo.request.ScanApproveRequest;
import com.venus.crud.dto.mongo.request.ScanIngredientDecisionRequest;
import com.venus.crud.dto.mongo.request.ScanRejectRequest;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataConstraintException;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import com.venus.crud.security.CurrentUserProvider;
import com.venus.crud.security.SecurityErrorHandler;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ScanReviewService {

    private static final Logger log = LoggerFactory.getLogger(ScanReviewService.class);

    private static final String CONCURRENT_CHANGE_MESSAGE = "O scan foi alterado por outra pessoa. Recarregue e tente de novo.";

    private final ScanSessionService scanSessionService;
    private final ScanSessionRepository scanSessionRepository;
    private final ScanReviewValidator scanReviewValidator;
    private final ScanCatalogSync scanCatalogSync;
    private final CurrentUserProvider currentUserProvider;

    public ScanReviewService(ScanSessionService scanSessionService, ScanSessionRepository scanSessionRepository,
            ScanReviewValidator scanReviewValidator, ScanCatalogSync scanCatalogSync, CurrentUserProvider currentUserProvider) {
        this.scanSessionService = scanSessionService;
        this.scanSessionRepository = scanSessionRepository;
        this.scanReviewValidator = scanReviewValidator;
        this.scanCatalogSync = scanCatalogSync;
        this.currentUserProvider = currentUserProvider;
    }

    public ScanSessionResponse approve(String id, ScanApproveRequest request) {
        ScanSession scanSession = scanSessionService.getOrThrow(id);
        requirePendingReview(scanSession, "aprovar");
        Long adminUserId = currentAdminUserId();
        ScanApprovedSnapshot snapshot = scanReviewValidator.validate(scanSession, request, adminUserId);

        scanSession.setStatus(ScanStatus.APPROVED);
        scanSession.setReview(review(adminUserId, request.reason(), decisionsOf(request.ingredients())));
        scanSession.setApprovedSnapshot(snapshot);
        ScanSession approved = save(scanSession);

        return scanSessionService.toResponse(synchronize(approved));
    }

    public ScanSessionResponse reject(String id, ScanRejectRequest request) {
        ScanSession scanSession = scanSessionService.getOrThrow(id);
        requirePendingReview(scanSession, "recusar");
        Long adminUserId = currentAdminUserId();
        scanReviewValidator.validateReviewer(adminUserId);

        scanSession.setStatus(ScanStatus.REJECTED);
        scanSession.setReview(review(adminUserId, request.reason(), List.of()));
        return scanSessionService.toResponse(save(scanSession));
    }

    public ScanSessionResponse sync(String id) {
        ScanSession scanSession = scanSessionService.getOrThrow(id);
        if (scanSession.getStatus() != ScanStatus.APPROVED && scanSession.getStatus() != ScanStatus.SYNC_FAILED) {
            throw new InvalidStateTransitionException(
                    "So da para sincronizar scan APPROVED ou SYNC_FAILED. Status atual: " + scanSession.getStatus());
        }
        return scanSessionService.toResponse(synchronize(scanSession));
    }

    private Long currentAdminUserId() {
        return currentUserProvider.adminUserId()
                .orElseThrow(() -> new AccessDeniedException(SecurityErrorHandler.ACCESS_DENIED_MESSAGE));
    }

    private void requirePendingReview(ScanSession scanSession, String action) {
        if (scanSession.getStatus() != ScanStatus.PENDING_REVIEW) {
            throw new InvalidStateTransitionException(
                    "So da para " + action + " scan em PENDING_REVIEW. Status atual: " + scanSession.getStatus());
        }
    }

    private ScanSession synchronize(ScanSession scanSession) {
        ScanSync sync = scanSession.getSync() != null ? scanSession.getSync() : new ScanSync();
        sync.setAttempts(sync.getAttempts() == null ? 1 : sync.getAttempts() + 1);
        try {
            ScanCatalogSync.Result result = scanCatalogSync.synchronize(scanSession);
            sync.setProductId(result.productId());
            sync.setProductVersionId(result.productVersionId());
            sync.setSyncedAt(OffsetDateTime.now());
            sync.setLastError(null);
            sync.setFailedAt(null);
            scanSession.setStatus(ScanStatus.SYNCED);
        } catch (RuntimeException ex) {
            log.error("Falha ao sincronizar o scan {} com o catalogo", scanSession.getScanId(), ex);
            sync.setLastError(describe(ex));
            sync.setFailedAt(OffsetDateTime.now());
            scanSession.setStatus(ScanStatus.SYNC_FAILED);
        }
        scanSession.setSync(sync);
        return save(scanSession);
    }

    private String describe(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        if (ex instanceof DataConstraintException constraint && !constraint.getDetails().isEmpty()) {
            return message + " (" + String.join("; ", constraint.getDetails()) + ")";
        }
        return message;
    }

    private ScanReview review(Long adminUserId, String reason, List<ScanIngredientDecision> decisions) {
        ScanReview review = new ScanReview();
        review.setDecidedByAdminId(adminUserId);
        review.setDecidedAt(OffsetDateTime.now());
        review.setReason(reason);
        review.setIngredientDecisions(decisions);
        return review;
    }

    private List<ScanIngredientDecision> decisionsOf(List<ScanIngredientDecisionRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream().map(request -> {
            ScanIngredientDecision decision = new ScanIngredientDecision();
            decision.setPosition(request.position());
            decision.setAction(request.action());
            decision.setIngredientId(request.ingredientId());
            decision.setInciName(request.inciName());
            return decision;
        }).toList();
    }

    private ScanSession save(ScanSession scanSession) {
        try {
            return scanSessionRepository.save(scanSession);
        } catch (OptimisticLockingFailureException ex) {
            throw new InvalidStateTransitionException(CONCURRENT_CHANGE_MESSAGE);
        } catch (DataAccessException ex) {
            log.error("Falha ao gravar a sessao de scan", ex);
            throw DataAccessFailureTranslator.translate(ex, "Falha ao gravar a sessao de scan");
        }
    }
}

package com.venus.crud.service.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanSession;
import com.venus.crud.dto.mongo.request.ScanApproveRequest;
import com.venus.crud.dto.mongo.request.ScanProductDecisionRequest;
import com.venus.crud.dto.mongo.request.ScanRejectRequest;
import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.exception.DataConstraintException;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import com.venus.crud.security.CurrentUserProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ScanReviewServiceTest {

    private static final String ID = "665f1b2c9a4e3b0012ab34cd";

    @Mock
    private ScanSessionService scanSessionService;

    @Mock
    private ScanSessionRepository scanSessionRepository;

    @Mock
    private ScanReviewValidator scanReviewValidator;

    @Mock
    private ScanCatalogSync scanCatalogSync;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private ScanReviewService service;

    @BeforeEach
    void setUp() {
        service = new ScanReviewService(scanSessionService, scanSessionRepository, scanReviewValidator, scanCatalogSync,
                currentUserProvider);
    }

    @Test
    void approvingOutsidePendingReviewIsRefused() {
        when(scanSessionService.getOrThrow(ID)).thenReturn(scanIn(ScanStatus.REJECTED));

        assertThatThrownBy(() -> service.approve(ID, approveRequest())).isInstanceOf(InvalidStateTransitionException.class);
        verifyNoInteractions(scanReviewValidator, scanCatalogSync);
    }

    @Test
    void concurrentDecisionIsRefused() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(new ScanApprovedSnapshot());
        when(scanSessionRepository.save(scan)).thenThrow(new OptimisticLockingFailureException("versao"));

        assertThatThrownBy(() -> service.approve(ID, approveRequest()))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("alterado por outra pessoa");
        verifyNoInteractions(scanCatalogSync);
    }

    @Test
    void successfulApprovalEndsSyncedWithTheCatalogIds() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        ScanApprovedSnapshot snapshot = new ScanApprovedSnapshot();
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(snapshot);
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scanCatalogSync.synchronize(scan)).thenReturn(new ScanCatalogSync.Result(57L, 130L));

        service.approve(ID, approveRequest());

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNCED);
        assertThat(scan.getSync().getProductId()).isEqualTo(57L);
        assertThat(scan.getSync().getProductVersionId()).isEqualTo(130L);
        assertThat(scan.getSync().getAttempts()).isEqualTo(1);
        assertThat(scan.getSync().getLastError()).isNull();
        assertThat(scan.getReview().getDecidedByAdminId()).isEqualTo(3L);
        assertThat(scan.getApprovedSnapshot()).isSameAs(snapshot);
        verify(scanSessionService).toResponse(scan);
    }

    @Test
    void failedSyncEndsSyncFailedAndTheRetryClearsTheError() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(new ScanApprovedSnapshot());
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scanCatalogSync.synchronize(scan))
                .thenThrow(new ResourceNotFoundException("Marca nao encontrada com id 12"))
                .thenReturn(new ScanCatalogSync.Result(57L, 130L));

        service.approve(ID, approveRequest());

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNC_FAILED);
        assertThat(scan.getSync().getLastError()).isEqualTo("Marca nao encontrada com id 12");
        assertThat(scan.getSync().getAttempts()).isEqualTo(1);
        assertThat(scan.getSync().getFailedAt()).isNotNull();

        service.sync(ID);

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNCED);
        assertThat(scan.getSync().getAttempts()).isEqualTo(2);
        assertThat(scan.getSync().getLastError()).isNull();
        assertThat(scan.getSync().getFailedAt()).isNull();
    }

    @Test
    void syncFromPendingReviewIsRefused() {
        when(scanSessionService.getOrThrow(ID)).thenReturn(scanIn(ScanStatus.PENDING_REVIEW));

        assertThatThrownBy(() -> service.sync(ID)).isInstanceOf(InvalidStateTransitionException.class);
        verifyNoInteractions(scanCatalogSync);
    }

    @Test
    void rejectionFillsTheReview() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.reject(ID, new ScanRejectRequest("Foto do verso ilegível"));

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.REJECTED);
        assertThat(scan.getReview().getDecidedByAdminId()).isEqualTo(3L);
        assertThat(scan.getReview().getReason()).isEqualTo("Foto do verso ilegível");
        verify(scanReviewValidator).validateReviewer(3L);
        verifyNoInteractions(scanCatalogSync);
    }

    @Test
    void conflictInTheFirstSyncIsRetriedOnceAndEndsSynced() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(new ScanApprovedSnapshot());
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scanCatalogSync.synchronize(scan))
                .thenThrow(conflict())
                .thenReturn(new ScanCatalogSync.Result(57L, 130L));

        service.approve(ID, approveRequest());

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNCED);
        assertThat(scan.getSync().getProductVersionId()).isEqualTo(130L);
        assertThat(scan.getSync().getAttempts()).isEqualTo(1);
        verify(scanCatalogSync, times(2)).synchronize(scan);
    }

    @Test
    void secondConflictInARowEndsSyncFailed() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(new ScanApprovedSnapshot());
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scanCatalogSync.synchronize(scan)).thenThrow(conflict(), conflict());

        service.approve(ID, approveRequest());

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNC_FAILED);
        assertThat(scan.getSync().getLastError()).startsWith("Ja existe um registro com os dados informados.");
        assertThat(scan.getSync().getAttempts()).isEqualTo(1);
        verify(scanCatalogSync, times(2)).synchronize(scan);
    }

    @Test
    void dataErrorThatIsNotAConflictIsNotRetried() {
        ScanSession scan = scanIn(ScanStatus.PENDING_REVIEW);
        when(scanSessionService.getOrThrow(ID)).thenReturn(scan);
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        when(scanReviewValidator.validate(eq(scan), any(), eq(3L))).thenReturn(new ScanApprovedSnapshot());
        when(scanSessionRepository.save(any(ScanSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scanCatalogSync.synchronize(scan)).thenThrow(new DataConstraintException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Um campo obrigatorio nao foi informado.", List.of("column: display_name")));

        service.approve(ID, approveRequest());

        assertThat(scan.getStatus()).isEqualTo(ScanStatus.SYNC_FAILED);
        assertThat(scan.getSync().getLastError()).isEqualTo("Um campo obrigatorio nao foi informado. (column: display_name)");
        verify(scanCatalogSync, times(1)).synchronize(scan);
    }

    private ScanSession scanIn(ScanStatus status) {
        ScanSession scanSession = new ScanSession();
        scanSession.setStatus(status);
        return scanSession;
    }

    private ScanApproveRequest approveRequest() {
        return new ScanApproveRequest(new ScanProductDecisionRequest(7L, null, null, null), List.of(), null);
    }

    private DataConstraintException conflict() {
        return new DataConstraintException(HttpStatus.CONFLICT, "Ja existe um registro com os dados informados.",
                List.of("constraint: products_slug_key"));
    }
}

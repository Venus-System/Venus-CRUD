package com.venus.crud.security;

import com.venus.crud.document.ScanSource;
import com.venus.crud.entity.user.User;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
import com.venus.crud.repository.jpa.scoring.RecommendationRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("ownership")
@Transactional(readOnly = true)
public class OwnershipGuard {

    private static final Logger log = LoggerFactory.getLogger(OwnershipGuard.class);

    private final CurrentUserProvider currentUserProvider;
    private final UserListRepository userListRepository;
    private final ReviewRepository reviewRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final RecommendationRepository recommendationRepository;
    private final RuleEvaluationRepository ruleEvaluationRepository;
    private final ScanSessionRepository scanSessionRepository;

    public OwnershipGuard(CurrentUserProvider currentUserProvider, UserListRepository userListRepository,
            ReviewRepository reviewRepository, AnalysisResultRepository analysisResultRepository,
            RecommendationRepository recommendationRepository, RuleEvaluationRepository ruleEvaluationRepository,
            ScanSessionRepository scanSessionRepository) {
        this.currentUserProvider = currentUserProvider;
        this.userListRepository = userListRepository;
        this.reviewRepository = reviewRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.recommendationRepository = recommendationRepository;
        this.ruleEvaluationRepository = ruleEvaluationRepository;
        this.scanSessionRepository = scanSessionRepository;
    }

    public boolean canAccessUser(Long userId) {
        if (currentUserProvider.isAdmin()) {
            return true;
        }
        return userId != null && currentUserId().filter(userId::equals).isPresent();
    }

    public boolean canAccessUserIfPresent(Long userId) {
        return userId == null || canAccessUser(userId);
    }

    public boolean canWriteForUser(Long pathUserId, Long bodyUserId) {
        return canAccessUser(pathUserId) && canAccessUserIfPresent(bodyUserId);
    }

    public boolean canAccessUserList(Long userListId) {
        return isOwnedByCurrentUser(userListId, id -> find(() -> userListRepository.findById(id),
                "Falha ao consultar a lista do usuario").map(userList -> userList.getUser().getId()));
    }

    public boolean canAccessUserListIfPresent(Long userListId) {
        return userListId == null || canAccessUserList(userListId);
    }

    public boolean canAccessReview(Long reviewId) {
        return isOwnedByCurrentUser(reviewId, id -> find(() -> reviewRepository.findById(id),
                "Falha ao consultar a avaliacao").map(review -> review.getUser().getId()));
    }

    public boolean canAccessAnalysisResult(Long analysisResultId) {
        return isOwnedByCurrentUser(analysisResultId, id -> find(() -> analysisResultRepository.findById(id),
                "Falha ao consultar a analise").map(analysisResult -> analysisResult.getUser().getId()));
    }

    public boolean canAccessRecommendation(Long recommendationId) {
        return isOwnedByCurrentUser(recommendationId, id -> find(() -> recommendationRepository.findById(id),
                "Falha ao consultar a recomendacao").map(recommendation -> recommendation.getUser().getId()));
    }

    public boolean canAccessRuleEvaluation(Long ruleEvaluationId) {
        return isOwnedByCurrentUser(ruleEvaluationId, id -> find(() -> ruleEvaluationRepository.findById(id),
                "Falha ao consultar a avaliacao de regra")
                .map(ruleEvaluation -> ruleEvaluation.getAnalysisResult().getUser().getId()));
    }

    public boolean canAccessScanSession(String scanSessionId) {
        return isOwnedByCurrentUser(scanSessionId, id -> find(() -> scanSessionRepository.findById(id),
                "Falha ao consultar a sessao de scan")
                .map(scanSession -> scanSession.getSource())
                .map(ScanSource::getUserId));
    }

    public boolean isCurrentFirebaseUid(String firebaseUid) {
        return firebaseUid != null && currentUserProvider.firebaseUid().filter(firebaseUid::equals).isPresent();
    }

    public boolean keepsOwnFirebaseUid(String firebaseUid) {
        return currentUserProvider.isAdmin() || isCurrentFirebaseUid(firebaseUid);
    }

    public boolean hasActiveAccount() {
        return currentUserProvider.activeUser().isPresent();
    }

    public boolean isCurrentAdmin(Long adminUserId) {
        return adminUserId != null && currentUserProvider.adminUserId().filter(adminUserId::equals).isPresent();
    }

    private <K> boolean isOwnedByCurrentUser(K recordId, Function<K, Optional<Long>> ownerLookup) {
        if (currentUserProvider.isAdmin()) {
            return true;
        }
        if (recordId == null) {
            return false;
        }
        Optional<Long> currentUserId = currentUserId();
        if (currentUserId.isEmpty()) {
            return false;
        }
        return ownerLookup.apply(recordId).filter(currentUserId.get()::equals).isPresent();
    }

    private Optional<Long> currentUserId() {
        return currentUserProvider.activeUser().map(User::getId);
    }

    private <T> T find(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}

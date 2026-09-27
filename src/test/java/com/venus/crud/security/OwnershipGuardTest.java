package com.venus.crud.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSource;
import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.scan.RuleEvaluation;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserList;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
import com.venus.crud.repository.jpa.scoring.RecommendationRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnershipGuardTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserListRepository userListRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AnalysisResultRepository analysisResultRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RuleEvaluationRepository ruleEvaluationRepository;

    @Mock
    private ScanSessionRepository scanSessionRepository;

    private OwnershipGuard guard;

    @BeforeEach
    void setUp() {
        guard = new OwnershipGuard(currentUserProvider, userListRepository, reviewRepository, analysisResultRepository,
                recommendationRepository, ruleEvaluationRepository, scanSessionRepository);
    }

    @Test
    void ownerAccessesOwnData() {
        loggedInAs(ANA_ID);
        assertThat(guard.canAccessUser(ANA_ID)).isTrue();
    }

    @Test
    void userCannotAccessAnotherUsersData() {
        loggedInAs(ANA_ID);
        assertThat(guard.canAccessUser(BIA_ID)).isFalse();
    }

    @Test
    void adminAccessesAnyUser() {
        when(currentUserProvider.isAdmin()).thenReturn(true);
        assertThat(guard.canAccessUser(BIA_ID)).isTrue();
    }

    @Test
    void tokenWithoutActiveAccountAccessesNothing() {
        when(currentUserProvider.isAdmin()).thenReturn(false);
        when(currentUserProvider.activeUser()).thenReturn(Optional.empty());
        assertThat(guard.canAccessUser(ANA_ID)).isFalse();
    }

    @Test
    void bodyPointingToAnotherUserIsRefused() {
        loggedInAs(ANA_ID);
        assertThat(guard.canWriteForUser(ANA_ID, BIA_ID)).isFalse();
        assertThat(guard.canWriteForUser(ANA_ID, null)).isTrue();
        assertThat(guard.canAccessUserIfPresent(null)).isTrue();
    }

    @Test
    void reviewIsCheckedAgainstItsOwner() {
        loggedInAs(ANA_ID);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewOf(ANA_ID)));
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(reviewOf(BIA_ID)));
        assertThat(guard.canAccessReview(1L)).isTrue();
        assertThat(guard.canAccessReview(2L)).isFalse();
    }

    @Test
    void missingReviewIsRefused() {
        loggedInAs(ANA_ID);
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(guard.canAccessReview(99L)).isFalse();
    }

    @Test
    void adminSkipsTheOwnerLookup() {
        when(currentUserProvider.isAdmin()).thenReturn(true);
        assertThat(guard.canAccessReview(2L)).isTrue();
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void userListIsCheckedAgainstItsOwner() {
        loggedInAs(ANA_ID);
        UserList userList = new UserList();
        userList.setUser(user(BIA_ID));
        when(userListRepository.findById(5L)).thenReturn(Optional.of(userList));
        assertThat(guard.canAccessUserList(5L)).isFalse();
        assertThat(guard.canAccessUserListIfPresent(null)).isTrue();
    }

    @Test
    void ruleEvaluationFollowsItsAnalysisResult() {
        loggedInAs(ANA_ID);
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setUser(user(ANA_ID));
        RuleEvaluation ruleEvaluation = new RuleEvaluation();
        ruleEvaluation.setAnalysisResult(analysisResult);
        when(ruleEvaluationRepository.findById(7L)).thenReturn(Optional.of(ruleEvaluation));
        assertThat(guard.canAccessRuleEvaluation(7L)).isTrue();
    }

    @Test
    void scanSessionIsCheckedAgainstItsSource() {
        loggedInAs(ANA_ID);
        when(scanSessionRepository.findById("scan-ana")).thenReturn(Optional.of(scanOf(ANA_ID)));
        when(scanSessionRepository.findById("scan-bia")).thenReturn(Optional.of(scanOf(BIA_ID)));
        assertThat(guard.canAccessScanSession("scan-ana")).isTrue();
        assertThat(guard.canAccessScanSession("scan-bia")).isFalse();
    }

    @Test
    void firebaseUidInTheBodyMustBeTheTokenUid() {
        when(currentUserProvider.firebaseUid()).thenReturn(Optional.of("uid-ana"));
        assertThat(guard.isCurrentFirebaseUid("uid-ana")).isTrue();
        assertThat(guard.isCurrentFirebaseUid("uid-bia")).isFalse();
    }

    @Test
    void adminKeepsAnyFirebaseUid() {
        when(currentUserProvider.isAdmin()).thenReturn(true);
        assertThat(guard.keepsOwnFirebaseUid("uid-bia")).isTrue();
    }

    @Test
    void passwordChangeOnlyForTheSameAdmin() {
        when(currentUserProvider.adminUserId()).thenReturn(Optional.of(3L));
        assertThat(guard.isCurrentAdmin(3L)).isTrue();
        assertThat(guard.isCurrentAdmin(4L)).isFalse();
    }

    private void loggedInAs(long userId) {
        lenient().when(currentUserProvider.isAdmin()).thenReturn(false);
        lenient().when(currentUserProvider.activeUser()).thenReturn(Optional.of(user(userId)));
    }

    private User user(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Review reviewOf(long userId) {
        Review review = new Review();
        review.setUser(user(userId));
        return review;
    }

    private ScanSession scanOf(long userId) {
        ScanSource source = new ScanSource();
        source.setUserId(userId);
        ScanSession scanSession = new ScanSession();
        scanSession.setSource(source);
        return scanSession;
    }
}

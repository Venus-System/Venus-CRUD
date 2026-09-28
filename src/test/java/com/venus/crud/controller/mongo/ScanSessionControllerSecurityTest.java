package com.venus.crud.controller.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venus.crud.config.JacksonConfig;
import com.venus.crud.config.SecurityConfig;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import com.venus.crud.repository.jpa.scan.AnalysisResultRepository;
import com.venus.crud.repository.jpa.scan.RuleEvaluationRepository;
import com.venus.crud.repository.jpa.scoring.RecommendationRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import com.venus.crud.security.AdminJwtAuthenticationConverter;
import com.venus.crud.security.AdminTokenService;
import com.venus.crud.security.CurrentUserProvider;
import com.venus.crud.security.OwnershipGuard;
import com.venus.crud.security.SecurityErrorHandler;
import com.venus.crud.security.SecurityRoles;
import com.venus.crud.service.mongo.ScanCloudinaryService;
import com.venus.crud.service.mongo.ScanReviewService;
import com.venus.crud.service.mongo.ScanSessionService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = ScanSessionController.class, properties = {
        "venus.security.admin-token.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "venus.security.admin-token.expiration=PT8H",
        "venus.security.firebase.project-id=venus-81bac",
        "server.port=0"
})
@Import({SecurityConfig.class, JacksonConfig.class, AdminTokenService.class, CurrentUserProvider.class,
        OwnershipGuard.class, SecurityErrorHandler.class})
class ScanSessionControllerSecurityTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;
    private static final String ANA_UID = "uid-ana";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScanSessionService scanSessionService;

    @MockBean
    private ScanCloudinaryService scanCloudinaryService;

    @MockBean
    private ScanReviewService scanReviewService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserListRepository userListRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private AnalysisResultRepository analysisResultRepository;

    @MockBean
    private RecommendationRepository recommendationRepository;

    @MockBean
    private RuleEvaluationRepository ruleEvaluationRepository;

    @MockBean
    private ScanSessionRepository scanSessionRepository;

    @Test
    void withoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/scan-sessions/user/{userId}", ANA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ownerListsOwnScansNewestFirst() throws Exception {
        registeredAna();
        mockMvc.perform(get("/api/scan-sessions/user/{userId}", ANA_ID).with(appUser()))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(scanSessionService).findByUserId(eq(ANA_ID), isNull(), pageable.capture());
        assertThat(pageable.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Test
    void anotherUsersScansReturn403() throws Exception {
        registeredAna();
        mockMvc.perform(get("/api/scan-sessions/user/{userId}", BIA_ID).with(appUser()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.ACCESS_DENIED_MESSAGE));
        verifyNoInteractions(scanSessionService);
    }

    @Test
    void analystListsAnyUsersScansFilteredByStatus() throws Exception {
        mockMvc.perform(get("/api/scan-sessions/user/{userId}", BIA_ID).param("status", "PENDING_REVIEW")
                        .with(admin(AdminRole.ANALYST)))
                .andExpect(status().isOk());

        verify(scanSessionService).findByUserId(eq(BIA_ID), eq(ScanStatus.PENDING_REVIEW), any(Pageable.class));
    }

    private void registeredAna() {
        User user = new User();
        user.setId(ANA_ID);
        user.setFirebaseUid(ANA_UID);
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user));
    }

    private static RequestPostProcessor appUser() {
        return jwt().jwt(token -> token.subject(ANA_UID)).authorities(new SimpleGrantedAuthority(SecurityRoles.USER));
    }

    private static RequestPostProcessor admin(AdminRole role) {
        return jwt().jwt(token -> token.subject("3")).authorities(AdminJwtAuthenticationConverter.authoritiesFor(role));
    }
}

package com.venus.crud.controller.jpa.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venus.crud.config.JacksonConfig;
import com.venus.crud.config.SecurityConfig;
import com.venus.crud.dto.jpa.request.review.ReportRequest;
import com.venus.crud.dto.jpa.response.review.ReportResponse;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
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
import com.venus.crud.service.jpa.review.ReportService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = ReportController.class, properties = {
        "venus.security.admin-token.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "venus.security.admin-token.expiration=PT8H",
        "venus.security.firebase.project-id=venus-81bac",
        "server.port=0"
})
@Import({SecurityConfig.class, JacksonConfig.class, AdminTokenService.class, CurrentUserProvider.class,
        OwnershipGuard.class, SecurityErrorHandler.class})
class ReportControllerSecurityTest {

    private static final long ANA_ID = 10L;
    private static final String ANA_UID = "uid-ana";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

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
    void appUserReportsAReviewAsOpen() throws Exception {
        registeredAna();
        when(reportService.create(any(ReportRequest.class))).thenReturn(response(ReportStatus.OPEN));
        mockMvc.perform(post("/api/reports").with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content(reportBody(ReportStatus.OPEN)))
                .andExpect(status().isCreated());
    }

    @Test
    void appUserCannotCreateAClosedReport() throws Exception {
        registeredAna();
        mockMvc.perform(post("/api/reports").with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content(reportBody(ReportStatus.RESOLVED)))
                .andExpect(status().isForbidden());
        verifyNoInteractions(reportService);
    }

    @Test
    void moderatorCreatesAReportInAnyStatus() throws Exception {
        when(reportService.create(any(ReportRequest.class))).thenReturn(response(ReportStatus.RESOLVED));
        mockMvc.perform(post("/api/reports").with(admin(AdminRole.MODERATOR))
                        .contentType(MediaType.APPLICATION_JSON).content(reportBody(ReportStatus.RESOLVED)))
                .andExpect(status().isCreated());
    }

    private void registeredAna() {
        User user = new User();
        user.setId(ANA_ID);
        user.setFirebaseUid(ANA_UID);
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user));
    }

    private static String reportBody(ReportStatus status) {
        return "{\"userId\": " + ANA_ID + ", \"targetType\": \"REVIEW\", \"targetId\": 88, "
                + "\"reason\": \"Avaliacao ofensiva\", \"status\": \"" + status + "\"}";
    }

    private static ReportResponse response(ReportStatus status) {
        return new ReportResponse(1L, ANA_ID, null, ReportTargetType.REVIEW, 88L, "Avaliacao ofensiva", status,
                null, null, null);
    }

    private static RequestPostProcessor appUser() {
        return jwt().jwt(token -> token.subject(ANA_UID)).authorities(new SimpleGrantedAuthority(SecurityRoles.USER));
    }

    private static RequestPostProcessor admin(AdminRole role) {
        return jwt().jwt(token -> token.subject("3")).authorities(AdminJwtAuthenticationConverter.authoritiesFor(role));
    }
}

package com.venus.crud.controller.jpa.user;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venus.crud.config.JacksonConfig;
import com.venus.crud.config.SecurityConfig;
import com.venus.crud.entity.enums.AdminRole;
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
import com.venus.crud.service.jpa.user.UserProfileService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = UserProfileController.class, properties = {
        "venus.security.admin-token.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "venus.security.admin-token.expiration=PT8H",
        "venus.security.firebase.project-id=venus-81bac",
        "server.port=0"
})
@Import({SecurityConfig.class, JacksonConfig.class, AdminTokenService.class, CurrentUserProvider.class,
        OwnershipGuard.class, SecurityErrorHandler.class})
class UserProfileControllerSecurityTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;
    private static final String ANA_UID = "uid-ana";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

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
    void withoutTokenReturns401InTheErrorFormat() throws Exception {
        mockMvc.perform(get("/api/user-profiles/{userId}", ANA_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.MISSING_TOKEN_MESSAGE));
    }

    @Test
    void malformedTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/user-profiles/{userId}", ANA_ID).header(HttpHeaders.AUTHORIZATION, "Bearer abc"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.INVALID_TOKEN_MESSAGE));
    }

    @Test
    void ownerReadsOwnProfile() throws Exception {
        registeredAna(UserStatus.ACTIVE);
        mockMvc.perform(get("/api/user-profiles/{userId}", ANA_ID).with(appUser()))
                .andExpect(status().isOk());
    }

    @Test
    void anotherUsersProfileReturns403() throws Exception {
        registeredAna(UserStatus.ACTIVE);
        mockMvc.perform(get("/api/user-profiles/{userId}", BIA_ID).with(appUser()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.ACCESS_DENIED_MESSAGE));
    }

    @Test
    void blockedUserReturns403() throws Exception {
        registeredAna(UserStatus.BLOCKED);
        mockMvc.perform(get("/api/user-profiles/{userId}", ANA_ID).with(appUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminReadsAnyProfile() throws Exception {
        mockMvc.perform(get("/api/user-profiles/{userId}", BIA_ID).with(admin(AdminRole.ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    void analystCannotListProfilesButCanCount() throws Exception {
        mockMvc.perform(get("/api/user-profiles").with(admin(AdminRole.ANALYST)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/user-profiles/count/skin-type").param("skinType", "OILY").with(admin(AdminRole.ANALYST)))
                .andExpect(status().isOk());
    }

    @Test
    void patchPointingToAnotherUserReturns403() throws Exception {
        registeredAna(UserStatus.ACTIVE);
        mockMvc.perform(patch("/api/user-profiles/{userId}", ANA_ID).with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"userId\": " + BIA_ID + "}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(userProfileService);
    }

    @Test
    void patchWithoutUserIdInTheBodyPasses() throws Exception {
        registeredAna(UserStatus.ACTIVE);
        mockMvc.perform(patch("/api/user-profiles/{userId}", ANA_ID).with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void appUserCannotWriteTheCatalog() throws Exception {
        mockMvc.perform(post("/api/products").with(appUser()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.ACCESS_DENIED_MESSAGE));
    }

    @Test
    void moderatorCannotWriteScoringRules() throws Exception {
        mockMvc.perform(post("/api/compatibility-rules").with(admin(AdminRole.MODERATOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void catalogReadWithoutTokenPassesSecurity() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isNotFound());
    }

    private void registeredAna(UserStatus status) {
        User user = new User();
        user.setId(ANA_ID);
        user.setFirebaseUid(ANA_UID);
        user.setStatus(status);
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user));
    }

    private static RequestPostProcessor appUser() {
        return jwt().jwt(token -> token.subject(ANA_UID)).authorities(new SimpleGrantedAuthority(SecurityRoles.USER));
    }

    private static RequestPostProcessor admin(AdminRole role) {
        return jwt().jwt(token -> token.subject("3")).authorities(AdminJwtAuthenticationConverter.authoritiesFor(role));
    }
}

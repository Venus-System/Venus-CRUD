package com.venus.crud.controller.jpa.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venus.crud.config.JacksonConfig;
import com.venus.crud.config.SecurityConfig;
import com.venus.crud.dto.jpa.response.user.UserResponse;
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
import com.venus.crud.service.jpa.user.UserService;
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

@WebMvcTest(controllers = UserController.class, properties = {
        "venus.security.admin-token.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "venus.security.admin-token.expiration=PT8H",
        "venus.security.firebase.project-id=venus-81bac",
        "server.port=0"
})
@Import({SecurityConfig.class, JacksonConfig.class, AdminTokenService.class, CurrentUserProvider.class,
        OwnershipGuard.class, SecurityErrorHandler.class})
class UserControllerSecurityTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;
    private static final String ANA_UID = "uid-ana";
    private static final String BIA_UID = "uid-bia";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    void appUserFindsOwnIdBySearch() throws Exception {
        mockMvc.perform(get("/api/users/search").param("firebaseUid", ANA_UID).with(appUser()))
                .andExpect(status().isOk());
    }

    @Test
    void searchForAnotherUidReturns403() throws Exception {
        mockMvc.perform(get("/api/users/search").param("firebaseUid", BIA_UID).with(appUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(userService);
    }

    @Test
    void adminSearchesByAnyFilter() throws Exception {
        mockMvc.perform(get("/api/users/search").param("name", "Ana").with(admin(AdminRole.ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    void ownerChangesOwnNameWithoutStatus() throws Exception {
        registeredAna();
        when(userService.patch(eq(ANA_ID), any())).thenReturn(anaResponse(UserStatus.ACTIVE));
        mockMvc.perform(patch("/api/users/{id}", ANA_ID).with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"Ana Souza\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void ownerCannotChangeOwnStatus() throws Exception {
        registeredAna();
        mockMvc.perform(patch("/api/users/{id}", ANA_ID).with(appUser())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(userService);
    }

    @Test
    void adminChangesAnyonesStatus() throws Exception {
        when(userService.patch(eq(BIA_ID), any())).thenReturn(anaResponse(UserStatus.BLOCKED));
        mockMvc.perform(patch("/api/users/{id}", BIA_ID).with(admin(AdminRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"BLOCKED\"}"))
                .andExpect(status().isOk());
    }

    private void registeredAna() {
        User user = new User();
        user.setId(ANA_ID);
        user.setFirebaseUid(ANA_UID);
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user));
    }

    private static UserResponse anaResponse(UserStatus status) {
        return new UserResponse(ANA_ID, ANA_UID, "Ana Souza", "ana@venus.com", status, null, null, null);
    }

    private static RequestPostProcessor appUser() {
        return jwt().jwt(token -> token.subject(ANA_UID)).authorities(new SimpleGrantedAuthority(SecurityRoles.USER));
    }

    private static RequestPostProcessor admin(AdminRole role) {
        return jwt().jwt(token -> token.subject("3")).authorities(AdminJwtAuthenticationConverter.authoritiesFor(role));
    }
}

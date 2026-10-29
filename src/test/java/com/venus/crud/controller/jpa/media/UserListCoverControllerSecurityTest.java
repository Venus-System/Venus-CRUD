package com.venus.crud.controller.jpa.media;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venus.crud.config.JacksonConfig;
import com.venus.crud.config.SecurityConfig;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserList;
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
import com.venus.crud.service.jpa.media.MediaAssetService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = UserListCoverController.class, properties = {
        "venus.security.admin-token.secret=segredo-de-teste-com-mais-de-32-bytes!!",
        "venus.security.admin-token.expiration=PT8H",
        "venus.security.firebase.project-id=venus-81bac",
        "server.port=0"
})
@Import({SecurityConfig.class, JacksonConfig.class, AdminTokenService.class, CurrentUserProvider.class,
        OwnershipGuard.class, SecurityErrorHandler.class})
class UserListCoverControllerSecurityTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;
    private static final long ANA_LIST_ID = 70L;
    private static final long BIA_LIST_ID = 71L;
    private static final String ANA_UID = "uid-ana";
    private static final String COVER_PATH = "/api/user-lists/{id}/cover";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaAssetService mediaAssetService;

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

    @BeforeEach
    void setUp() {
        User ana = new User();
        ana.setId(ANA_ID);
        ana.setFirebaseUid(ANA_UID);
        ana.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(ana));
        when(userListRepository.findById(ANA_LIST_ID)).thenReturn(Optional.of(listOwnedBy(ANA_ID)));
        when(userListRepository.findById(BIA_LIST_ID)).thenReturn(Optional.of(listOwnedBy(BIA_ID)));
    }

    @Test
    void withoutTokenReturns401() throws Exception {
        mockMvc.perform(get(COVER_PATH, ANA_LIST_ID))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(mediaAssetService);
    }

    @Test
    void sendingTheCoverOfAnotherUsersListReturns403() throws Exception {
        mockMvc.perform(multipart(COVER_PATH, BIA_LIST_ID).file(coverImage()).with(appUser()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(SecurityErrorHandler.ACCESS_DENIED_MESSAGE));
        verifyNoInteractions(mediaAssetService);
    }

    @Test
    void readingTheCoverOfAnotherUsersListReturns403() throws Exception {
        mockMvc.perform(get(COVER_PATH, BIA_LIST_ID).with(appUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(mediaAssetService);
    }

    @Test
    void deletingTheCoverOfAnotherUsersListReturns403() throws Exception {
        mockMvc.perform(delete(COVER_PATH, BIA_LIST_ID).with(appUser()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(mediaAssetService);
    }

    @Test
    void ownerSendsReadsAndDeletesTheCover() throws Exception {
        mockMvc.perform(multipart(COVER_PATH, ANA_LIST_ID).file(coverImage()).with(appUser()))
                .andExpect(status().isCreated());
        mockMvc.perform(get(COVER_PATH, ANA_LIST_ID).with(appUser()))
                .andExpect(status().isOk());
        mockMvc.perform(delete(COVER_PATH, ANA_LIST_ID).with(appUser()))
                .andExpect(status().isNoContent());
    }

    @Test
    void adminReadsTheCoverOfAnyList() throws Exception {
        mockMvc.perform(get(COVER_PATH, BIA_LIST_ID).with(admin()))
                .andExpect(status().isOk());
    }

    private static UserList listOwnedBy(long ownerId) {
        User owner = new User();
        owner.setId(ownerId);
        UserList userList = new UserList();
        userList.setUser(owner);
        return userList;
    }

    private static MockMultipartFile coverImage() {
        return new MockMultipartFile("file", "capa.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[] {1, 2, 3});
    }

    private static RequestPostProcessor appUser() {
        return jwt().jwt(token -> token.subject(ANA_UID)).authorities(new SimpleGrantedAuthority(SecurityRoles.USER));
    }

    private static RequestPostProcessor admin() {
        return jwt().jwt(token -> token.subject("3")).authorities(AdminJwtAuthenticationConverter.authoritiesFor(AdminRole.ADMIN));
    }
}

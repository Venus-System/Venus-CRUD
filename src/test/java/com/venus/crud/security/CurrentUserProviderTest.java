package com.venus.crud.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class CurrentUserProviderTest {

    private static final String ANA_UID = "uid-ana";

    @Mock
    private UserRepository userRepository;

    private CurrentUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new CurrentUserProvider(userRepository);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "PENDING"})
    void activeAndPendingUsersHaveAnActiveAccount(UserStatus status) {
        authenticate(ANA_UID, List.of(new SimpleGrantedAuthority(SecurityRoles.USER)));
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user(status)));

        assertThat(provider.activeUser()).isPresent();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"BLOCKED", "INACTIVE"})
    void blockedAndInactiveUsersHaveNoActiveAccount(UserStatus status) {
        authenticate(ANA_UID, List.of(new SimpleGrantedAuthority(SecurityRoles.USER)));
        when(userRepository.findByFirebaseUid(ANA_UID)).thenReturn(Optional.of(user(status)));

        assertThat(provider.activeUser()).isEmpty();
    }

    @Test
    void adminTokenGivesTheAdminIdAndNoFirebaseUid() {
        authenticate("3", AdminJwtAuthenticationConverter.authoritiesFor(AdminRole.MODERATOR));

        assertThat(provider.adminUserId()).contains(3L);
        assertThat(provider.firebaseUid()).isEmpty();
        assertThat(provider.activeUser()).isEmpty();
        assertThat(provider.isAdmin()).isFalse();
        verifyNoInteractions(userRepository);
    }

    @Test
    void adminRoleIsAdmin() {
        authenticate("3", AdminJwtAuthenticationConverter.authoritiesFor(AdminRole.ADMIN));

        assertThat(provider.isAdmin()).isTrue();
    }

    @Test
    void withoutAuthenticationThereIsNobody() {
        assertThat(provider.isAdmin()).isFalse();
        assertThat(provider.firebaseUid()).isEmpty();
        assertThat(provider.adminUserId()).isEmpty();
    }

    private void authenticate(String subject, Collection<? extends GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject(subject).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, authorities, subject));
    }

    private User user(UserStatus status) {
        User user = new User();
        user.setId(10L);
        user.setFirebaseUid(ANA_UID);
        user.setStatus(status);
        return user;
    }
}

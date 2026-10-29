package com.venus.crud.service.jpa.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.request.admin.AdminLoginRequest;
import com.venus.crud.dto.jpa.response.admin.AdminLoginResponse;
import com.venus.crud.dto.jpa.response.admin.AdminUserResponse;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.exception.InvalidCredentialsException;
import com.venus.crud.mapper.jpa.admin.AdminUserMapper;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import com.venus.crud.security.AdminTokenService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    private static final String EMAIL = "ana@venus.com";
    private static final String PASSWORD = "SenhaForte123";
    private static final String INVALID_CREDENTIALS = "E-mail ou senha invalidos.";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private AdminTokenService adminTokenService;

    private AdminAuthService service;

    @BeforeEach
    void setUp() {
        service = new AdminAuthService(adminUserRepository, adminUserMapper, passwordEncoder, adminTokenService);
    }

    @Test
    void rightPasswordReturnsTheToken() {
        AdminUser admin = admin(true, passwordEncoder.encode(PASSWORD));
        Instant expiresAt = Instant.parse("2026-09-27T01:30:00Z");
        AdminUserResponse adminResponse = new AdminUserResponse(3L, "Ana Souza", EMAIL, AdminRole.MODERATOR, true, true, null, null);
        when(adminUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin));
        when(adminTokenService.issue(admin)).thenReturn(new AdminTokenService.AdminToken("token-assinado", expiresAt));
        when(adminUserMapper.toResponse(admin)).thenReturn(adminResponse);

        AdminLoginResponse response = service.login(new AdminLoginRequest(EMAIL, PASSWORD));

        assertThat(response.accessToken()).isEqualTo("token-assinado");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresAt().toInstant()).isEqualTo(expiresAt);
        assertThat(response.admin()).isEqualTo(adminResponse);
    }

    @Test
    void wrongPasswordIsRejected() {
        when(adminUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin(true, passwordEncoder.encode(PASSWORD))));
        assertRejected(new AdminLoginRequest(EMAIL, "SenhaErrada"));
    }

    @Test
    void unknownEmailIsRejected() {
        when(adminUserRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertRejected(new AdminLoginRequest(EMAIL, PASSWORD));
    }

    @Test
    void adminWithoutPasswordIsRejected() {
        when(adminUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin(true, null)));
        assertRejected(new AdminLoginRequest(EMAIL, PASSWORD));
    }

    @Test
    void inactiveAdminIsRejectedEvenWithTheRightPassword() {
        when(adminUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin(false, passwordEncoder.encode(PASSWORD))));
        assertRejected(new AdminLoginRequest(EMAIL, PASSWORD));
    }

    private void assertRejected(AdminLoginRequest request) {
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage(INVALID_CREDENTIALS);
        verifyNoInteractions(adminTokenService);
    }

    private AdminUser admin(boolean active, String passwordHash) {
        AdminUser admin = new AdminUser();
        admin.setId(3L);
        admin.setName("Ana Souza");
        admin.setEmail(EMAIL);
        admin.setRole(AdminRole.MODERATOR);
        admin.setIsActive(active);
        admin.setPasswordHash(passwordHash);
        return admin;
    }
}

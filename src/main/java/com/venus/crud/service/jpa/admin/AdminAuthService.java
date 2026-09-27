package com.venus.crud.service.jpa.admin;

import com.venus.crud.dto.jpa.request.admin.AdminLoginRequest;
import com.venus.crud.dto.jpa.response.admin.AdminLoginResponse;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.InvalidCredentialsException;
import com.venus.crud.mapper.jpa.admin.AdminUserMapper;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import com.venus.crud.security.AdminTokenService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthService.class);

    private static final String INVALID_CREDENTIALS_MESSAGE = "E-mail ou senha invalidos.";
    private static final String TOKEN_TYPE = "Bearer";

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminTokenService adminTokenService;
    private final String decoyPasswordHash;

    public AdminAuthService(AdminUserRepository adminUserRepository, AdminUserMapper adminUserMapper,
            PasswordEncoder passwordEncoder, AdminTokenService adminTokenService) {
        this.adminUserRepository = adminUserRepository;
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminTokenService = adminTokenService;
        this.decoyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse login(AdminLoginRequest request) {
        Optional<AdminUser> found = executeOrFail(() -> adminUserRepository.findByEmail(request.email()),
                "Falha ao consultar administrador para login");
        String passwordHash = found.map(AdminUser::getPasswordHash).orElse(decoyPasswordHash);
        boolean passwordMatches = passwordEncoder.matches(request.password(), passwordHash);

        AdminUser adminUser = found
                .filter(admin -> admin.getPasswordHash() != null)
                .filter(admin -> passwordMatches)
                .filter(admin -> Boolean.TRUE.equals(admin.getIsActive()))
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        AdminTokenService.AdminToken token = adminTokenService.issue(adminUser);
        return new AdminLoginResponse(token.value(), TOKEN_TYPE,
                OffsetDateTime.ofInstant(token.expiresAt(), ZoneId.systemDefault()), adminUserMapper.toResponse(adminUser));
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}

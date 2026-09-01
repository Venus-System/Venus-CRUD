package com.venus.crud.service.jpa.admin;

import com.venus.crud.dto.jpa.patch.admin.AdminUserPatchRequest;
import com.venus.crud.dto.jpa.request.admin.AdminUserRequest;
import com.venus.crud.dto.jpa.response.admin.AdminUserResponse;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.admin.AdminUserMapper;
import com.venus.crud.repository.jpa.admin.AdminUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;

    public AdminUserService(AdminUserRepository adminUserRepository, AdminUserMapper adminUserMapper) {
        this.adminUserRepository = adminUserRepository;
        this.adminUserMapper = adminUserMapper;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> findAll() {
        return executeOrFail(adminUserRepository::findAll, "Falha ao consultar administradores no banco de dados").stream()
                .map(adminUserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserResponse findById(Long id) {
        return adminUserMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public AdminUserResponse findByEmail(String email) {
        return executeOrFail(() -> adminUserRepository.findByEmail(email), "Falha ao consultar administrador por email")
                .map(adminUserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado com o email " + email));
    }

    @Transactional(readOnly = true)
    public Slice<AdminUserResponse> search(String name, AdminRole role, Boolean isActive, Pageable pageable) {
        Slice<AdminUser> result;
        if (StringUtils.hasText(name)) {
            result = executeOrFail(() -> adminUserRepository.findByNameContainingIgnoreCase(name, pageable),
                    "Falha ao consultar administradores por nome");
        } else if (role != null) {
            result = executeOrFail(() -> adminUserRepository.findByRole(role, pageable), "Falha ao consultar administradores por papel");
        } else if (Boolean.TRUE.equals(isActive)) {
            result = executeOrFail(() -> adminUserRepository.findByIsActiveTrue(pageable), "Falha ao consultar administradores ativos");
        } else {
            result = executeOrFail(() -> adminUserRepository.findAllBy(pageable), "Falha ao consultar administradores");
        }

        return result.map(adminUserMapper::toResponse);
    }

    @Transactional
    public AdminUserResponse create(AdminUserRequest request) {
        ensureEmailAvailable(request.email(), null);

        AdminUser adminUser = adminUserMapper.toEntity(request);
        AdminUser saved = executeOrFail(() -> adminUserRepository.save(adminUser), "Falha ao criar administrador no banco de dados");
        return adminUserMapper.toResponse(saved);
    }

    @Transactional
    public AdminUserResponse update(Long id, AdminUserRequest request) {
        AdminUser adminUser = getOrThrow(id);
        ensureEmailAvailable(request.email(), id);

        adminUserMapper.updateEntity(request, adminUser);
        AdminUser saved = executeOrFail(() -> adminUserRepository.save(adminUser), "Falha ao atualizar administrador no banco de dados");
        return adminUserMapper.toResponse(saved);
    }

    @Transactional
    public AdminUserResponse patch(Long id, AdminUserPatchRequest request) {
        AdminUser adminUser = getOrThrow(id);
        if (StringUtils.hasText(request.email())) {
            ensureEmailAvailable(request.email(), id);
        }

        adminUserMapper.patchEntity(request, adminUser);
        AdminUser saved = executeOrFail(() -> adminUserRepository.save(adminUser), "Falha ao atualizar administrador no banco de dados");
        return adminUserMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        AdminUser adminUser = getOrThrow(id);
        executeOrFail(() -> {
            adminUserRepository.delete(adminUser);
            return null;
        }, "Falha ao remover administrador no banco de dados");
    }

    private AdminUser getOrThrow(Long id) {
        Optional<AdminUser> adminUser = executeOrFail(() -> adminUserRepository.findById(id),
                "Falha ao consultar administrador no banco de dados");
        return adminUser.orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado com id " + id));
    }

    private void ensureEmailAvailable(String email, Long excludeId) {
        boolean inUse = executeOrFail(() -> adminUserRepository.findByEmail(email), "Falha ao verificar email do administrador")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um administrador com o email " + email);
        }
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violacao de integridade de dados: {}", ex.getMessage());
            throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }
}
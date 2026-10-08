package com.venus.crud.service.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserRequest;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.user.UserMapper;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.security.CurrentUserProvider;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return executeOrFail(userRepository::findAll, "Falha ao consultar usuarios no banco de dados").stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<UserResponse> search(UserStatus status, String name, String firebaseUid, Pageable pageable) {
        String nameOrNull = StringUtils.hasText(name) ? name : null;
        String firebaseUidOrNull = StringUtils.hasText(firebaseUid) ? firebaseUid : null;

        Slice<User> users = executeOrFail(
                () -> userRepository.search(status, nameOrNull, firebaseUidOrNull, pageable),
                "Falha ao consultar usuarios");

        return users.map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        ensureFirebaseUidAvailable(request.firebaseUid(), null);
        ensureEmailAvailable(request.email(), null);

        User user = userMapper.toEntity(request);
        user.setPasswordHash(hashPasswordIfPresent(request.password()));

        User saved = executeOrFail(() -> userRepository.save(user), "Falha ao criar usuario no banco de dados");
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getOrThrow(id);
        ensureFirebaseUidAvailable(request.firebaseUid(), id);
        ensureEmailAvailable(request.email(), id);

        userMapper.updateEntity(request, user);
        if (StringUtils.hasText(request.password())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        User saved = executeOrFail(() -> userRepository.save(user), "Falha ao atualizar usuario no banco de dados");
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse patch(Long id, UserPatchRequest request) {
        User user = getOrThrow(id);
        if (StringUtils.hasText(request.email())) {
            ensureEmailAvailable(request.email(), id);
        }

        userMapper.patchEntity(request, user);
        if (StringUtils.hasText(request.password())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        User saved = executeOrFail(() -> userRepository.save(user), "Falha ao atualizar usuario no banco de dados");
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        User user = getOrThrow(id);
        executeOrFail(() -> {
            userRepository.delete(user);
            return null;
        }, "Falha ao remover usuario no banco de dados");
    }

    @Transactional
    public void registerAccess() {
        Long userId = currentUserProvider.activeUser()
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario logado nao encontrado"));
        executeOrFail(() -> userRepository.registerAccess(userId, "APP_OPEN", "{}"),
                "Falha ao registrar acesso do usuario");
    }

    private String hashPasswordIfPresent(String rawPassword) {
        return StringUtils.hasText(rawPassword) ? passwordEncoder.encode(rawPassword) : null;
    }

    private User getOrThrow(Long id) {
        Optional<User> user = executeOrFail(() -> userRepository.findById(id), "Falha ao consultar usuario no banco de dados");
        return user.orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id " + id));
    }

    private void ensureFirebaseUidAvailable(String firebaseUid, Long excludeId) {
        boolean inUse = executeOrFail(() -> userRepository.findByFirebaseUid(firebaseUid), "Falha ao verificar firebaseUid")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um usuario com o firebaseUid " + firebaseUid);
        }
    }

    private void ensureEmailAvailable(String email, Long excludeId) {
        if (!StringUtils.hasText(email)) {
            return;
        }
        boolean inUse = executeOrFail(() -> userRepository.findByEmailIgnoreCase(email), "Falha ao verificar email")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um usuario com o email " + email);
        }
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
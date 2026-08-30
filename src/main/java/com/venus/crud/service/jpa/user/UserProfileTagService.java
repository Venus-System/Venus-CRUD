package com.venus.crud.service.jpa.user;

import com.venus.crud.dto.jpa.request.user.UserProfileTagRequest;
import com.venus.crud.dto.jpa.response.user.UserProfileTagResponse;
import com.venus.crud.entity.user.UserProfileTag;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.user.UserProfileTagMapper;
import com.venus.crud.repository.jpa.user.UserProfileTagRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileTagService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileTagService.class);

    private final UserProfileTagRepository userProfileTagRepository;
    private final UserProfileTagMapper userProfileTagMapper;

    public UserProfileTagService(UserProfileTagRepository userProfileTagRepository, UserProfileTagMapper userProfileTagMapper) {
        this.userProfileTagRepository = userProfileTagRepository;
        this.userProfileTagMapper = userProfileTagMapper;
    }

    @Transactional(readOnly = true)
    public List<UserProfileTagResponse> findAll() {
        return executeOrFail(userProfileTagRepository::findAll, "Falha ao consultar tags de usuario no banco de dados").stream()
                .map(userProfileTagMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<UserProfileTagResponse> findByUserId(Long userId, Pageable pageable) {
        return executeOrFail(() -> userProfileTagRepository.findByUserId(userId, pageable), "Falha ao consultar tags do usuario")
                .map(userProfileTagMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<UserProfileTagResponse> findByProfileTagId(Long profileTagId, Pageable pageable) {
        return executeOrFail(() -> userProfileTagRepository.findByProfileTagId(profileTagId, pageable), "Falha ao consultar usuarios com a tag")
                .map(userProfileTagMapper::toResponse);
    }

    @Transactional
    public UserProfileTagResponse create(UserProfileTagRequest request) {
        ensureTagNotAssigned(request.userId(), request.profileTagId());

        UserProfileTag userProfileTag = userProfileTagMapper.toEntity(request);
        UserProfileTag saved = executeOrFail(() -> userProfileTagRepository.save(userProfileTag), "Falha ao associar tag ao usuario");
        return userProfileTagMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long userId, Long profileTagId) {
        ensureTagAssigned(userId, profileTagId);
        executeOrFail(() -> {
            userProfileTagRepository.deleteByUserIdAndProfileTagId(userId, profileTagId);
            return null;
        }, "Falha ao remover tag do usuario");
    }

    private void ensureTagNotAssigned(Long userId, Long profileTagId) {
        if (isTagAssigned(userId, profileTagId)) {
            throw new DuplicateResourceException("A tag " + profileTagId + " ja esta associada ao usuario " + userId);
        }
    }

    private void ensureTagAssigned(Long userId, Long profileTagId) {
        if (!isTagAssigned(userId, profileTagId)) {
            throw new ResourceNotFoundException("A tag " + profileTagId + " nao esta associada ao usuario " + userId);
        }
    }

    private boolean isTagAssigned(Long userId, Long profileTagId) {
        return executeOrFail(() -> userProfileTagRepository.existsByUserIdAndProfileTagId(userId, profileTagId),
                "Falha ao verificar tag ja associada");
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
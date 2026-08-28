package com.venus.crud.service.user;

import com.venus.crud.dto.request.user.UserPreferencePatchRequest;
import com.venus.crud.dto.request.user.UserPreferenceRequest;
import com.venus.crud.dto.response.user.UserPreferenceResponse;
import com.venus.crud.entity.user.UserPreference;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.user.UserPreferenceMapper;
import com.venus.crud.repository.jpa.user.UserPreferenceRepository;
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
public class UserPreferenceService {

    private static final Logger log = LoggerFactory.getLogger(UserPreferenceService.class);

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserPreferenceMapper userPreferenceMapper;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository, UserPreferenceMapper userPreferenceMapper) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userPreferenceMapper = userPreferenceMapper;
    }

    @Transactional(readOnly = true)
    public List<UserPreferenceResponse> findAll() {
        return executeOrFail(userPreferenceRepository::findAll, "Falha ao consultar preferencias de usuario no banco de dados").stream()
                .map(userPreferenceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserPreferenceResponse findByUserId(Long userId) {
        return userPreferenceMapper.toResponse(getOrThrow(userId));
    }

    @Transactional(readOnly = true)
    public Slice<UserPreferenceResponse> search(Boolean preferCrueltyFree, Boolean preferVegan, Boolean preferSustainable,
            Boolean preferFragranceFree, Boolean preferParabenFree, Boolean preferSulfateFree, Boolean preferSiliconeFree,
            Pageable pageable) {
        Slice<UserPreference> result;
        if (Boolean.TRUE.equals(preferVegan)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferVeganTrue(pageable), "Falha ao consultar preferencias veganas");
        } else if (Boolean.TRUE.equals(preferCrueltyFree)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferCrueltyFreeTrue(pageable), "Falha ao consultar preferencias cruelty-free");
        } else if (Boolean.TRUE.equals(preferSustainable)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferSustainableTrue(pageable), "Falha ao consultar preferencias sustentaveis");
        } else if (Boolean.TRUE.equals(preferFragranceFree)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferFragranceFreeTrue(pageable), "Falha ao consultar preferencias sem fragrancia");
        } else if (Boolean.TRUE.equals(preferParabenFree)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferParabenFreeTrue(pageable), "Falha ao consultar preferencias sem parabeno");
        } else if (Boolean.TRUE.equals(preferSulfateFree)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferSulfateFreeTrue(pageable), "Falha ao consultar preferencias sem sulfato");
        } else if (Boolean.TRUE.equals(preferSiliconeFree)) {
            result = executeOrFail(() -> userPreferenceRepository.findByPreferSiliconeFreeTrue(pageable), "Falha ao consultar preferencias sem silicone");
        } else {
            result = executeOrFail(() -> userPreferenceRepository.findAllBy(pageable), "Falha ao consultar preferencias de usuario");
        }

        return result.map(userPreferenceMapper::toResponse);
    }

    @Transactional
    public UserPreferenceResponse create(UserPreferenceRequest request) {
        ensurePreferenceNotSet(request.userId());

        UserPreference preference = userPreferenceMapper.toEntity(request);
        UserPreference saved = executeOrFail(() -> userPreferenceRepository.save(preference), "Falha ao criar preferencias de usuario no banco de dados");
        return userPreferenceMapper.toResponse(saved);
    }

    @Transactional
    public UserPreferenceResponse update(Long userId, UserPreferenceRequest request) {
        UserPreference preference = getOrThrow(userId);
        userPreferenceMapper.updateEntity(request, preference);

        UserPreference saved = executeOrFail(() -> userPreferenceRepository.save(preference), "Falha ao atualizar preferencias de usuario no banco de dados");
        return userPreferenceMapper.toResponse(saved);
    }

    @Transactional
    public UserPreferenceResponse patch(Long userId, UserPreferencePatchRequest request) {
        UserPreference preference = getOrThrow(userId);
        userPreferenceMapper.patchEntity(request, preference);

        UserPreference saved = executeOrFail(() -> userPreferenceRepository.save(preference), "Falha ao atualizar preferencias de usuario no banco de dados");
        return userPreferenceMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long userId) {
        getOrThrow(userId);
        executeOrFail(() -> {
            userPreferenceRepository.deleteByUserId(userId);
            return null;
        }, "Falha ao remover preferencias de usuario no banco de dados");
    }

    private UserPreference getOrThrow(Long userId) {
        var preference = executeOrFail(() -> userPreferenceRepository.findByUserId(userId), "Falha ao consultar preferencias de usuario no banco de dados");
        return preference.orElseThrow(() -> new ResourceNotFoundException("Preferencias nao encontradas para o usuario com id " + userId));
    }

    private void ensurePreferenceNotSet(Long userId) {
        boolean exists = executeOrFail(() -> userPreferenceRepository.existsByUserId(userId), "Falha ao verificar preferencias existentes");
        if (exists) {
            throw new DuplicateResourceException("Ja existem preferencias cadastradas para o usuario com id " + userId);
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
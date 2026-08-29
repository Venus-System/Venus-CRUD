package com.venus.crud.service.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserAllergyPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserAllergyRequest;
import com.venus.crud.dto.jpa.response.user.UserAllergyResponse;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.user.UserAllergy;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.user.UserAllergyMapper;
import com.venus.crud.repository.jpa.user.UserAllergyRepository;
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
public class UserAllergyService {

    private static final Logger log = LoggerFactory.getLogger(UserAllergyService.class);

    private final UserAllergyRepository userAllergyRepository;
    private final UserAllergyMapper userAllergyMapper;

    public UserAllergyService(UserAllergyRepository userAllergyRepository, UserAllergyMapper userAllergyMapper) {
        this.userAllergyRepository = userAllergyRepository;
        this.userAllergyMapper = userAllergyMapper;
    }

    @Transactional(readOnly = true)
    public List<UserAllergyResponse> findAll() {
        return executeOrFail(userAllergyRepository::findAll, "Falha ao consultar alergias de usuario no banco de dados").stream()
                .map(userAllergyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<UserAllergyResponse> findByUserId(Long userId, RiskLevel severity, Pageable pageable) {
        Slice<UserAllergy> result = severity != null
                ? executeOrFail(() -> userAllergyRepository.findByUserIdAndSeverity(userId, severity, pageable), "Falha ao consultar alergias do usuario por gravidade")
                : executeOrFail(() -> userAllergyRepository.findByUserId(userId, pageable), "Falha ao consultar alergias do usuario");

        return result.map(userAllergyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<UserAllergyResponse> findByAllergyId(Long allergyId, Pageable pageable) {
        return executeOrFail(() -> userAllergyRepository.findByAllergyId(allergyId, pageable), "Falha ao consultar usuarios com a alergia")
                .map(userAllergyMapper::toResponse);
    }

    @Transactional
    public UserAllergyResponse create(UserAllergyRequest request) {
        ensureAllergyNotAssigned(request.userId(), request.allergyId());

        UserAllergy userAllergy = userAllergyMapper.toEntity(request);
        UserAllergy saved = executeOrFail(() -> userAllergyRepository.save(userAllergy), "Falha ao associar alergia ao usuario");
        return userAllergyMapper.toResponse(saved);
    }

    @Transactional
    public UserAllergyResponse patch(Long userId, Long allergyId, UserAllergyPatchRequest request) {
        UserAllergy userAllergy = getOrThrow(userId, allergyId);
        userAllergyMapper.patchEntity(request, userAllergy);

        UserAllergy saved = executeOrFail(() -> userAllergyRepository.save(userAllergy), "Falha ao atualizar alergia do usuario");
        return userAllergyMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long userId, Long allergyId) {
        getOrThrow(userId, allergyId);
        executeOrFail(() -> {
            userAllergyRepository.deleteByUserIdAndAllergyId(userId, allergyId);
            return null;
        }, "Falha ao remover alergia do usuario");
    }

    private UserAllergy getOrThrow(Long userId, Long allergyId) {
        var userAllergy = executeOrFail(() -> userAllergyRepository.findByUserIdAndAllergyId(userId, allergyId),
                "Falha ao consultar alergia do usuario");
        return userAllergy.orElseThrow(
                () -> new ResourceNotFoundException("A alergia " + allergyId + " nao esta associada ao usuario " + userId));
    }

    private void ensureAllergyNotAssigned(Long userId, Long allergyId) {
        boolean exists = executeOrFail(() -> userAllergyRepository.existsByUserIdAndAllergyId(userId, allergyId),
                "Falha ao verificar alergia ja associada");
        if (exists) {
            throw new DuplicateResourceException("A alergia " + allergyId + " ja esta associada ao usuario " + userId);
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
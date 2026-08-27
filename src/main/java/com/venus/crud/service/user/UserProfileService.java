package com.venus.crud.service.user;

import com.venus.crud.dto.request.user.UserProfilePatchRequest;
import com.venus.crud.dto.request.user.UserProfileRequest;
import com.venus.crud.dto.response.user.UserProfileResponse;
import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.entity.user.UserProfile;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.user.UserProfileMapper;
import com.venus.crud.repository.jpa.user.UserProfileRepository;
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
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> findAll() {
        return executeOrFail(userProfileRepository::findAll, "Falha ao consultar perfis de usuario no banco de dados").stream()
                .map(userProfileMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findByUserId(Long userId) {
        return userProfileMapper.toResponse(getOrThrow(userId));
    }

    @Transactional
    public UserProfileResponse create(UserProfileRequest request) {
        ensureProfileAvailable(request.userId());

        UserProfile profile = userProfileMapper.toEntity(request);
        UserProfile saved = executeOrFail(() -> userProfileRepository.save(profile), "Falha ao criar perfil de usuario no banco de dados");
        return userProfileMapper.toResponse(saved);
    }

    @Transactional
    public UserProfileResponse update(Long userId, UserProfileRequest request) {
        UserProfile profile = getOrThrow(userId);
        userProfileMapper.updateEntity(request, profile);

        UserProfile saved = executeOrFail(() -> userProfileRepository.save(profile), "Falha ao atualizar perfil de usuario no banco de dados");
        return userProfileMapper.toResponse(saved);
    }

    @Transactional
    public UserProfileResponse patch(Long userId, UserProfilePatchRequest request) {
        UserProfile profile = getOrThrow(userId);
        userProfileMapper.patchEntity(request, profile);

        UserProfile saved = executeOrFail(() -> userProfileRepository.save(profile), "Falha ao atualizar perfil de usuario no banco de dados");
        return userProfileMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Slice<UserProfileResponse> search(SkinType skinType, HairType hairType, SensitivityLevel skinSensitivity,
            Boolean acneProne, Boolean isPregnant, AgeRange ageRange, Gender gender, Pageable pageable) {
        boolean hasSkinType = skinType != null;
        boolean hasHairType = hairType != null;
        boolean hasSkinSensitivity = skinSensitivity != null;
        boolean hasAcneProne = Boolean.TRUE.equals(acneProne);
        boolean hasPregnant = Boolean.TRUE.equals(isPregnant);
        boolean hasAgeRange = ageRange != null;
        boolean hasGender = gender != null;

        Slice<UserProfile> result;
        if (hasAgeRange && hasGender) {
            result = executeOrFail(() -> userProfileRepository.findByAgeRangeAndGender(ageRange, gender, pageable),
                    "Falha ao consultar perfis por faixa etaria e genero");
        } else if (hasSkinType) {
            result = executeOrFail(() -> userProfileRepository.findBySkinType(skinType, pageable), "Falha ao consultar perfis por tipo de pele");
        } else if (hasHairType) {
            result = executeOrFail(() -> userProfileRepository.findByHairType(hairType, pageable), "Falha ao consultar perfis por tipo de cabelo");
        } else if (hasSkinSensitivity) {
            result = executeOrFail(() -> userProfileRepository.findBySkinSensitivity(skinSensitivity, pageable),
                    "Falha ao consultar perfis por sensibilidade de pele");
        } else if (hasAcneProne) {
            result = executeOrFail(() -> userProfileRepository.findByAcneProneTrue(pageable), "Falha ao consultar perfis com propensao a acne");
        } else if (hasPregnant) {
            result = executeOrFail(() -> userProfileRepository.findByIsPregnantTrue(pageable), "Falha ao consultar perfis de usuarias gravidas");
        } else {
            result = executeOrFail(() -> userProfileRepository.findAllBy(pageable), "Falha ao consultar perfis");
        }

        return result.map(userProfileMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long countBySkinType(SkinType skinType) {
        return executeOrFail(() -> userProfileRepository.countBySkinType(skinType), "Falha ao contar perfis por tipo de pele");
    }

    @Transactional(readOnly = true)
    public long countByAcneProne() {
        return executeOrFail(userProfileRepository::countByAcneProneTrue, "Falha ao contar perfis com propensao a acne");
    }

    @Transactional
    public void delete(Long userId) {
        getOrThrow(userId);
        executeOrFail(() -> {
            userProfileRepository.deleteByUserId(userId);
            return null;
        }, "Falha ao remover perfil de usuario no banco de dados");
    }

    private UserProfile getOrThrow(Long userId) {
        var profile = executeOrFail(() -> userProfileRepository.findByUserId(userId), "Falha ao consultar perfil de usuario no banco de dados");
        return profile.orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado para o usuario com id " + userId));
    }

    private void ensureProfileAvailable(Long userId) {
        boolean exists = executeOrFail(() -> userProfileRepository.existsByUserId(userId), "Falha ao verificar perfil existente");
        if (exists) {
            throw new DuplicateResourceException("Ja existe um perfil cadastrado para o usuario com id " + userId);
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
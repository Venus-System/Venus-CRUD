package com.venus.crud.service.fullstage;

import com.venus.crud.dto.response.fullstage.UserFullProfileResponse;
import com.venus.crud.dto.response.user.UserProfileResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.shared.ProfileTagMapper;
import com.venus.crud.mapper.user.UserMapper;
import com.venus.crud.mapper.user.UserProfileMapper;
import com.venus.crud.repository.jpa.user.UserProfileRepository;
import com.venus.crud.repository.jpa.user.UserProfileTagRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFullProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserFullProfileService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileTagRepository userProfileTagRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final ProfileTagMapper profileTagMapper;

    public UserFullProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository,
            UserProfileTagRepository userProfileTagRepository, UserMapper userMapper,
            UserProfileMapper userProfileMapper, ProfileTagMapper profileTagMapper) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileTagRepository = userProfileTagRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.profileTagMapper = profileTagMapper;
    }

    @Transactional(readOnly = true)
    public UserFullProfileResponse findByUserId(Long userId) {
        User user = executeOrFail(() -> userRepository.findById(userId), "Falha ao consultar usuario no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id " + userId));

        UserProfileResponse profile = executeOrFail(() -> userProfileRepository.findByUserId(userId), "Falha ao consultar perfil de usuario no banco de dados")
                .map(userProfileMapper::toResponse)
                .orElse(null);

        var tags = executeOrFail(() -> userProfileTagRepository.findByUserId(userId), "Falha ao consultar tags do usuario")
                .stream()
                .map(userProfileTag -> profileTagMapper.toResponse(userProfileTag.getProfileTag()))
                .toList();

        return new UserFullProfileResponse(userMapper.toResponse(user), profile, tags);
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
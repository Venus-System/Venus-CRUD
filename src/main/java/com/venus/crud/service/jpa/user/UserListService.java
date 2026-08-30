package com.venus.crud.service.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserListPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListRequest;
import com.venus.crud.dto.jpa.response.user.UserListResponse;
import com.venus.crud.entity.enums.ListType;
import com.venus.crud.entity.user.UserList;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.user.UserListMapper;
import com.venus.crud.repository.jpa.user.UserListRepository;
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

@Service
public class UserListService {

    private static final Logger log = LoggerFactory.getLogger(UserListService.class);

    private final UserListRepository userListRepository;
    private final UserListMapper userListMapper;

    public UserListService(UserListRepository userListRepository, UserListMapper userListMapper) {
        this.userListRepository = userListRepository;
        this.userListMapper = userListMapper;
    }

    @Transactional(readOnly = true)
    public List<UserListResponse> findAll() {
        return executeOrFail(userListRepository::findAll, "Falha ao consultar listas de usuario no banco de dados").stream()
                .map(userListMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserListResponse findById(Long id) {
        return userListMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<UserListResponse> findByUser(Long userId, ListType listType, Pageable pageable) {
        Slice<UserList> result = listType != null
                ? executeOrFail(() -> userListRepository.findByUserIdAndListType(userId, listType, pageable), "Falha ao consultar listas do usuario por tipo")
                : executeOrFail(() -> userListRepository.findByUserId(userId, pageable), "Falha ao consultar listas do usuario");

        return result.map(userListMapper::toResponse);
    }

    @Transactional
    public UserListResponse create(UserListRequest request) {
        ensureNameAvailable(request.userId(), request.name(), null);

        UserList userList = userListMapper.toEntity(request);
        UserList saved = executeOrFail(() -> userListRepository.save(userList), "Falha ao criar lista de usuario no banco de dados");
        return userListMapper.toResponse(saved);
    }

    @Transactional
    public UserListResponse update(Long id, UserListRequest request) {
        UserList userList = getOrThrow(id);
        ensureNameAvailable(request.userId(), request.name(), id);

        userListMapper.updateEntity(request, userList);
        UserList saved = executeOrFail(() -> userListRepository.save(userList), "Falha ao atualizar lista de usuario no banco de dados");
        return userListMapper.toResponse(saved);
    }

    @Transactional
    public UserListResponse patch(Long id, UserListPatchRequest request) {
        UserList userList = getOrThrow(id);
        if (request.name() != null) {
            Long userId = request.userId() != null ? request.userId() : userList.getUser().getId();
            ensureNameAvailable(userId, request.name(), id);
        }

        userListMapper.patchEntity(request, userList);
        UserList saved = executeOrFail(() -> userListRepository.save(userList), "Falha ao atualizar lista de usuario no banco de dados");
        return userListMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        UserList userList = getOrThrow(id);
        executeOrFail(() -> {
            userListRepository.delete(userList);
            return null;
        }, "Falha ao remover lista de usuario no banco de dados");
    }

    private UserList getOrThrow(Long id) {
        Optional<UserList> userList = executeOrFail(() -> userListRepository.findById(id), "Falha ao consultar lista de usuario no banco de dados");
        return userList.orElseThrow(() -> new ResourceNotFoundException("Lista de usuario nao encontrada com id " + id));
    }

    private void ensureNameAvailable(Long userId, String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> userListRepository.findByUserIdAndName(userId, name), "Falha ao verificar nome da lista")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("O usuario " + userId + " ja tem uma lista com o nome " + name);
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
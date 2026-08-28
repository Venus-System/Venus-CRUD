package com.venus.crud.service.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserListItemPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListItemRequest;
import com.venus.crud.dto.jpa.response.user.UserListItemResponse;
import com.venus.crud.entity.user.UserListItem;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.user.UserListItemMapper;
import com.venus.crud.repository.jpa.user.UserListItemRepository;
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
public class UserListItemService {

    private static final Logger log = LoggerFactory.getLogger(UserListItemService.class);

    private final UserListItemRepository userListItemRepository;
    private final UserListItemMapper userListItemMapper;

    public UserListItemService(UserListItemRepository userListItemRepository, UserListItemMapper userListItemMapper) {
        this.userListItemRepository = userListItemRepository;
        this.userListItemMapper = userListItemMapper;
    }

    @Transactional(readOnly = true)
    public List<UserListItemResponse> findAll() {
        return executeOrFail(userListItemRepository::findAll, "Falha ao consultar itens de lista no banco de dados").stream()
                .map(userListItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserListItemResponse> findByUserListId(Long userListId) {
        return executeOrFail(() -> userListItemRepository.findByUserListIdOrderByPositionOrder(userListId), "Falha ao consultar itens da lista")
                .stream()
                .map(userListItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<UserListItemResponse> findByProductId(Long productId, Pageable pageable) {
        return executeOrFail(() -> userListItemRepository.findByProductId(productId, pageable), "Falha ao consultar listas com o produto")
                .map(userListItemMapper::toResponse);
    }

    @Transactional
    public UserListItemResponse create(UserListItemRequest request) {
        ensureProductNotInList(request.userListId(), request.productId());

        UserListItem userListItem = userListItemMapper.toEntity(request);
        UserListItem saved = executeOrFail(() -> userListItemRepository.save(userListItem), "Falha ao adicionar item na lista");
        return userListItemMapper.toResponse(saved);
    }

    @Transactional
    public UserListItemResponse patch(Long userListId, Long productId, UserListItemPatchRequest request) {
        UserListItem userListItem = getOrThrow(userListId, productId);
        userListItemMapper.patchEntity(request, userListItem);

        UserListItem saved = executeOrFail(() -> userListItemRepository.save(userListItem), "Falha ao atualizar item da lista");
        return userListItemMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long userListId, Long productId) {
        getOrThrow(userListId, productId);
        executeOrFail(() -> {
            userListItemRepository.deleteByUserListIdAndProductId(userListId, productId);
            return null;
        }, "Falha ao remover item da lista");
    }

    private UserListItem getOrThrow(Long userListId, Long productId) {
        var userListItem = executeOrFail(() -> userListItemRepository.findByUserListIdAndProductId(userListId, productId),
                "Falha ao consultar item da lista");
        return userListItem.orElseThrow(
                () -> new ResourceNotFoundException("O produto " + productId + " nao esta na lista " + userListId));
    }

    private void ensureProductNotInList(Long userListId, Long productId) {
        boolean exists = executeOrFail(() -> userListItemRepository.existsByUserListIdAndProductId(userListId, productId),
                "Falha ao verificar item ja existente na lista");
        if (exists) {
            throw new DuplicateResourceException("O produto " + productId + " ja esta na lista " + userListId);
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
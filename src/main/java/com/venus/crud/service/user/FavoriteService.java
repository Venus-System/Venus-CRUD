package com.venus.crud.service.user;

import com.venus.crud.dto.request.user.FavoriteRequest;
import com.venus.crud.dto.response.user.FavoriteResponse;
import com.venus.crud.entity.user.Favorite;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.user.FavoriteMapper;
import com.venus.crud.repository.jpa.user.FavoriteRepository;
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
public class FavoriteService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> findAll() {
        return executeOrFail(favoriteRepository::findAll, "Falha ao consultar favoritos no banco de dados").stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<FavoriteResponse> findByUserId(Long userId, Pageable pageable) {
        return executeOrFail(() -> favoriteRepository.findByUserId(userId, pageable), "Falha ao consultar favoritos do usuario")
                .map(favoriteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<FavoriteResponse> findByProductId(Long productId, Pageable pageable) {
        return executeOrFail(() -> favoriteRepository.findByProductId(productId, pageable), "Falha ao consultar usuarios que favoritaram o produto")
                .map(favoriteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long countByProductId(Long productId) {
        return executeOrFail(() -> favoriteRepository.countByProductId(productId), "Falha ao contar favoritos do produto");
    }

    @Transactional
    public FavoriteResponse create(FavoriteRequest request) {
        ensureProductNotFavorited(request.userId(), request.productId());

        Favorite favorite = favoriteMapper.toEntity(request);
        Favorite saved = executeOrFail(() -> favoriteRepository.save(favorite), "Falha ao favoritar produto");
        return favoriteMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long userId, Long productId) {
        ensureProductFavorited(userId, productId);
        executeOrFail(() -> {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
            return null;
        }, "Falha ao remover favorito");
    }

    private void ensureProductNotFavorited(Long userId, Long productId) {
        if (isProductFavorited(userId, productId)) {
            throw new DuplicateResourceException("O produto " + productId + " ja esta favoritado pelo usuario " + userId);
        }
    }

    private void ensureProductFavorited(Long userId, Long productId) {
        if (!isProductFavorited(userId, productId)) {
            throw new ResourceNotFoundException("O produto " + productId + " nao esta favoritado pelo usuario " + userId);
        }
    }

    private boolean isProductFavorited(Long userId, Long productId) {
        return executeOrFail(() -> favoriteRepository.existsByUserIdAndProductId(userId, productId), "Falha ao verificar favorito existente");
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
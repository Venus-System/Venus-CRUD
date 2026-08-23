package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @EntityGraph(attributePaths = "product")
    Slice<Favorite> findByUserId(Long userId, Pageable pageable);
    Page<Favorite> findByProductId(Long productId, Pageable pageable);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    long countByProductId(Long productId);
    long countByUserId(Long userId);

    @Query("SELECT f.product.id FROM Favorite f WHERE f.user.id = :userId AND f.product.id IN :productIds")
    Set<Long> findFavoritedProductIds(@Param("userId") Long userId, @Param("productIds") Collection<Long> productIds);
}

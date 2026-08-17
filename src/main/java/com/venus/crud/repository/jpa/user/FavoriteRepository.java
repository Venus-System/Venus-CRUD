package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @EntityGraph(attributePaths = "product")
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    Page<Favorite> findByProductId(Long productId, Pageable pageable);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    long countByProductId(Long productId);
}

package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Slice<Favorite> findByUserId(Long userId, Pageable pageable);
    Slice<Favorite> findByProductId(Long productId, Pageable pageable);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    long countByProductId(Long productId);
}

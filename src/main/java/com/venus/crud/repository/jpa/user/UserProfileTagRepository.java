package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserProfileTag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileTagRepository extends JpaRepository<UserProfileTag, Long> {

    @EntityGraph(attributePaths = "profileTag")
    Slice<UserProfileTag> findByUserId(Long userId, Pageable pageable);
    @EntityGraph(attributePaths = "profileTag")
    List<UserProfileTag> findByUserId(Long userId);
    Slice<UserProfileTag> findByProfileTagId(Long profileTagId, Pageable pageable);
    boolean existsByUserIdAndProfileTagId(Long userId, Long profileTagId);
    void deleteByUserIdAndProfileTagId(Long userId, Long profileTagId);
}

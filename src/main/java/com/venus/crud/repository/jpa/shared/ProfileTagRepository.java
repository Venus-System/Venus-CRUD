package com.venus.crud.repository.jpa.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import com.venus.crud.entity.shared.ProfileTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileTagRepository extends JpaRepository<ProfileTag, Long> {

    Optional<ProfileTag> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<ProfileTag> findByCategory(ProfileTagCategory category, Pageable pageable);
}

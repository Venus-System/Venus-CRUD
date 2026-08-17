package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Page<UserPreference> findByPreferVeganTrue(Pageable pageable);
    Page<UserPreference> findByPreferCrueltyFreeTrue(Pageable pageable);
    Page<UserPreference> findByPreferSustainableTrue(Pageable pageable);
    Page<UserPreference> findByPreferFragranceFreeTrue(Pageable pageable);
    Page<UserPreference> findByPreferParabenFreeTrue(Pageable pageable);
    Page<UserPreference> findByPreferSulfateFreeTrue(Pageable pageable);
    Page<UserPreference> findByPreferSiliconeFreeTrue(Pageable pageable);
}

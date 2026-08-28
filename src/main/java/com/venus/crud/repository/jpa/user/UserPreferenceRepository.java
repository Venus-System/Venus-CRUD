package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserPreference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
    Slice<UserPreference> findByPreferVeganTrue(Pageable pageable);
    Slice<UserPreference> findByPreferCrueltyFreeTrue(Pageable pageable);
    Slice<UserPreference> findByPreferSustainableTrue(Pageable pageable);
    Slice<UserPreference> findByPreferFragranceFreeTrue(Pageable pageable);
    Slice<UserPreference> findByPreferParabenFreeTrue(Pageable pageable);
    Slice<UserPreference> findByPreferSulfateFreeTrue(Pageable pageable);
    Slice<UserPreference> findByPreferSiliconeFreeTrue(Pageable pageable);
    Slice<UserPreference> findAllBy(Pageable pageable);
}

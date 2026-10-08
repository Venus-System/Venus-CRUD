package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserPreference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);

    @Query("""
            select userPreference from UserPreference userPreference
            where (:preferCrueltyFree is null or userPreference.preferCrueltyFree = :preferCrueltyFree)
              and (:preferVegan is null or userPreference.preferVegan = :preferVegan)
              and (:preferSustainable is null or userPreference.preferSustainable = :preferSustainable)
              and (:preferFragranceFree is null or userPreference.preferFragranceFree = :preferFragranceFree)
              and (:preferParabenFree is null or userPreference.preferParabenFree = :preferParabenFree)
              and (:preferSulfateFree is null or userPreference.preferSulfateFree = :preferSulfateFree)
              and (:preferSiliconeFree is null or userPreference.preferSiliconeFree = :preferSiliconeFree)
            """)
    Slice<UserPreference> search(Boolean preferCrueltyFree, Boolean preferVegan, Boolean preferSustainable,
            Boolean preferFragranceFree, Boolean preferParabenFree, Boolean preferSulfateFree,
            Boolean preferSiliconeFree, Pageable pageable);
}

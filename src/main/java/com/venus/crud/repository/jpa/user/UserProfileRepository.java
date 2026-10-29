package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairPattern;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.entity.user.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);

    @Query("""
            select userProfile from UserProfile userProfile
            where (cast(:skinType as String) is null or userProfile.skinType = :skinType)
              and (cast(:hairPattern as String) is null or userProfile.hairPattern = :hairPattern)
              and (cast(:skinSensitivity as String) is null or userProfile.skinSensitivity = :skinSensitivity)
              and (:acneProne is null or userProfile.acneProne = :acneProne)
              and (:isPregnant is null or userProfile.isPregnant = :isPregnant)
              and (:isBreastfeeding is null or userProfile.isBreastfeeding = :isBreastfeeding)
              and (cast(:ageRange as String) is null or userProfile.ageRange = :ageRange)
              and (cast(:gender as String) is null or userProfile.gender = :gender)
            """)
    Slice<UserProfile> search(SkinType skinType, HairPattern hairPattern, SensitivityLevel skinSensitivity,
            Boolean acneProne, Boolean isPregnant, Boolean isBreastfeeding, AgeRange ageRange, Gender gender,
            Pageable pageable);

    long countBySkinType(SkinType skinType);
    long countByAcneProneTrue();
}

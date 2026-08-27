package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.entity.user.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
    Slice<UserProfile> findBySkinType(SkinType skinType, Pageable pageable);
    Slice<UserProfile> findByHairType(HairType hairType, Pageable pageable);
    Slice<UserProfile> findBySkinSensitivity(SensitivityLevel skinSensitivity, Pageable pageable);
    Slice<UserProfile> findByAcneProneTrue(Pageable pageable);
    Slice<UserProfile> findByIsPregnantTrue(Pageable pageable);
    Slice<UserProfile> findByAgeRangeAndGender(AgeRange ageRange, Gender gender, Pageable pageable);
    Slice<UserProfile> findAllBy(Pageable pageable);
    long countBySkinType(SkinType skinType);
    long countByAcneProneTrue();
}

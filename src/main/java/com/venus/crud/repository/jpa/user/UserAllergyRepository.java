package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.user.UserAllergy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {

    @EntityGraph(attributePaths = "allergy")
    List<UserAllergy> findByUserId(Long userId);
    Slice<UserAllergy> findByUserId(Long userId, Pageable pageable);
    Slice<UserAllergy> findByAllergyId(Long allergyId, Pageable pageable);
    Optional<UserAllergy> findByUserIdAndAllergyId(Long userId, Long allergyId);
    boolean existsByUserIdAndAllergyId(Long userId, Long allergyId);
    void deleteByUserIdAndAllergyId(Long userId, Long allergyId);
    Slice<UserAllergy> findByUserIdAndSeverity(Long userId, RiskLevel severity, Pageable pageable);
}

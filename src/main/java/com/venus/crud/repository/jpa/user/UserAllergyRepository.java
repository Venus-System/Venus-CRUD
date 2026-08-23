package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.user.UserAllergy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {

    @EntityGraph(attributePaths = "allergy")
    List<UserAllergy> findByUserId(Long userId);
    Page<UserAllergy> findByAllergyId(Long allergyId, Pageable pageable);
    boolean existsByUserIdAndAllergyId(Long userId, Long allergyId);
    void deleteByUserIdAndAllergyId(Long userId, Long allergyId);
    List<UserAllergy> findByUserIdAndSeverity(Long userId, RiskLevel severity);
}

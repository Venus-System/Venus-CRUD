package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.entity.scan.AnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @EntityGraph(attributePaths = "productVersion")
    Optional<AnalysisResult> findByScanSessionId(Long scanSessionId);
    Page<AnalysisResult> findByUserId(Long userId, Pageable pageable);
    Page<AnalysisResult> findByUserIdAndProductVersionId(Long userId, Long productVersionId, Pageable pageable);
    Page<AnalysisResult> findByStatus(AnalysisStatus status, Pageable pageable);
    Page<AnalysisResult> findByOverallScoreGreaterThanEqual(Integer overallScore, Pageable pageable);
}

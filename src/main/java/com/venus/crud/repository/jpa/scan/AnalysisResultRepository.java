package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.entity.scan.AnalysisResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @EntityGraph(attributePaths = "productVersion")
    Slice<AnalysisResult> findByUserId(Long userId, Pageable pageable);
    Slice<AnalysisResult> findByUserIdAndProductVersionId(Long userId, Long productVersionId, Pageable pageable);
    Slice<AnalysisResult> findByStatus(AnalysisStatus status, Pageable pageable);
    Slice<AnalysisResult> findByOverallScoreGreaterThanEqual(Integer overallScore, Pageable pageable);
    Slice<AnalysisResult> findAllBy(Pageable pageable);
}
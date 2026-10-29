package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.entity.scan.AnalysisResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @EntityGraph(attributePaths = "productVersion")
    Slice<AnalysisResult> findByUserId(Long userId, Pageable pageable);
    Slice<AnalysisResult> findByUserIdAndProductVersionId(Long userId, Long productVersionId, Pageable pageable);

    @Query("""
            select analysisResult from AnalysisResult analysisResult
            where (cast(:status as String) is null or analysisResult.status = :status)
              and (:minOverallScore is null or analysisResult.overallScore >= :minOverallScore)
            """)
    Slice<AnalysisResult> search(AnalysisStatus status, Integer minOverallScore, Pageable pageable);
}
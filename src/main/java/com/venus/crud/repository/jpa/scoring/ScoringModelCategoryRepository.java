package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.scoring.ScoringModelCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoringModelCategoryRepository extends JpaRepository<ScoringModelCategory, Long> {

    List<ScoringModelCategory> findByScoringModelId(Long scoringModelId);
    Slice<ScoringModelCategory> findByScoreCategoryId(Long scoreCategoryId, Pageable pageable);
    Optional<ScoringModelCategory> findByScoringModelIdAndScoreCategoryId(Long scoringModelId, Long scoreCategoryId);
    boolean existsByScoringModelIdAndScoreCategoryId(Long scoringModelId, Long scoreCategoryId);
    void deleteByScoringModelIdAndScoreCategoryId(Long scoringModelId, Long scoreCategoryId);
    Slice<ScoringModelCategory> findAllBy(Pageable pageable);
}

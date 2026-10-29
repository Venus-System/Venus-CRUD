package com.venus.crud.repository.jpa.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.entity.review.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
            select report from Report report
            where (:userId is null or report.user.id = :userId)
              and (cast(:status as String) is null or report.status = :status)
              and (cast(:targetType as String) is null or report.targetType = :targetType)
              and (:targetId is null or report.targetId = :targetId)
              and (:adminUserId is null or report.adminUser.id = :adminUserId)
            """)
    Slice<Report> search(Long userId, ReportStatus status, ReportTargetType targetType, Long targetId,
            Long adminUserId, Pageable pageable);
}
package com.venus.crud.repository.jpa.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.entity.review.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Slice<Report> findByUserId(Long userId, Pageable pageable);
    Slice<Report> findByStatus(ReportStatus status, Pageable pageable);
    Slice<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId, Pageable pageable);
    Slice<Report> findByAdminUserId(Long adminUserId, Pageable pageable);
    Slice<Report> findAllBy(Pageable pageable);
}
package com.venus.crud.repository.jpa.scan;

import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.ScanType;
import com.venus.crud.entity.scan.ScanSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanSessionRepository extends JpaRepository<ScanSession, Long> {

    Page<ScanSession> findByUserId(Long userId, Pageable pageable);
    Page<ScanSession> findByUserIdAndStatus(Long userId, ScanStatus status, Pageable pageable);
    Page<ScanSession> findByScanType(ScanType scanType, Pageable pageable);
}

package com.venus.crud.repository.mongo;

import com.venus.crud.document.ScanSession;
import com.venus.crud.entity.enums.ScanStatus;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanSessionRepository extends MongoRepository<ScanSession, String> {

    Optional<ScanSession> findByScanId(String scanId);
    Slice<ScanSession> findAllBy(Pageable pageable);
    Slice<ScanSession> findByStatus(ScanStatus status, Pageable pageable);
    Slice<ScanSession> findByDevice_DeviceId(String deviceId, Pageable pageable);
}

package com.venus.crud.repository.mongo;

import com.venus.crud.document.ScanSession;
import com.venus.crud.entity.enums.AnalysisStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanSessionRepository extends MongoRepository<ScanSession, String> {

    Slice<ScanSession> findAllBy(Pageable pageable);
    Slice<ScanSession> findByStatus(AnalysisStatus status, Pageable pageable);
    Slice<ScanSession> findByDeviceDeviceId(String deviceId, Pageable pageable);
}

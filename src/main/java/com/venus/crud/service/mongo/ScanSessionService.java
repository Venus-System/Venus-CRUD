package com.venus.crud.service.mongo;

import com.venus.crud.document.ScanSession;
import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.mongo.ScanSessionMapper;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import java.util.function.Supplier;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScanSessionService {

    private static final Logger log = LoggerFactory.getLogger(ScanSessionService.class);

    private final ScanSessionRepository scanSessionRepository;
    private final ScanSessionMapper scanSessionMapper;

    public ScanSessionService(ScanSessionRepository scanSessionRepository, ScanSessionMapper scanSessionMapper) {
        this.scanSessionRepository = scanSessionRepository;
        this.scanSessionMapper = scanSessionMapper;
    }

    @Transactional(readOnly = true)
    public Slice<ScanSessionResponse> findAll(Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findAllBy(pageable), "Falha ao consultar sessoes de scan")
                .map(scanSessionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ScanSessionResponse findById(String id) {
        return scanSessionMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ScanSessionResponse> findByStatus(AnalysisStatus status, Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findByStatus(status, pageable), "Falha ao consultar sessoes de scan por status")
                .map(scanSessionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<ScanSessionResponse> findByDeviceId(String deviceId, Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findByDevice_DeviceId(deviceId, pageable),
                "Falha ao consultar sessoes de scan por dispositivo")
                .map(scanSessionMapper::toResponse);
    }


    @Transactional
    public ScanSessionResponse create(ScanSessionRequest request) {
        ScanSession scanSession = scanSessionMapper.toEntity(request);
        ScanSession saved = executeOrFail(() -> scanSessionRepository.save(scanSession),
                "Falha ao criar sessao de scan no banco de dados");
        return scanSessionMapper.toResponse(saved);
    }

    private ScanSession getOrThrow(String id) {
        if (!ObjectId.isValid(id)) {
            throw new ResourceNotFoundException("Sessao de scan nao encontrada com id " + id);
        }
        return executeOrFail(() -> scanSessionRepository.findById(id), "Falha ao consultar sessao de scan")
                .orElseThrow(() -> new ResourceNotFoundException("Sessao de scan nao encontrada com id " + id));
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violacao de integridade de dados: {}", ex.getMessage());
            throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }
}

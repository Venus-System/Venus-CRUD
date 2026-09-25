package com.venus.crud.service.mongo;

import com.venus.crud.document.ScanImage;
import com.venus.crud.document.ScanImages;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSource;
import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.user.User;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.mongo.ScanSessionMapper;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import java.util.Optional;
import java.util.function.Supplier;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class ScanSessionService {

    private static final Logger log = LoggerFactory.getLogger(ScanSessionService.class);

    private final ScanSessionRepository scanSessionRepository;
    private final ScanSessionMapper scanSessionMapper;
    private final ScanSessionRequestValidator scanSessionRequestValidator;
    private final IngredientMatcher ingredientMatcher;
    private final ScanCloudinaryService scanCloudinaryService;
    private final UserRepository userRepository;

    public ScanSessionService(ScanSessionRepository scanSessionRepository, ScanSessionMapper scanSessionMapper,
            ScanSessionRequestValidator scanSessionRequestValidator, IngredientMatcher ingredientMatcher,
            ScanCloudinaryService scanCloudinaryService, UserRepository userRepository) {
        this.scanSessionRepository = scanSessionRepository;
        this.scanSessionMapper = scanSessionMapper;
        this.scanSessionRequestValidator = scanSessionRequestValidator;
        this.ingredientMatcher = ingredientMatcher;
        this.scanCloudinaryService = scanCloudinaryService;
        this.userRepository = userRepository;
    }

    public Slice<ScanSessionResponse> findAll(Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findAllBy(pageable), "Falha ao consultar sessoes de scan")
                .map(scanSessionMapper::toResponse);
    }

    public ScanSessionResponse findById(String id) {
        return scanSessionMapper.toResponse(getOrThrow(id));
    }

    public Slice<ScanSessionResponse> findByStatus(ScanStatus status, Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findByStatus(status, pageable), "Falha ao consultar sessoes de scan por status")
                .map(scanSessionMapper::toResponse);
    }

    public Slice<ScanSessionResponse> findByDeviceId(String deviceId, Pageable pageable) {
        return executeOrFail(() -> scanSessionRepository.findByDevice_DeviceId(deviceId, pageable),
                "Falha ao consultar sessoes de scan por dispositivo")
                .map(scanSessionMapper::toResponse);
    }

    public ScanSessionResponse create(ScanSessionRequest request) {
        scanSessionRequestValidator.validate(request);

        Optional<ScanSession> existing = findByScanId(request.scanId().toString());
        if (existing.isPresent()) {
            return scanSessionMapper.toResponse(existing.get());
        }

        User user = getUserOrThrow(request.firebaseUid());
        ScanSession scanSession = scanSessionMapper.toEntity(request);
        scanSession.setStatus(ScanStatus.PENDING_REVIEW);
        scanSession.setSource(toSource(user));
        fillSecureUrls(scanSession.getImages());
        ingredientMatcher.matchAll(scanSession.getIngredients());

        return scanSessionMapper.toResponse(insertOrReturnExisting(scanSession));
    }

    private ScanSession insertOrReturnExisting(ScanSession scanSession) {
        try {
            return scanSessionRepository.insert(scanSession);
        } catch (DuplicateKeyException ex) {
            return findByScanId(scanSession.getScanId())
                    .orElseThrow(() -> DataIntegrityViolationTranslator.translate(ex));
        } catch (DataIntegrityViolationException ex) {
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error("Falha ao criar sessao de scan no banco de dados", ex);
            throw DataAccessFailureTranslator.translate(ex, "Falha ao criar sessao de scan no banco de dados");
        }
    }

    private Optional<ScanSession> findByScanId(String scanId) {
        return executeOrFail(() -> scanSessionRepository.findByScanId(scanId), "Falha ao consultar sessao de scan pelo scanId");
    }

    private User getUserOrThrow(String firebaseUid) {
        return executeOrFail(() -> userRepository.findByFirebaseUid(firebaseUid), "Falha ao consultar usuario pelo firebaseUid")
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com firebaseUid " + firebaseUid));
    }

    private ScanSource toSource(User user) {
        ScanSource source = new ScanSource();
        source.setUserId(user.getId());
        source.setFirebaseUid(user.getFirebaseUid());
        return source;
    }

    private void fillSecureUrls(ScanImages images) {
        if (images == null) {
            return;
        }
        fillSecureUrl(images.getFront());
        fillSecureUrl(images.getBack());
    }

    private void fillSecureUrl(ScanImage image) {
        if (image != null) {
            image.setSecureUrl(scanCloudinaryService.secureUrlFor(image.getPublicId()));
        }
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
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}

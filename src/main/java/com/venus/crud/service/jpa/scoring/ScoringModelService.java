package com.venus.crud.service.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoringModelPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import com.venus.crud.entity.scoring.ScoringModel;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ScoringModelMapper;
import com.venus.crud.repository.jpa.scoring.ScoringModelRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ScoringModelService {

    private static final Logger log = LoggerFactory.getLogger(ScoringModelService.class);

    private final ScoringModelRepository scoringModelRepository;
    private final ScoringModelMapper scoringModelMapper;

    public ScoringModelService(ScoringModelRepository scoringModelRepository, ScoringModelMapper scoringModelMapper) {
        this.scoringModelRepository = scoringModelRepository;
        this.scoringModelMapper = scoringModelMapper;
    }

    @Transactional(readOnly = true)
    public List<ScoringModelResponse> findAll() {
        return executeOrFail(scoringModelRepository::findAll, "Falha ao consultar modelos de scoring no banco de dados").stream()
                .map(scoringModelMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScoringModelResponse findById(Long id) {
        return scoringModelMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public ScoringModelResponse findActive() {
        return executeOrFail(scoringModelRepository::findByIsActiveTrue, "Falha ao consultar modelo de scoring ativo")
                .map(scoringModelMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum modelo de scoring ativo encontrado"));
    }

    @Transactional(readOnly = true)
    public Slice<ScoringModelResponse> search(String name, Pageable pageable) {
        Slice<ScoringModel> result = StringUtils.hasText(name)
                ? executeOrFail(() -> scoringModelRepository.findByNameContainingIgnoreCase(name, pageable),
                        "Falha ao consultar modelos de scoring por nome")
                : executeOrFail(() -> scoringModelRepository.findAllBy(pageable), "Falha ao consultar modelos de scoring");

        return result.map(scoringModelMapper::toResponse);
    }

    @Transactional
    public ScoringModelResponse create(ScoringModelRequest request) {
        ensureNameAndVersionAvailable(request.name(), request.version(), null);

        ScoringModel scoringModel = scoringModelMapper.toEntity(request);
        ScoringModel saved = executeOrFail(() -> scoringModelRepository.save(scoringModel),
                "Falha ao criar modelo de scoring no banco de dados");
        return scoringModelMapper.toResponse(saved);
    }

    @Transactional
    public ScoringModelResponse update(Long id, ScoringModelRequest request) {
        ScoringModel scoringModel = getOrThrow(id);
        ensureNameAndVersionAvailable(request.name(), request.version(), id);

        scoringModelMapper.updateEntity(request, scoringModel);
        ScoringModel saved = executeOrFail(() -> scoringModelRepository.save(scoringModel),
                "Falha ao atualizar modelo de scoring no banco de dados");
        return scoringModelMapper.toResponse(saved);
    }

    @Transactional
    public ScoringModelResponse patch(Long id, ScoringModelPatchRequest request) {
        ScoringModel scoringModel = getOrThrow(id);
        String name = StringUtils.hasText(request.name()) ? request.name() : scoringModel.getName();
        String version = StringUtils.hasText(request.version()) ? request.version() : scoringModel.getVersion();
        if (StringUtils.hasText(request.name()) || StringUtils.hasText(request.version())) {
            ensureNameAndVersionAvailable(name, version, id);
        }

        scoringModelMapper.patchEntity(request, scoringModel);
        ScoringModel saved = executeOrFail(() -> scoringModelRepository.save(scoringModel),
                "Falha ao atualizar modelo de scoring no banco de dados");
        return scoringModelMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ScoringModel scoringModel = getOrThrow(id);
        executeOrFail(() -> {
            scoringModelRepository.delete(scoringModel);
            return null;
        }, "Falha ao remover modelo de scoring no banco de dados");
    }

    private ScoringModel getOrThrow(Long id) {
        Optional<ScoringModel> scoringModel = executeOrFail(() -> scoringModelRepository.findById(id),
                "Falha ao consultar modelo de scoring no banco de dados");
        return scoringModel.orElseThrow(() -> new ResourceNotFoundException("Modelo de scoring nao encontrado com id " + id));
    }

    private void ensureNameAndVersionAvailable(String name, String version, Long excludeId) {
        boolean inUse = executeOrFail(() -> scoringModelRepository.findByNameAndVersion(name, version),
                "Falha ao verificar nome e versao do modelo de scoring")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um modelo de scoring " + name + " na versao " + version);
        }
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
package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.RegulationPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.RegulationRequest;
import com.venus.crud.dto.jpa.response.ingredient.RegulationResponse;
import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.ingredient.Regulation;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.RegulationMapper;
import com.venus.crud.repository.jpa.ingredient.RegulationRepository;
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
public class RegulationService {

    private static final Logger log = LoggerFactory.getLogger(RegulationService.class);

    private final RegulationRepository regulationRepository;
    private final RegulationMapper regulationMapper;

    public RegulationService(RegulationRepository regulationRepository, RegulationMapper regulationMapper) {
        this.regulationRepository = regulationRepository;
        this.regulationMapper = regulationMapper;
    }

    @Transactional(readOnly = true)
    public List<RegulationResponse> findAll() {
        return executeOrFail(regulationRepository::findAll, "Falha ao consultar regulamentacoes no banco de dados").stream()
                .map(regulationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegulationResponse findById(Long id) {
        return regulationMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<RegulationResponse> search(String title, String country, String agency, RegulationStatus status, Pageable pageable) {
        Slice<Regulation> result;
        if (StringUtils.hasText(title)) {
            result = executeOrFail(() -> regulationRepository.findByTitleContainingIgnoreCase(title, pageable),
                    "Falha ao consultar regulamentacoes por titulo");
        } else if (StringUtils.hasText(country)) {
            result = executeOrFail(() -> regulationRepository.findByCountry(country, pageable), "Falha ao consultar regulamentacoes por pais");
        } else if (StringUtils.hasText(agency)) {
            result = executeOrFail(() -> regulationRepository.findByAgency(agency, pageable), "Falha ao consultar regulamentacoes por agencia");
        } else if (status != null) {
            result = executeOrFail(() -> regulationRepository.findByStatus(status, pageable), "Falha ao consultar regulamentacoes por status");
        } else {
            result = executeOrFail(() -> regulationRepository.findAllBy(pageable), "Falha ao consultar regulamentacoes");
        }

        return result.map(regulationMapper::toResponse);
    }

    @Transactional
    public RegulationResponse create(RegulationRequest request) {
        ensureDocumentUrlAvailable(request.documentUrl(), null);

        Regulation regulation = regulationMapper.toEntity(request);
        Regulation saved = executeOrFail(() -> regulationRepository.save(regulation), "Falha ao criar regulamentacao no banco de dados");
        return regulationMapper.toResponse(saved);
    }

    @Transactional
    public RegulationResponse update(Long id, RegulationRequest request) {
        Regulation regulation = getOrThrow(id);
        ensureDocumentUrlAvailable(request.documentUrl(), id);

        regulationMapper.updateEntity(request, regulation);
        Regulation saved = executeOrFail(() -> regulationRepository.save(regulation), "Falha ao atualizar regulamentacao no banco de dados");
        return regulationMapper.toResponse(saved);
    }

    @Transactional
    public RegulationResponse patch(Long id, RegulationPatchRequest request) {
        Regulation regulation = getOrThrow(id);
        if (StringUtils.hasText(request.documentUrl())) {
            ensureDocumentUrlAvailable(request.documentUrl(), id);
        }

        regulationMapper.patchEntity(request, regulation);
        Regulation saved = executeOrFail(() -> regulationRepository.save(regulation), "Falha ao atualizar regulamentacao no banco de dados");
        return regulationMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Regulation regulation = getOrThrow(id);
        executeOrFail(() -> {
            regulationRepository.delete(regulation);
            return null;
        }, "Falha ao remover regulamentacao no banco de dados");
    }

    private Regulation getOrThrow(Long id) {
        Optional<Regulation> regulation = executeOrFail(() -> regulationRepository.findById(id), "Falha ao consultar regulamentacao no banco de dados");
        return regulation.orElseThrow(() -> new ResourceNotFoundException("Regulamentacao nao encontrada com id " + id));
    }

    private void ensureDocumentUrlAvailable(String documentUrl, Long excludeId) {
        boolean inUse = executeOrFail(() -> regulationRepository.findByDocumentUrl(documentUrl), "Falha ao verificar documento da regulamentacao")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma regulamentacao com o documento " + documentUrl);
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
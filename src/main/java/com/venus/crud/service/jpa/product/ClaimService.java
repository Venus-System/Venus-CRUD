package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ClaimPatchRequest;
import com.venus.crud.dto.jpa.request.product.ClaimRequest;
import com.venus.crud.dto.jpa.response.product.ClaimResponse;
import com.venus.crud.entity.enums.ClaimType;
import com.venus.crud.entity.product.Claim;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ClaimMapper;
import com.venus.crud.repository.jpa.product.ClaimRepository;
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
public class ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimService.class);

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;

    public ClaimService(ClaimRepository claimRepository, ClaimMapper claimMapper) {
        this.claimRepository = claimRepository;
        this.claimMapper = claimMapper;
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> findAll() {
        return executeOrFail(claimRepository::findAll, "Falha ao consultar claims no banco de dados").stream()
                .map(claimMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse findById(Long id) {
        return claimMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ClaimResponse> search(ClaimType claimType, Pageable pageable) {
        Slice<Claim> result = claimType != null
                ? executeOrFail(() -> claimRepository.findByClaimType(claimType, pageable), "Falha ao consultar claims por tipo")
                : executeOrFail(() -> claimRepository.findAllBy(pageable), "Falha ao consultar claims");

        return result.map(claimMapper::toResponse);
    }

    @Transactional
    public ClaimResponse create(ClaimRequest request) {
        ensureNameAvailable(request.name(), null);

        Claim claim = claimMapper.toEntity(request);
        Claim saved = executeOrFail(() -> claimRepository.save(claim), "Falha ao criar claim no banco de dados");
        return claimMapper.toResponse(saved);
    }

    @Transactional
    public ClaimResponse update(Long id, ClaimRequest request) {
        Claim claim = getOrThrow(id);
        ensureNameAvailable(request.name(), id);

        claimMapper.updateEntity(request, claim);
        Claim saved = executeOrFail(() -> claimRepository.save(claim), "Falha ao atualizar claim no banco de dados");
        return claimMapper.toResponse(saved);
    }

    @Transactional
    public ClaimResponse patch(Long id, ClaimPatchRequest request) {
        Claim claim = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }

        claimMapper.patchEntity(request, claim);
        Claim saved = executeOrFail(() -> claimRepository.save(claim), "Falha ao atualizar claim no banco de dados");
        return claimMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Claim claim = getOrThrow(id);
        executeOrFail(() -> {
            claimRepository.delete(claim);
            return null;
        }, "Falha ao remover claim no banco de dados");
    }

    private Claim getOrThrow(Long id) {
        Optional<Claim> claim = executeOrFail(() -> claimRepository.findById(id), "Falha ao consultar claim no banco de dados");
        return claim.orElseThrow(() -> new ResourceNotFoundException("Claim nao encontrado com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> claimRepository.findByNameIgnoreCase(name), "Falha ao verificar nome do claim")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um claim com o nome " + name);
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

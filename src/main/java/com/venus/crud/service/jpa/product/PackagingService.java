package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.PackagingPatchRequest;
import com.venus.crud.dto.jpa.request.product.PackagingRequest;
import com.venus.crud.dto.jpa.response.product.PackagingResponse;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.product.Packaging;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.PackagingMapper;
import com.venus.crud.repository.jpa.product.PackagingRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PackagingService {

    private static final Logger log = LoggerFactory.getLogger(PackagingService.class);

    private final PackagingRepository packagingRepository;
    private final PackagingMapper packagingMapper;

    public PackagingService(PackagingRepository packagingRepository, PackagingMapper packagingMapper) {
        this.packagingRepository = packagingRepository;
        this.packagingMapper = packagingMapper;
    }

    @Transactional(readOnly = true)
    public List<PackagingResponse> findAll() {
        return executeOrFail(packagingRepository::findAll, "Falha ao consultar embalagens no banco de dados").stream()
                .map(packagingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PackagingResponse findByProductVersionId(Long productVersionId) {
        return packagingMapper.toResponse(getOrThrow(productVersionId));
    }

    @Transactional(readOnly = true)
    public Slice<PackagingResponse> search(PackagingMaterial material, Boolean isRecyclable, Boolean isRefillable,
            Boolean isBiodegradable, Pageable pageable) {
        Slice<Packaging> result;
        if (material != null) {
            result = executeOrFail(() -> packagingRepository.findByMaterial(material, pageable), "Falha ao consultar embalagens por material");
        } else if (Boolean.TRUE.equals(isRecyclable)) {
            result = executeOrFail(() -> packagingRepository.findByIsRecyclableTrue(pageable), "Falha ao consultar embalagens reciclaveis");
        } else if (Boolean.TRUE.equals(isRefillable)) {
            result = executeOrFail(() -> packagingRepository.findByIsRefillableTrue(pageable), "Falha ao consultar embalagens reabastecíveis");
        } else if (Boolean.TRUE.equals(isBiodegradable)) {
            result = executeOrFail(() -> packagingRepository.findByIsBiodegradableTrue(pageable), "Falha ao consultar embalagens biodegradaveis");
        } else {
            result = executeOrFail(() -> packagingRepository.findAllBy(pageable), "Falha ao consultar embalagens");
        }

        return result.map(packagingMapper::toResponse);
    }

    @Transactional
    public PackagingResponse create(PackagingRequest request) {
        ensurePackagingNotSet(request.productVersionId());

        Packaging packaging = packagingMapper.toEntity(request);
        Packaging saved = executeOrFail(() -> packagingRepository.save(packaging), "Falha ao criar embalagem no banco de dados");
        return packagingMapper.toResponse(saved);
    }

    @Transactional
    public PackagingResponse update(Long productVersionId, PackagingRequest request) {
        Packaging packaging = getOrThrow(productVersionId);
        packagingMapper.updateEntity(request, packaging);

        Packaging saved = executeOrFail(() -> packagingRepository.save(packaging), "Falha ao atualizar embalagem no banco de dados");
        return packagingMapper.toResponse(saved);
    }

    @Transactional
    public PackagingResponse patch(Long productVersionId, PackagingPatchRequest request) {
        Packaging packaging = getOrThrow(productVersionId);
        packagingMapper.patchEntity(request, packaging);

        Packaging saved = executeOrFail(() -> packagingRepository.save(packaging), "Falha ao atualizar embalagem no banco de dados");
        return packagingMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long productVersionId) {
        getOrThrow(productVersionId);
        executeOrFail(() -> {
            packagingRepository.deleteByProductVersionId(productVersionId);
            return null;
        }, "Falha ao remover embalagem no banco de dados");
    }

    private Packaging getOrThrow(Long productVersionId) {
        var packaging = executeOrFail(() -> packagingRepository.findByProductVersionId(productVersionId), "Falha ao consultar embalagem no banco de dados");
        return packaging.orElseThrow(() -> new ResourceNotFoundException("Embalagem nao encontrada para a versao de produto com id " + productVersionId));
    }

    private void ensurePackagingNotSet(Long productVersionId) {
        boolean exists = executeOrFail(() -> packagingRepository.existsByProductVersionId(productVersionId), "Falha ao verificar embalagem existente");
        if (exists) {
            throw new DuplicateResourceException("Ja existe uma embalagem cadastrada para a versao de produto com id " + productVersionId);
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

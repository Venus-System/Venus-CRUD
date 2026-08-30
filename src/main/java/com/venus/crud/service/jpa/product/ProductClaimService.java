package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductClaimPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductClaimRequest;
import com.venus.crud.dto.jpa.response.product.ProductClaimResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductClaim;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductClaimMapper;
import com.venus.crud.repository.jpa.product.ProductClaimRepository;
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
public class ProductClaimService {

    private static final Logger log = LoggerFactory.getLogger(ProductClaimService.class);

    private final ProductClaimRepository productClaimRepository;
    private final ProductClaimMapper productClaimMapper;

    public ProductClaimService(ProductClaimRepository productClaimRepository, ProductClaimMapper productClaimMapper) {
        this.productClaimRepository = productClaimRepository;
        this.productClaimMapper = productClaimMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductClaimResponse> findAll() {
        return executeOrFail(productClaimRepository::findAll, "Falha ao consultar claims de produto no banco de dados").stream()
                .map(productClaimMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductClaimResponse> findByProductVersionId(Long productVersionId, Boolean wasVerified) {
        List<ProductClaim> result = Boolean.TRUE.equals(wasVerified)
                ? executeOrFail(() -> productClaimRepository.findByProductVersionIdAndWasVerifiedTrue(productVersionId), "Falha ao consultar claims verificados da versao de produto")
                : executeOrFail(() -> productClaimRepository.findByProductVersionId(productVersionId), "Falha ao consultar claims da versao de produto");

        return result.stream().map(productClaimMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Slice<ProductClaimResponse> search(Long claimId, SourceType sourceType, Pageable pageable) {
        Slice<ProductClaim> result;
        if (claimId != null) {
            result = executeOrFail(() -> productClaimRepository.findByClaimId(claimId, pageable), "Falha ao consultar claims de produto por claim");
        } else if (sourceType != null) {
            result = executeOrFail(() -> productClaimRepository.findBySourceType(sourceType, pageable), "Falha ao consultar claims de produto por origem");
        } else {
            result = executeOrFail(() -> productClaimRepository.findAllBy(pageable), "Falha ao consultar claims de produto");
        }

        return result.map(productClaimMapper::toResponse);
    }

    @Transactional
    public ProductClaimResponse create(ProductClaimRequest request) {
        ensureClaimNotAssigned(request.productVersionId(), request.claimId());

        ProductClaim productClaim = productClaimMapper.toEntity(request);
        ProductClaim saved = executeOrFail(() -> productClaimRepository.save(productClaim), "Falha ao associar claim a versao de produto");
        return productClaimMapper.toResponse(saved);
    }

    @Transactional
    public ProductClaimResponse patch(Long productVersionId, Long claimId, ProductClaimPatchRequest request) {
        ProductClaim productClaim = getOrThrow(productVersionId, claimId);
        productClaimMapper.patchEntity(request, productClaim);

        ProductClaim saved = executeOrFail(() -> productClaimRepository.save(productClaim), "Falha ao atualizar claim da versao de produto");
        return productClaimMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long productVersionId, Long claimId) {
        getOrThrow(productVersionId, claimId);
        executeOrFail(() -> {
            productClaimRepository.deleteByProductVersionIdAndClaimId(productVersionId, claimId);
            return null;
        }, "Falha ao remover claim da versao de produto");
    }

    private ProductClaim getOrThrow(Long productVersionId, Long claimId) {
        var productClaim = executeOrFail(() -> productClaimRepository.findByProductVersionIdAndClaimId(productVersionId, claimId),
                "Falha ao consultar claim da versao de produto");
        return productClaim.orElseThrow(
                () -> new ResourceNotFoundException("O claim " + claimId + " nao esta associado a versao de produto " + productVersionId));
    }

    private void ensureClaimNotAssigned(Long productVersionId, Long claimId) {
        boolean exists = executeOrFail(() -> productClaimRepository.existsByProductVersionIdAndClaimId(productVersionId, claimId),
                "Falha ao verificar claim ja associado");
        if (exists) {
            throw new DuplicateResourceException("O claim " + claimId + " ja esta associado a versao de produto " + productVersionId);
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

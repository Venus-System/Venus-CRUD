package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductVersionPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductVersionRequest;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductVersionMapper;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
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
public class ProductVersionService {

    private static final Logger log = LoggerFactory.getLogger(ProductVersionService.class);

    private final ProductVersionRepository productVersionRepository;
    private final ProductVersionMapper productVersionMapper;

    public ProductVersionService(ProductVersionRepository productVersionRepository, ProductVersionMapper productVersionMapper) {
        this.productVersionRepository = productVersionRepository;
        this.productVersionMapper = productVersionMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductVersionResponse> findAll() {
        return executeOrFail(productVersionRepository::findAll, "Falha ao consultar versoes de produto no banco de dados").stream()
                .map(productVersionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductVersionResponse findById(Long id) {
        return productVersionMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductVersionResponse> findByProductId(Long productId) {
        return executeOrFail(() -> productVersionRepository.findByProductId(productId), "Falha ao consultar versoes do produto").stream()
                .map(productVersionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductVersionResponse findCurrentByProductId(Long productId) {
        return executeOrFail(() -> productVersionRepository.findByProductIdAndIsCurrentTrue(productId), "Falha ao consultar versao atual do produto")
                .map(productVersionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma versao atual encontrada para o produto com id " + productId));
    }

    @Transactional(readOnly = true)
    public Slice<ProductVersionResponse> search(VersionStatus status, String formulaSignature, Pageable pageable) {
        Slice<ProductVersion> result;
        if (status != null) {
            result = executeOrFail(() -> productVersionRepository.findByStatus(status, pageable), "Falha ao consultar versoes por status");
        } else if (StringUtils.hasText(formulaSignature)) {
            result = executeOrFail(() -> productVersionRepository.findByFormulaSignature(formulaSignature, pageable),
                    "Falha ao consultar versoes por assinatura de formula");
        } else {
            result = executeOrFail(() -> productVersionRepository.findAllBy(pageable), "Falha ao consultar versoes de produto");
        }

        return result.map(productVersionMapper::toResponse);
    }

    @Transactional
    public ProductVersionResponse create(ProductVersionRequest request) {
        ProductVersion productVersion = productVersionMapper.toEntity(request);
        ProductVersion saved = executeOrFail(() -> productVersionRepository.save(productVersion), "Falha ao criar versao de produto no banco de dados");
        return productVersionMapper.toResponse(saved);
    }

    @Transactional
    public ProductVersionResponse update(Long id, ProductVersionRequest request) {
        ProductVersion productVersion = getOrThrow(id);
        productVersionMapper.updateEntity(request, productVersion);

        ProductVersion saved = executeOrFail(() -> productVersionRepository.save(productVersion), "Falha ao atualizar versao de produto no banco de dados");
        return productVersionMapper.toResponse(saved);
    }

    @Transactional
    public ProductVersionResponse patch(Long id, ProductVersionPatchRequest request) {
        ProductVersion productVersion = getOrThrow(id);
        productVersionMapper.patchEntity(request, productVersion);

        ProductVersion saved = executeOrFail(() -> productVersionRepository.save(productVersion), "Falha ao atualizar versao de produto no banco de dados");
        return productVersionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ProductVersion productVersion = getOrThrow(id);
        executeOrFail(() -> {
            productVersionRepository.delete(productVersion);
            return null;
        }, "Falha ao remover versao de produto no banco de dados");
    }

    private ProductVersion getOrThrow(Long id) {
        Optional<ProductVersion> productVersion = executeOrFail(() -> productVersionRepository.findById(id), "Falha ao consultar versao de produto no banco de dados");
        return productVersion.orElseThrow(() -> new ResourceNotFoundException("Versao de produto nao encontrada com id " + id));
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

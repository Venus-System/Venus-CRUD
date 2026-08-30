package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductLabelPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductLabelRequest;
import com.venus.crud.dto.jpa.response.product.ProductLabelResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductLabel;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductLabelMapper;
import com.venus.crud.repository.jpa.product.ProductLabelRepository;
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
import org.springframework.util.StringUtils;

@Service
public class ProductLabelService {

    private static final Logger log = LoggerFactory.getLogger(ProductLabelService.class);

    private final ProductLabelRepository productLabelRepository;
    private final ProductLabelMapper productLabelMapper;

    public ProductLabelService(ProductLabelRepository productLabelRepository, ProductLabelMapper productLabelMapper) {
        this.productLabelRepository = productLabelRepository;
        this.productLabelMapper = productLabelMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductLabelResponse> findAll() {
        return executeOrFail(productLabelRepository::findAll, "Falha ao consultar rotulos de produto no banco de dados").stream()
                .map(productLabelMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductLabelResponse findByProductVersionId(Long productVersionId) {
        return productLabelMapper.toResponse(getOrThrow(productVersionId));
    }

    @Transactional(readOnly = true)
    public ProductLabelResponse findBySourceReference(String sourceReference) {
        return executeOrFail(() -> productLabelRepository.findBySourceReference(sourceReference), "Falha ao consultar rotulo por referencia de origem")
                .map(productLabelMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Rotulo de produto nao encontrado com a referencia de origem " + sourceReference));
    }

    @Transactional(readOnly = true)
    public Slice<ProductLabelResponse> search(String language, SourceType sourceType, Pageable pageable) {
        Slice<ProductLabel> result;
        if (StringUtils.hasText(language)) {
            result = executeOrFail(() -> productLabelRepository.findByLanguage(language, pageable), "Falha ao consultar rotulos por idioma");
        } else if (sourceType != null) {
            result = executeOrFail(() -> productLabelRepository.findBySourceType(sourceType, pageable), "Falha ao consultar rotulos por origem");
        } else {
            result = executeOrFail(() -> productLabelRepository.findAllBy(pageable), "Falha ao consultar rotulos de produto");
        }

        return result.map(productLabelMapper::toResponse);
    }

    @Transactional
    public ProductLabelResponse create(ProductLabelRequest request) {
        ensureLabelNotSet(request.productVersionId());

        ProductLabel productLabel = productLabelMapper.toEntity(request);
        ProductLabel saved = executeOrFail(() -> productLabelRepository.save(productLabel), "Falha ao criar rotulo de produto no banco de dados");
        return productLabelMapper.toResponse(saved);
    }

    @Transactional
    public ProductLabelResponse update(Long productVersionId, ProductLabelRequest request) {
        ProductLabel productLabel = getOrThrow(productVersionId);
        productLabelMapper.updateEntity(request, productLabel);

        ProductLabel saved = executeOrFail(() -> productLabelRepository.save(productLabel), "Falha ao atualizar rotulo de produto no banco de dados");
        return productLabelMapper.toResponse(saved);
    }

    @Transactional
    public ProductLabelResponse patch(Long productVersionId, ProductLabelPatchRequest request) {
        ProductLabel productLabel = getOrThrow(productVersionId);
        productLabelMapper.patchEntity(request, productLabel);

        ProductLabel saved = executeOrFail(() -> productLabelRepository.save(productLabel), "Falha ao atualizar rotulo de produto no banco de dados");
        return productLabelMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long productVersionId) {
        getOrThrow(productVersionId);
        executeOrFail(() -> {
            productLabelRepository.deleteByProductVersionId(productVersionId);
            return null;
        }, "Falha ao remover rotulo de produto no banco de dados");
    }

    private ProductLabel getOrThrow(Long productVersionId) {
        var productLabel = executeOrFail(() -> productLabelRepository.findByProductVersionId(productVersionId), "Falha ao consultar rotulo de produto no banco de dados");
        return productLabel.orElseThrow(() -> new ResourceNotFoundException("Rotulo nao encontrado para a versao de produto com id " + productVersionId));
    }

    private void ensureLabelNotSet(Long productVersionId) {
        boolean exists = executeOrFail(() -> productLabelRepository.existsByProductVersionId(productVersionId), "Falha ao verificar rotulo existente");
        if (exists) {
            throw new DuplicateResourceException("Ja existe um rotulo cadastrado para a versao de produto com id " + productVersionId);
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

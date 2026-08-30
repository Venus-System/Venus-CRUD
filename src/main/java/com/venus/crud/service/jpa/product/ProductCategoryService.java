package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductCategoryRequest;
import com.venus.crud.dto.jpa.response.product.ProductCategoryResponse;
import com.venus.crud.entity.product.ProductCategory;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductCategoryMapper;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
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
public class ProductCategoryService {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryService.class);

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> findAll() {
        return executeOrFail(productCategoryRepository::findAll, "Falha ao consultar categorias no banco de dados").stream()
                .map(productCategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductCategoryResponse findById(Long id) {
        return productCategoryMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ProductCategoryResponse> search(String name, Pageable pageable) {
        Slice<ProductCategory> result = StringUtils.hasText(name)
                ? executeOrFail(() -> productCategoryRepository.findByNameContainingIgnoreCase(name, pageable), "Falha ao consultar categorias por nome")
                : executeOrFail(() -> productCategoryRepository.findAllBy(pageable), "Falha ao consultar categorias");

        return result.map(productCategoryMapper::toResponse);
    }

    @Transactional
    public ProductCategoryResponse create(ProductCategoryRequest request) {
        ensureNameAvailable(request.name(), null);

        ProductCategory productCategory = productCategoryMapper.toEntity(request);
        ProductCategory saved = executeOrFail(() -> productCategoryRepository.save(productCategory), "Falha ao criar categoria no banco de dados");
        return productCategoryMapper.toResponse(saved);
    }

    @Transactional
    public ProductCategoryResponse update(Long id, ProductCategoryRequest request) {
        ProductCategory productCategory = getOrThrow(id);
        ensureNameAvailable(request.name(), id);

        productCategoryMapper.updateEntity(request, productCategory);
        ProductCategory saved = executeOrFail(() -> productCategoryRepository.save(productCategory), "Falha ao atualizar categoria no banco de dados");
        return productCategoryMapper.toResponse(saved);
    }

    @Transactional
    public ProductCategoryResponse patch(Long id, ProductCategoryPatchRequest request) {
        ProductCategory productCategory = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }

        productCategoryMapper.patchEntity(request, productCategory);
        ProductCategory saved = executeOrFail(() -> productCategoryRepository.save(productCategory), "Falha ao atualizar categoria no banco de dados");
        return productCategoryMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ProductCategory productCategory = getOrThrow(id);
        executeOrFail(() -> {
            productCategoryRepository.delete(productCategory);
            return null;
        }, "Falha ao remover categoria no banco de dados");
    }

    private ProductCategory getOrThrow(Long id) {
        Optional<ProductCategory> productCategory = executeOrFail(() -> productCategoryRepository.findById(id), "Falha ao consultar categoria no banco de dados");
        return productCategory.orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> productCategoryRepository.findByNameIgnoreCase(name), "Falha ao verificar nome da categoria")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma categoria com o nome " + name);
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

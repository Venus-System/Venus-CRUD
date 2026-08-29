package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductRequest;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductMapper;
import com.venus.crud.repository.jpa.product.ProductRepository;
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
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return executeOrFail(productRepository::findAll, "Falha ao consultar produtos no banco de dados").stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ProductResponse> search(String name, Long brandId, Long productCategoryId, Boolean isActive, Pageable pageable) {
        boolean hasBrand = brandId != null;
        boolean hasCategory = productCategoryId != null;

        Slice<Product> result;
        if (hasBrand && hasCategory) {
            result = executeOrFail(() -> productRepository.findByBrandIdAndProductCategoryId(brandId, productCategoryId, pageable),
                    "Falha ao consultar produtos por marca e categoria");
        } else if (StringUtils.hasText(name)) {
            result = executeOrFail(() -> productRepository.findByNameContainingIgnoreCase(name, pageable), "Falha ao consultar produtos por nome");
        } else if (hasBrand) {
            result = executeOrFail(() -> productRepository.findByBrandId(brandId, pageable), "Falha ao consultar produtos por marca");
        } else if (hasCategory) {
            result = executeOrFail(() -> productRepository.findByProductCategoryId(productCategoryId, pageable), "Falha ao consultar produtos por categoria");
        } else if (Boolean.TRUE.equals(isActive)) {
            result = executeOrFail(() -> productRepository.findByIsActiveTrue(pageable), "Falha ao consultar produtos ativos");
        } else {
            result = executeOrFail(() -> productRepository.findAllBy(pageable), "Falha ao consultar produtos");
        }

        return result.map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        ensureSlugAvailable(request.slug(), null);

        Product product = productMapper.toEntity(request);
        Product saved = executeOrFail(() -> productRepository.save(product), "Falha ao criar produto no banco de dados");
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        ensureSlugAvailable(request.slug(), id);

        productMapper.updateEntity(request, product);
        Product saved = executeOrFail(() -> productRepository.save(product), "Falha ao atualizar produto no banco de dados");
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse patch(Long id, ProductPatchRequest request) {
        Product product = getOrThrow(id);
        if (StringUtils.hasText(request.slug())) {
            ensureSlugAvailable(request.slug(), id);
        }

        productMapper.patchEntity(request, product);
        Product saved = executeOrFail(() -> productRepository.save(product), "Falha ao atualizar produto no banco de dados");
        return productMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        executeOrFail(() -> {
            productRepository.delete(product);
            return null;
        }, "Falha ao remover produto no banco de dados");
    }

    private Product getOrThrow(Long id) {
        Optional<Product> product = executeOrFail(() -> productRepository.findById(id), "Falha ao consultar produto no banco de dados");
        return product.orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id " + id));
    }

    private void ensureSlugAvailable(String slug, Long excludeId) {
        boolean inUse = executeOrFail(() -> productRepository.findBySlug(slug), "Falha ao verificar slug do produto")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe um produto com o slug " + slug);
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

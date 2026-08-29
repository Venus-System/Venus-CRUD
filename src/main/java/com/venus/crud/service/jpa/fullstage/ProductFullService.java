package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductClaimDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.dto.jpa.response.product.PackagingResponse;
import com.venus.crud.dto.jpa.response.product.ProductCategoryResponse;
import com.venus.crud.dto.jpa.response.product.ProductLabelResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.BrandMapper;
import com.venus.crud.mapper.jpa.product.ClaimMapper;
import com.venus.crud.mapper.jpa.product.PackagingMapper;
import com.venus.crud.mapper.jpa.product.ProductCategoryMapper;
import com.venus.crud.mapper.jpa.product.ProductLabelMapper;
import com.venus.crud.mapper.jpa.product.ProductMapper;
import com.venus.crud.mapper.jpa.product.ProductVersionMapper;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.PackagingRepository;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
import com.venus.crud.repository.jpa.product.ProductClaimRepository;
import com.venus.crud.repository.jpa.product.ProductLabelRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductFullService {

    private static final Logger log = LoggerFactory.getLogger(ProductFullService.class);

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductVersionRepository productVersionRepository;
    private final PackagingRepository packagingRepository;
    private final ProductLabelRepository productLabelRepository;
    private final ProductClaimRepository productClaimRepository;
    private final ProductMapper productMapper;
    private final BrandMapper brandMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductVersionMapper productVersionMapper;
    private final PackagingMapper packagingMapper;
    private final ProductLabelMapper productLabelMapper;
    private final ClaimMapper claimMapper;

    public ProductFullService(ProductRepository productRepository, BrandRepository brandRepository,
            ProductCategoryRepository productCategoryRepository, ProductVersionRepository productVersionRepository,
            PackagingRepository packagingRepository, ProductLabelRepository productLabelRepository,
            ProductClaimRepository productClaimRepository, ProductMapper productMapper, BrandMapper brandMapper,
            ProductCategoryMapper productCategoryMapper, ProductVersionMapper productVersionMapper,
            PackagingMapper packagingMapper, ProductLabelMapper productLabelMapper, ClaimMapper claimMapper) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productVersionRepository = productVersionRepository;
        this.packagingRepository = packagingRepository;
        this.productLabelRepository = productLabelRepository;
        this.productClaimRepository = productClaimRepository;
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
        this.productCategoryMapper = productCategoryMapper;
        this.productVersionMapper = productVersionMapper;
        this.packagingMapper = packagingMapper;
        this.productLabelMapper = productLabelMapper;
        this.claimMapper = claimMapper;
    }

    @Transactional(readOnly = true)
    public ProductFullResponse findById(Long id) {
        Product product = executeOrFail(() -> productRepository.findById(id), "Falha ao consultar produto no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id " + id));

        BrandResponse brand = executeOrFail(() -> brandRepository.findById(product.getBrand().getId()), "Falha ao consultar marca do produto")
                .map(brandMapper::toResponse)
                .orElse(null);

        ProductCategoryResponse category = executeOrFail(() -> productCategoryRepository.findById(product.getProductCategory().getId()),
                "Falha ao consultar categoria do produto")
                .map(productCategoryMapper::toResponse)
                .orElse(null);

        ProductVersionResponse currentVersion = executeOrFail(() -> productVersionRepository.findByProductIdAndIsCurrentTrue(id),
                "Falha ao consultar versao atual do produto")
                .map(productVersionMapper::toResponse)
                .orElse(null);

        PackagingResponse packaging = null;
        ProductLabelResponse label = null;
        List<ProductClaimDetailResponse> claims = List.of();

        if (currentVersion != null) {
            Long versionId = currentVersion.id();

            packaging = executeOrFail(() -> packagingRepository.findByProductVersionId(versionId), "Falha ao consultar embalagem da versao atual")
                    .map(packagingMapper::toResponse)
                    .orElse(null);

            label = executeOrFail(() -> productLabelRepository.findByProductVersionId(versionId), "Falha ao consultar rotulo da versao atual")
                    .map(productLabelMapper::toResponse)
                    .orElse(null);

            claims = executeOrFail(() -> productClaimRepository.findByProductVersionId(versionId), "Falha ao consultar claims da versao atual")
                    .stream()
                    .map(productClaim -> new ProductClaimDetailResponse(
                            claimMapper.toResponse(productClaim.getClaim()),
                            productClaim.getWasVerified(),
                            productClaim.getVerifiedBy(),
                            productClaim.getVerifiedAt(),
                            productClaim.getSourceType(),
                            productClaim.getSourceReference()))
                    .toList();
        }

        return new ProductFullResponse(productMapper.toResponse(product), brand, category, currentVersion, packaging, label, claims);
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

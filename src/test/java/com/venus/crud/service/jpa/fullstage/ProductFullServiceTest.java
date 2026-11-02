package com.venus.crud.service.jpa.fullstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.response.fullstage.ProductClaimDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.Brand;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.product.ProductCategory;
import com.venus.crud.entity.product.ProductClaim;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.mapper.jpa.media.MediaAssetMapper;
import com.venus.crud.mapper.jpa.product.BrandMapper;
import com.venus.crud.mapper.jpa.product.ClaimMapper;
import com.venus.crud.mapper.jpa.product.PackagingMapper;
import com.venus.crud.mapper.jpa.product.ProductCategoryMapper;
import com.venus.crud.mapper.jpa.product.ProductLabelMapper;
import com.venus.crud.mapper.jpa.product.ProductMapper;
import com.venus.crud.mapper.jpa.product.ProductVersionMapper;
import com.venus.crud.mapper.jpa.scoring.ProductScoreMapper;
import com.venus.crud.repository.jpa.ingredient.ProductIngredientRepository;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.PackagingRepository;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
import com.venus.crud.repository.jpa.product.ProductClaimRepository;
import com.venus.crud.repository.jpa.product.ProductLabelRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.repository.jpa.scoring.ProductScoreRepository;
import com.venus.crud.repository.jpa.scoring.ScoringModelRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductFullServiceTest {

    private static final Long PRODUCT_ID = 15L;
    private static final Long BRAND_ID = 3L;
    private static final Long CATEGORY_ID = 5L;
    private static final Long VERSION_ID = 42L;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductVersionRepository productVersionRepository;

    @Mock
    private PackagingRepository packagingRepository;

    @Mock
    private ProductLabelRepository productLabelRepository;

    @Mock
    private ProductIngredientRepository productIngredientRepository;

    @Mock
    private ProductClaimRepository productClaimRepository;

    @Mock
    private ProductScoreRepository productScoreRepository;

    @Mock
    private ScoringModelRepository scoringModelRepository;

    @Mock
    private ProductScoreMapper productScoreMapper;

    @Mock
    private MediaAssetMapper mediaAssetMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BrandMapper brandMapper;

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Mock
    private ProductVersionMapper productVersionMapper;

    @Mock
    private PackagingMapper packagingMapper;

    @Mock
    private ProductLabelMapper productLabelMapper;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private ClaimMapper claimMapper;

    private ProductFullService service;

    @BeforeEach
    void setUp() {
        service = new ProductFullService(productRepository, mediaAssetRepository, brandRepository,
                productCategoryRepository, productVersionRepository, packagingRepository, productLabelRepository,
                productIngredientRepository, productClaimRepository, productScoreRepository, scoringModelRepository,
                productScoreMapper, mediaAssetMapper, productMapper, brandMapper, productCategoryMapper,
                productVersionMapper, packagingMapper, productLabelMapper, ingredientMapper, claimMapper);
    }

    @Test
    void missingProductGivesNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void productWithoutVersionSkipsVersionData() {
        mockProduct();
        when(productVersionRepository.findByProductIdAndIsCurrentTrue(PRODUCT_ID)).thenReturn(Optional.empty());

        ProductFullResponse response = service.findById(PRODUCT_ID);

        assertThat(response.currentVersion()).isNull();
        assertThat(response.photos()).isEmpty();
        assertThat(response.packaging()).isNull();
        assertThat(response.label()).isNull();
        assertThat(response.ingredients()).isEmpty();
        assertThat(response.claims()).isEmpty();
        assertThat(response.score()).isNull();
        verifyNoInteractions(mediaAssetRepository, packagingRepository, productLabelRepository,
                productIngredientRepository, productClaimRepository, scoringModelRepository, productScoreRepository);
    }

    @Test
    void productWithVersionReadsVersionData() {
        mockProduct();
        ProductVersion version = new ProductVersion();
        ProductVersionResponse versionResponse = new ProductVersionResponse(VERSION_ID, PRODUCT_ID, null, null, null,
                true, null, null, null, null, null, null);
        when(productVersionRepository.findByProductIdAndIsCurrentTrue(PRODUCT_ID)).thenReturn(Optional.of(version));
        when(productVersionMapper.toResponse(version)).thenReturn(versionResponse);

        ProductClaim verifiedClaim = new ProductClaim();
        verifiedClaim.setWasVerified(true);
        verifiedClaim.setSourceType(SourceType.OFFICIAL_SITE);
        verifiedClaim.setSourceReference("ANVISA-2024-0031");

        when(mediaAssetRepository.findByProductVersionIdAndPurposeAndStatusInOrderBySortOrderAscIdAsc(
                VERSION_ID, MediaPurpose.PRODUCT_PHOTO, MediaAssetRepository.LIVE_STATUSES)).thenReturn(List.of());
        when(packagingRepository.findByProductVersionId(VERSION_ID)).thenReturn(Optional.empty());
        when(productLabelRepository.findByProductVersionId(VERSION_ID)).thenReturn(Optional.empty());
        when(productIngredientRepository.findByProductVersionIdOrderByPosition(VERSION_ID)).thenReturn(List.of());
        when(productClaimRepository.findByProductVersionId(VERSION_ID)).thenReturn(List.of(verifiedClaim));
        when(scoringModelRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        ProductFullResponse response = service.findById(PRODUCT_ID);

        assertThat(response.currentVersion()).isEqualTo(versionResponse);
        assertThat(response.claims()).containsExactly(new ProductClaimDetailResponse(null, true, null, null,
                SourceType.OFFICIAL_SITE, "ANVISA-2024-0031"));
    }

    private void mockProduct() {
        Brand brand = new Brand();
        brand.setId(BRAND_ID);
        ProductCategory category = new ProductCategory();
        category.setId(CATEGORY_ID);
        Product product = new Product();
        product.setBrand(brand);
        product.setProductCategory(category);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(brandRepository.findById(BRAND_ID)).thenReturn(Optional.empty());
        when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());
    }
}

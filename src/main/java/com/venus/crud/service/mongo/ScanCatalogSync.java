package com.venus.crud.service.mongo;

import com.venus.crud.document.ScanApprovedSnapshot;
import com.venus.crud.document.ScanBackExtracted;
import com.venus.crud.document.ScanFrontExtracted;
import com.venus.crud.document.ScanImage;
import com.venus.crud.document.ScanImages;
import com.venus.crud.document.ScanIngredientDecision;
import com.venus.crud.document.ScanOcr;
import com.venus.crud.document.ScanOcrBack;
import com.venus.crud.document.ScanOcrFront;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSnapshotProduct;
import com.venus.crud.entity.enums.IngredientDecisionAction;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientCategory;
import com.venus.crud.entity.ingredient.ProductIngredient;
import com.venus.crud.entity.product.Brand;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.product.ProductCategory;
import com.venus.crud.entity.product.ProductLabel;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.repository.jpa.ingredient.IngredientCategoryRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import com.venus.crud.repository.jpa.ingredient.ProductIngredientRepository;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import com.venus.crud.repository.jpa.product.BrandRepository;
import com.venus.crud.repository.jpa.product.ProductCategoryRepository;
import com.venus.crud.repository.jpa.product.ProductLabelRepository;
import com.venus.crud.repository.jpa.product.ProductRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.service.jpa.media.CloudinaryUpload;
import com.venus.crud.service.jpa.media.MediaAssetWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ScanCatalogSync {

    private static final Logger log = LoggerFactory.getLogger(ScanCatalogSync.class);

    private static final String UNCLASSIFIED_CATEGORY = "Não classificado";
    private static final String LABEL_LANGUAGE = "pt-BR";
    private static final String SCAN_REFERENCE_PREFIX = "scan:";
    private static final String VERSION_NAME_PREFIX = "Scan de ";
    private static final DateTimeFormatter VERSION_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final short ZERO = 0;

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductVersionRepository productVersionRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final ProductLabelRepository productLabelRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetWriter mediaAssetWriter;

    public ScanCatalogSync(ProductRepository productRepository, BrandRepository brandRepository,
            ProductCategoryRepository productCategoryRepository, ProductVersionRepository productVersionRepository,
            IngredientRepository ingredientRepository, IngredientCategoryRepository ingredientCategoryRepository,
            ProductIngredientRepository productIngredientRepository, ProductLabelRepository productLabelRepository,
            MediaAssetRepository mediaAssetRepository, MediaAssetWriter mediaAssetWriter) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productVersionRepository = productVersionRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.productLabelRepository = productLabelRepository;
        this.mediaAssetRepository = mediaAssetRepository;
        this.mediaAssetWriter = mediaAssetWriter;
    }

    @Transactional
    public Result synchronize(ScanSession scanSession) {
        ScanApprovedSnapshot snapshot = scanSession.getApprovedSnapshot();
        if (snapshot == null) {
            throw new InvalidStateTransitionException("O scan nao tem aprovacao para sincronizar.");
        }
        String reference = SCAN_REFERENCE_PREFIX + scanSession.getScanId();
        Product product = resolveProduct(snapshot.getProduct());
        List<PositionedIngredient> ingredients = resolveIngredients(snapshot.getIngredients(), reference);
        String signature = ScanSyncKeys.formulaSignature(ingredients.stream().map(item -> item.ingredient().getId()).toList());

        Optional<ProductVersion> sameFormula = executeOrFail(
                () -> productVersionRepository.findByProductIdAndFormulaSignature(product.getId(), signature),
                "Falha ao consultar a versao do produto");
        if (sameFormula.isPresent()) {
            log.info("Scan {} reaproveitou a versao {} do produto {}", scanSession.getScanId(), sameFormula.get().getId(),
                    product.getId());
            return new Result(product.getId(), sameFormula.get().getId());
        }

        ProductVersion version = createVersion(product, signature, scanSession);
        linkIngredients(version, ingredients);
        createLabel(version, scanSession, reference);
        registerFrontPhoto(version, product, scanSession);
        return new Result(product.getId(), version.getId());
    }

    private Product resolveProduct(ScanSnapshotProduct snapshot) {
        if (snapshot.getProductId() != null) {
            return executeOrFail(() -> productRepository.findById(snapshot.getProductId()), "Falha ao consultar o produto")
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id " + snapshot.getProductId()));
        }
        Brand brand = executeOrFail(() -> brandRepository.findById(snapshot.getBrandId()), "Falha ao consultar a marca")
                .orElseThrow(() -> new ResourceNotFoundException("Marca nao encontrada com id " + snapshot.getBrandId()));
        String slug = ScanSyncKeys.slug(brand.getName(), snapshot.getName());
        Optional<Product> sameSlug = executeOrFail(() -> productRepository.findBySlug(slug), "Falha ao consultar o produto pelo slug");
        if (sameSlug.isPresent()) {
            return sameSlug.get();
        }
        ProductCategory category = executeOrFail(() -> productCategoryRepository.findById(snapshot.getProductCategoryId()),
                "Falha ao consultar a categoria do produto")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria de produto nao encontrada com id " + snapshot.getProductCategoryId()));

        Product product = new Product();
        product.setBrand(brand);
        product.setProductCategory(category);
        product.setName(snapshot.getName());
        product.setDescription("");
        product.setSlug(slug);
        product.setIsActive(true);
        return executeOrFail(() -> productRepository.save(product), "Falha ao criar o produto");
    }

    private List<PositionedIngredient> resolveIngredients(List<ScanIngredientDecision> decisions, String reference) {
        List<PositionedIngredient> resolved = new ArrayList<>();
        List<ScanIngredientDecision> ordered = decisions.stream()
                .sorted(Comparator.comparing(ScanIngredientDecision::getPosition))
                .toList();
        for (ScanIngredientDecision decision : ordered) {
            Ingredient ingredient = decision.getAction() == IngredientDecisionAction.CREATE
                    ? findOrCreateIngredient(decision.getInciName(), reference)
                    : executeOrFail(() -> ingredientRepository.findById(decision.getIngredientId()), "Falha ao consultar o ingrediente")
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Ingrediente nao encontrado com id " + decision.getIngredientId()));
            resolved.add(new PositionedIngredient(decision.getPosition(), ingredient));
        }
        return resolved;
    }

    private Ingredient findOrCreateIngredient(String inciName, String reference) {
        List<Ingredient> sameName = executeOrFail(
                () -> ingredientRepository.findByUpperInciNameIn(List.of(inciName.toUpperCase(Locale.ROOT))),
                "Falha ao consultar o ingrediente pelo nome");
        if (!sameName.isEmpty()) {
            return sameName.get(0);
        }
        IngredientCategory unclassified = executeOrFail(
                () -> ingredientCategoryRepository.findByNameIgnoreCase(UNCLASSIFIED_CATEGORY),
                "Falha ao consultar a categoria de ingrediente")
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de ingrediente 'Nao classificado' nao encontrada"));

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientCategory(unclassified);
        ingredient.setInciName(inciName);
        ingredient.setCommonName("");
        ingredient.setFunctionSummary("");
        ingredient.setDescription("");
        ingredient.setSafetySummary("");
        ingredient.setBiodegradabilityLevel(ZERO);
        ingredient.setIrritationRiskLevel(ZERO);
        ingredient.setComedogenicityScore(ZERO);
        ingredient.setEnvironmentalRiskLevel(ZERO);
        ingredient.setScientificConfidence(ZERO);
        ingredient.setSourceType(SourceType.ADMIN);
        ingredient.setSourceReference(reference);
        return executeOrFail(() -> ingredientRepository.save(ingredient), "Falha ao criar o ingrediente " + inciName);
    }

    private ProductVersion createVersion(Product product, String signature, ScanSession scanSession) {
        ProductVersion version = new ProductVersion();
        version.setProduct(product);
        version.setVersionName(VERSION_NAME_PREFIX + LocalDate.now().format(VERSION_DATE));
        version.setDisplayName(displayName(product, scanSession));
        version.setStatus(VersionStatus.VERIFIED);
        version.setIsCurrent(true);
        version.setFormulaSignature(signature);
        version.setDetectedBy(SourceType.OCR);
        version.setEffectiveFrom(LocalDate.now());
        return executeOrFail(() -> productVersionRepository.save(version), "Falha ao criar a versao do produto");
    }

    private String displayName(Product product, ScanSession scanSession) {
        return Optional.ofNullable(scanSession.getOcr())
                .map(ScanOcr::getFront)
                .map(ScanOcrFront::getExtracted)
                .map(ScanFrontExtracted::getCapacity)
                .filter(StringUtils::hasText)
                .map(capacity -> product.getName() + " " + capacity.trim())
                .orElse(product.getName());
    }

    private void linkIngredients(ProductVersion version, List<PositionedIngredient> ingredients) {
        List<ProductIngredient> links = ingredients.stream().map(item -> {
            ProductIngredient link = new ProductIngredient();
            link.setProductVersion(version);
            link.setIngredient(item.ingredient());
            link.setPosition(item.position());
            return link;
        }).toList();
        executeOrFail(() -> productIngredientRepository.saveAll(links), "Falha ao ligar os ingredientes a versao do produto");
    }

    private void createLabel(ProductVersion version, ScanSession scanSession, String reference) {
        String text = labelText(scanSession);
        if (text == null) {
            return;
        }
        ProductLabel label = new ProductLabel();
        label.setProductVersion(version);
        label.setNormalizedText(text);
        label.setLanguage(LABEL_LANGUAGE);
        label.setSourceType(SourceType.OCR);
        label.setSourceReference(reference);
        executeOrFail(() -> productLabelRepository.save(label), "Falha ao criar o rotulo da versao do produto");
    }

    private String labelText(ScanSession scanSession) {
        Optional<ScanOcrBack> back = Optional.ofNullable(scanSession.getOcr()).map(ScanOcr::getBack);
        return back.map(ScanOcrBack::getExtracted)
                .map(ScanBackExtracted::getIngredientsText)
                .filter(StringUtils::hasText)
                .or(() -> back.map(ScanOcrBack::getFullText).filter(StringUtils::hasText))
                .map(String::trim)
                .orElse(null);
    }

    private void registerFrontPhoto(ProductVersion version, Product product, ScanSession scanSession) {
        ScanImage front = Optional.ofNullable(scanSession.getImages()).map(ScanImages::getFront).orElse(null);
        if (front == null || !StringUtils.hasText(front.getPublicId())) {
            return;
        }
        boolean alreadyRegistered = Boolean.TRUE.equals(executeOrFail(
                () -> mediaAssetRepository.existsByPublicId(front.getPublicId()), "Falha ao consultar a foto do scan"));
        if (alreadyRegistered) {
            return;
        }
        CloudinaryUpload upload = new CloudinaryUpload(front.getPublicId(), null, null, front.getSecureUrl(),
                front.getFormat(), front.getWidth(), front.getHeight(), front.getBytes(), folderOf(front.getPublicId()));
        mediaAssetWriter.registerProductPhoto(version.getId(), upload, product.getName(), null, null);
    }

    private String folderOf(String publicId) {
        int lastSlash = publicId.lastIndexOf('/');
        return lastSlash > 0 ? publicId.substring(0, lastSlash) : null;
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }

    private record PositionedIngredient(Integer position, Ingredient ingredient) {
    }

    public record Result(Long productId, Long productVersionId) {
    }
}

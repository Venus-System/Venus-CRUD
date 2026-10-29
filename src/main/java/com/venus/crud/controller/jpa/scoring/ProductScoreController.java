package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ProductScorePatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ProductScoreRequest;
import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import com.venus.crud.service.jpa.scoring.ProductScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/product-scores")
@Tag(name = "Scores de Produto", description = "Nota geral do produto por categoria de score, com o agregado.")
public class ProductScoreController {

    private final ProductScoreService productScoreService;

    public ProductScoreController(ProductScoreService productScoreService) {
        this.productScoreService = productScoreService;
    }

    @Operation(operationId = "productScoreFindAll", summary = "Lista os scores de produto")
    @GetMapping
    public ResponseEntity<List<ProductScoreResponse>> findAll() {
        return ResponseEntity.ok(productScoreService.findAll());
    }

    @Operation(operationId = "productScoreSearch", summary = "Busca os scores de produto com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ProductScoreResponse>> search(
            @RequestParam(required = false) Integer minOverallScore,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productScoreService.search(minOverallScore, pageable));
    }

    @Operation(operationId = "productScoreFindByProductVersionId", summary = "Lista os scores de uma versão de produto")
    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<Slice<ProductScoreResponse>> findByProductVersionId(
            @PathVariable Long productVersionId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productScoreService.findByProductVersionId(productVersionId, pageable));
    }

    @Operation(
            operationId = "productScoreFindByProductVersionIdAndScoringModelId",
            summary = "Busca o score de uma versão de produto num modelo")
    @GetMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<ProductScoreResponse> findByProductVersionIdAndScoringModelId(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        return ResponseEntity.ok(productScoreService.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId));
    }

    @Operation(operationId = "productScoreCreate", summary = "Cadastra um score de produto")
    @PostMapping
    public ResponseEntity<ProductScoreResponse> create(@Valid @RequestBody ProductScoreRequest request) {
        ProductScoreResponse created = productScoreService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
                .buildAndExpand(created.productVersionId(), created.scoringModelId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "productScorePatch", summary = "Atualiza parcialmente o score de produto")
    @PatchMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<ProductScoreResponse> patch(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId,
            @Valid @RequestBody ProductScorePatchRequest request) {
        return ResponseEntity.ok(productScoreService.patch(productVersionId, scoringModelId, request));
    }

    @Operation(operationId = "productScoreDelete", summary = "Remove o score de produto")
    @DeleteMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        productScoreService.delete(productVersionId, scoringModelId);
        return ResponseEntity.noContent().build();
    }
}
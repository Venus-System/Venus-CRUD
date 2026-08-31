package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ProductScorePatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ProductScoreRequest;
import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import com.venus.crud.service.jpa.scoring.ProductScoreService;
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
public class ProductScoreController {

    private final ProductScoreService productScoreService;

    public ProductScoreController(ProductScoreService productScoreService) {
        this.productScoreService = productScoreService;
    }

    @GetMapping
    public ResponseEntity<List<ProductScoreResponse>> findAll() {
        return ResponseEntity.ok(productScoreService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ProductScoreResponse>> search(
            @RequestParam(required = false) Integer minOverallScore,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productScoreService.search(minOverallScore, pageable));
    }

    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<Slice<ProductScoreResponse>> findByProductVersionId(
            @PathVariable Long productVersionId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productScoreService.findByProductVersionId(productVersionId, pageable));
    }

    @GetMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<ProductScoreResponse> findByProductVersionIdAndScoringModelId(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        return ResponseEntity.ok(productScoreService.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId));
    }

    @PostMapping
    public ResponseEntity<ProductScoreResponse> create(@Valid @RequestBody ProductScoreRequest request) {
        ProductScoreResponse created = productScoreService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
                .buildAndExpand(created.productVersionId(), created.scoringModelId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<ProductScoreResponse> patch(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId,
            @Valid @RequestBody ProductScorePatchRequest request) {
        return ResponseEntity.ok(productScoreService.patch(productVersionId, scoringModelId, request));
    }

    @DeleteMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        productScoreService.delete(productVersionId, scoringModelId);
        return ResponseEntity.noContent().build();
    }
}
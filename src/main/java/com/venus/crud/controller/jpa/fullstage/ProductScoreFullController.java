package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductScoreFullResponse;
import com.venus.crud.service.jpa.fullstage.ProductScoreFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-scores")
public class ProductScoreFullController {

    private final ProductScoreFullService productScoreFullService;

    public ProductScoreFullController(ProductScoreFullService productScoreFullService) {
        this.productScoreFullService = productScoreFullService;
    }

    @GetMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}/full")
    public ResponseEntity<ProductScoreFullResponse> findByProductVersionIdAndScoringModelId(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        return ResponseEntity.ok(productScoreFullService.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId));
    }
}
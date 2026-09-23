package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductScoreFullResponse;
import com.venus.crud.service.jpa.fullstage.ProductScoreFullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-scores")
@Tag(name = "Scores de Produto", description = "Nota geral do produto por categoria de score, com o agregado.")
public class ProductScoreFullController {

    private final ProductScoreFullService productScoreFullService;

    public ProductScoreFullController(ProductScoreFullService productScoreFullService) {
        this.productScoreFullService = productScoreFullService;
    }

    @Operation(
            operationId = "productScoreFullFindByProductVersionIdAndScoringModelId",
            summary = "Busca o score de uma versão de produto num modelo, com as categorias detalhadas")
    @GetMapping("/product-version/{productVersionId}/scoring-model/{scoringModelId}/full")
    public ResponseEntity<ProductScoreFullResponse> findByProductVersionIdAndScoringModelId(
            @PathVariable Long productVersionId, @PathVariable Long scoringModelId) {
        return ResponseEntity.ok(productScoreFullService.findByProductVersionIdAndScoringModelId(productVersionId, scoringModelId));
    }
}
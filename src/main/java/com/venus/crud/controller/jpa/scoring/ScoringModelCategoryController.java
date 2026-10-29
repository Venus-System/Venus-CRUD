package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoringModelCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelCategoryResponse;
import com.venus.crud.service.jpa.scoring.ScoringModelCategoryService;
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
@RequestMapping("/api/scoring-model-categories")
@Tag(name = "Modelos de Score", description = "Versões do modelo de pontuação e o peso de cada categoria.")
public class ScoringModelCategoryController {

    private final ScoringModelCategoryService scoringModelCategoryService;

    public ScoringModelCategoryController(ScoringModelCategoryService scoringModelCategoryService) {
        this.scoringModelCategoryService = scoringModelCategoryService;
    }

    @Operation(operationId = "scoringModelCategoryFindAll", summary = "Lista as categorias do modelo de score")
    @GetMapping
    public ResponseEntity<List<ScoringModelCategoryResponse>> findAll() {
        return ResponseEntity.ok(scoringModelCategoryService.findAll());
    }

    @Operation(
            operationId = "scoringModelCategorySearch",
            summary = "Busca as categorias do modelo de score com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ScoringModelCategoryResponse>> search(
            @RequestParam(required = false) Long scoreCategoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scoringModelCategoryService.search(scoreCategoryId, pageable));
    }

    @Operation(
            operationId = "scoringModelCategoryFindByScoringModelId",
            summary = "Lista as categorias e os pesos de um modelo de score")
    @GetMapping("/model/{scoringModelId}")
    public ResponseEntity<List<ScoringModelCategoryResponse>> findByScoringModelId(@PathVariable Long scoringModelId) {
        return ResponseEntity.ok(scoringModelCategoryService.findByScoringModelId(scoringModelId));
    }

    @Operation(operationId = "scoringModelCategoryCreate", summary = "Cadastra uma categoria do modelo de score")
    @PostMapping
    public ResponseEntity<ScoringModelCategoryResponse> create(@Valid @RequestBody ScoringModelCategoryRequest request) {
        ScoringModelCategoryResponse created = scoringModelCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/model/{scoringModelId}")
                .buildAndExpand(created.scoringModelId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "scoringModelCategoryPatch", summary = "Atualiza parcialmente a categoria do modelo de score")
    @PatchMapping("/model/{scoringModelId}/category/{scoreCategoryId}")
    public ResponseEntity<ScoringModelCategoryResponse> patch(
            @PathVariable Long scoringModelId, @PathVariable Long scoreCategoryId,
            @Valid @RequestBody ScoringModelCategoryPatchRequest request) {
        return ResponseEntity.ok(scoringModelCategoryService.patch(scoringModelId, scoreCategoryId, request));
    }

    @Operation(operationId = "scoringModelCategoryDelete", summary = "Remove a categoria do modelo de score")
    @DeleteMapping("/model/{scoringModelId}/category/{scoreCategoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long scoringModelId, @PathVariable Long scoreCategoryId) {
        scoringModelCategoryService.delete(scoringModelId, scoreCategoryId);
        return ResponseEntity.noContent().build();
    }
}

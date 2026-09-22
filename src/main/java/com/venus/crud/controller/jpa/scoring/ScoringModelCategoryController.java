package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoringModelCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelCategoryResponse;
import com.venus.crud.service.jpa.scoring.ScoringModelCategoryService;
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
public class ScoringModelCategoryController {

    private final ScoringModelCategoryService scoringModelCategoryService;

    public ScoringModelCategoryController(ScoringModelCategoryService scoringModelCategoryService) {
        this.scoringModelCategoryService = scoringModelCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<ScoringModelCategoryResponse>> findAll() {
        return ResponseEntity.ok(scoringModelCategoryService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ScoringModelCategoryResponse>> search(
            @RequestParam(required = false) Long scoreCategoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scoringModelCategoryService.search(scoreCategoryId, pageable));
    }

    @GetMapping("/model/{scoringModelId}")
    public ResponseEntity<List<ScoringModelCategoryResponse>> findByScoringModelId(@PathVariable Long scoringModelId) {
        return ResponseEntity.ok(scoringModelCategoryService.findByScoringModelId(scoringModelId));
    }

    @PostMapping
    public ResponseEntity<ScoringModelCategoryResponse> create(@Valid @RequestBody ScoringModelCategoryRequest request) {
        ScoringModelCategoryResponse created = scoringModelCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/model/{scoringModelId}")
                .buildAndExpand(created.scoringModelId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/model/{scoringModelId}/category/{scoreCategoryId}")
    public ResponseEntity<ScoringModelCategoryResponse> patch(
            @PathVariable Long scoringModelId, @PathVariable Long scoreCategoryId,
            @Valid @RequestBody ScoringModelCategoryPatchRequest request) {
        return ResponseEntity.ok(scoringModelCategoryService.patch(scoringModelId, scoreCategoryId, request));
    }

    @DeleteMapping("/model/{scoringModelId}/category/{scoreCategoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long scoringModelId, @PathVariable Long scoreCategoryId) {
        scoringModelCategoryService.delete(scoringModelId, scoreCategoryId);
        return ResponseEntity.noContent().build();
    }
}

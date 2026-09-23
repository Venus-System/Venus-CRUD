package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.RecommendationPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.RecommendationRequest;
import com.venus.crud.dto.jpa.response.scoring.RecommendationResponse;
import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.service.jpa.scoring.RecommendationService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recomendações", description = "Produtos recomendados a partir de uma análise.")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(operationId = "recommendationFindAll", summary = "Lista as recomendações")
    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> findAll() {
        return ResponseEntity.ok(recommendationService.findAll());
    }

    @Operation(operationId = "recommendationSearch", summary = "Busca as recomendações com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<RecommendationResponse>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) RecommendationType recommendationType,
            @RequestParam(required = false) Long productVersionId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recommendationService.search(userId, recommendationType, productVersionId, pageable));
    }

    @Operation(operationId = "recommendationFindByUserId", summary = "Lista as recomendações de um usuário")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationResponse>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.findByUserId(userId));
    }

    @Operation(
            operationId = "recommendationFindByAnalysisResultId",
            summary = "Lista as recomendações geradas por um resultado de análise")
    @GetMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<List<RecommendationResponse>> findByAnalysisResultId(@PathVariable Long analysisResultId) {
        return ResponseEntity.ok(recommendationService.findByAnalysisResultId(analysisResultId));
    }

    @Operation(operationId = "recommendationFindById", summary = "Busca a recomendação por id")
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationService.findById(id));
    }

    @Operation(operationId = "recommendationCreate", summary = "Cadastra uma recomendação")
    @PostMapping
    public ResponseEntity<RecommendationResponse> create(@Valid @RequestBody RecommendationRequest request) {
        RecommendationResponse created = recommendationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "recommendationUpdate", summary = "Substitui os dados da recomendação")
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationResponse> update(@PathVariable Long id, @Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.update(id, request));
    }

    @Operation(operationId = "recommendationPatch", summary = "Atualiza parcialmente a recomendação")
    @PatchMapping("/{id}")
    public ResponseEntity<RecommendationResponse> patch(@PathVariable Long id, @Valid @RequestBody RecommendationPatchRequest request) {
        return ResponseEntity.ok(recommendationService.patch(id, request));
    }

    @Operation(operationId = "recommendationDelete", summary = "Remove a recomendação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoringModelPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import com.venus.crud.service.jpa.scoring.ScoringModelService;
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
@RequestMapping("/api/scoring-models")
@Tag(name = "Modelos de Score", description = "Versões do modelo de pontuação e o peso de cada categoria.")
public class ScoringModelController {

    private final ScoringModelService scoringModelService;

    public ScoringModelController(ScoringModelService scoringModelService) {
        this.scoringModelService = scoringModelService;
    }

    @Operation(operationId = "scoringModelFindAll", summary = "Lista os modelos de score")
    @GetMapping
    public ResponseEntity<List<ScoringModelResponse>> findAll() {
        return ResponseEntity.ok(scoringModelService.findAll());
    }

    @Operation(operationId = "scoringModelSearch", summary = "Busca os modelos de score com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ScoringModelResponse>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scoringModelService.search(name, pageable));
    }

    @Operation(
            operationId = "scoringModelFindActive",
            summary = "Busca o modelo de score ativo",
            description = "Devolve o único modelo marcado como ativo. Devolve 404 se nenhum modelo estiver ativo.")
    @GetMapping("/active")
    public ResponseEntity<ScoringModelResponse> findActive() {
        return ResponseEntity.ok(scoringModelService.findActive());
    }

    @Operation(operationId = "scoringModelFindById", summary = "Busca o modelo de score por id")
    @GetMapping("/{id}")
    public ResponseEntity<ScoringModelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(scoringModelService.findById(id));
    }

    @Operation(operationId = "scoringModelCreate", summary = "Cadastra um modelo de score")
    @PostMapping
    public ResponseEntity<ScoringModelResponse> create(@Valid @RequestBody ScoringModelRequest request) {
        ScoringModelResponse created = scoringModelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "scoringModelUpdate", summary = "Substitui os dados do modelo de score")
    @PutMapping("/{id}")
    public ResponseEntity<ScoringModelResponse> update(@PathVariable Long id, @Valid @RequestBody ScoringModelRequest request) {
        return ResponseEntity.ok(scoringModelService.update(id, request));
    }

    @Operation(operationId = "scoringModelPatch", summary = "Atualiza parcialmente o modelo de score")
    @PatchMapping("/{id}")
    public ResponseEntity<ScoringModelResponse> patch(@PathVariable Long id, @Valid @RequestBody ScoringModelPatchRequest request) {
        return ResponseEntity.ok(scoringModelService.patch(id, request));
    }

    @Operation(operationId = "scoringModelDelete", summary = "Remove o modelo de score")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scoringModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
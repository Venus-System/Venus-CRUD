package com.venus.crud.controller.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.RuleEvaluationPatchRequest;
import com.venus.crud.dto.jpa.request.scan.RuleEvaluationRequest;
import com.venus.crud.dto.jpa.response.scan.RuleEvaluationResponse;
import com.venus.crud.service.jpa.scan.RuleEvaluationService;
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
@RequestMapping("/api/rule-evaluations")
@Tag(name = "Avaliações de Regra", description = "Registro de cada regra de compatibilidade disparada numa análise.")
public class RuleEvaluationController {

    private final RuleEvaluationService ruleEvaluationService;

    public RuleEvaluationController(RuleEvaluationService ruleEvaluationService) {
        this.ruleEvaluationService = ruleEvaluationService;
    }

    @Operation(operationId = "ruleEvaluationFindAll", summary = "Lista as avaliações de regra")
    @GetMapping
    public ResponseEntity<List<RuleEvaluationResponse>> findAll() {
        return ResponseEntity.ok(ruleEvaluationService.findAll());
    }

    @Operation(operationId = "ruleEvaluationSearch", summary = "Busca as avaliações de regra com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<RuleEvaluationResponse>> search(
            @RequestParam(required = false) Long ingredientId,
            @RequestParam(required = false) Long compatibilityRuleId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ruleEvaluationService.search(ingredientId, compatibilityRuleId, pageable));
    }

    @Operation(
            operationId = "ruleEvaluationFindByAnalysisResultId",
            summary = "Lista as regras disparadas num resultado de análise")
    @GetMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<List<RuleEvaluationResponse>> findByAnalysisResultId(
            @PathVariable Long analysisResultId, @RequestParam(required = false) Boolean wasMatched) {
        return ResponseEntity.ok(ruleEvaluationService.findByAnalysisResultId(analysisResultId, wasMatched));
    }

    @Operation(operationId = "ruleEvaluationFindById", summary = "Busca a avaliação de regra por id")
    @GetMapping("/{id}")
    public ResponseEntity<RuleEvaluationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleEvaluationService.findById(id));
    }

    @Operation(operationId = "ruleEvaluationCreate", summary = "Cadastra uma avaliação de regra")
    @PostMapping
    public ResponseEntity<RuleEvaluationResponse> create(@Valid @RequestBody RuleEvaluationRequest request) {
        RuleEvaluationResponse created = ruleEvaluationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "ruleEvaluationUpdate", summary = "Substitui os dados da avaliação de regra")
    @PutMapping("/{id}")
    public ResponseEntity<RuleEvaluationResponse> update(@PathVariable Long id, @Valid @RequestBody RuleEvaluationRequest request) {
        return ResponseEntity.ok(ruleEvaluationService.update(id, request));
    }

    @Operation(operationId = "ruleEvaluationPatch", summary = "Atualiza parcialmente a avaliação de regra")
    @PatchMapping("/{id}")
    public ResponseEntity<RuleEvaluationResponse> patch(@PathVariable Long id, @Valid @RequestBody RuleEvaluationPatchRequest request) {
        return ResponseEntity.ok(ruleEvaluationService.patch(id, request));
    }

    @Operation(operationId = "ruleEvaluationDelete", summary = "Remove a avaliação de regra")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ruleEvaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
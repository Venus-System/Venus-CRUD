package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.service.jpa.fullstage.AnalysisResultFullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis-results")
@Tag(name = "Resultados de Análise", description = "Resultado da análise de um scan, com o agregado de scores e regras.")
public class AnalysisResultFullController {

    private final AnalysisResultFullService analysisResultFullService;

    public AnalysisResultFullController(AnalysisResultFullService analysisResultFullService) {
        this.analysisResultFullService = analysisResultFullService;
    }

    @Operation(operationId = "analysisResultFullFindById", summary = "Busca o resultado de análise completo por id")
    @GetMapping("/{id}/full")
    public ResponseEntity<AnalysisResultFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisResultFullService.findById(id));
    }
}
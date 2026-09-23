package com.venus.crud.controller.mongo.fullstage;

import com.venus.crud.dto.mongo.response.fullstage.ScanFullResponse;
import com.venus.crud.service.mongo.fullstage.ScanFullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan-sessions")
@Tag(name = "Sessões de Scan", description = "Sessões de leitura de rótulo guardadas no MongoDB, com o agregado do resultado.")
public class ScanFullController {

    private final ScanFullService scanFullService;

    public ScanFullController(ScanFullService scanFullService) {
        this.scanFullService = scanFullService;
    }

    @Operation(
            operationId = "scanFullFindByScanSessionIdAndAnalysisResultId",
            summary = "Busca a sessão de scan com o resultado da análise")
    @GetMapping("/{scanSessionId}/analysis-result/{analysisResultId}/full")
    public ResponseEntity<ScanFullResponse> findByScanSessionIdAndAnalysisResultId(
            @PathVariable String scanSessionId, @PathVariable Long analysisResultId) {
        return ResponseEntity.ok(scanFullService.findByScanSessionIdAndAnalysisResultId(scanSessionId, analysisResultId));
    }
}

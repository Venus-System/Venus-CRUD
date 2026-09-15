package com.venus.crud.controller.mongo.fullstage;

import com.venus.crud.dto.mongo.response.fullstage.ScanFullResponse;
import com.venus.crud.service.mongo.fullstage.ScanFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan-sessions")
public class ScanFullController {

    private final ScanFullService scanFullService;

    public ScanFullController(ScanFullService scanFullService) {
        this.scanFullService = scanFullService;
    }

    @GetMapping("/{scanSessionId}/analysis-result/{analysisResultId}/full")
    public ResponseEntity<ScanFullResponse> findByScanSessionIdAndAnalysisResultId(
            @PathVariable String scanSessionId, @PathVariable Long analysisResultId) {
        return ResponseEntity.ok(scanFullService.findByScanSessionIdAndAnalysisResultId(scanSessionId, analysisResultId));
    }
}

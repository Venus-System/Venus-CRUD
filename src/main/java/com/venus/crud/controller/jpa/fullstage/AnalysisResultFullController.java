package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.service.jpa.fullstage.AnalysisResultFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis-results")
public class AnalysisResultFullController {

    private final AnalysisResultFullService analysisResultFullService;

    public AnalysisResultFullController(AnalysisResultFullService analysisResultFullService) {
        this.analysisResultFullService = analysisResultFullService;
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<AnalysisResultFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisResultFullService.findById(id));
    }
}
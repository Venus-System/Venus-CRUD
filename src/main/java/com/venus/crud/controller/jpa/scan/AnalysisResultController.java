package com.venus.crud.controller.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.AnalysisResultPatchRequest;
import com.venus.crud.dto.jpa.request.scan.AnalysisResultRequest;
import com.venus.crud.dto.jpa.response.scan.AnalysisResultResponse;
import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.service.jpa.scan.AnalysisResultService;
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
@RequestMapping("/api/analysis-results")
public class AnalysisResultController {

    private final AnalysisResultService analysisResultService;

    public AnalysisResultController(AnalysisResultService analysisResultService) {
        this.analysisResultService = analysisResultService;
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResultResponse>> findAll() {
        return ResponseEntity.ok(analysisResultService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<AnalysisResultResponse>> search(
            @RequestParam(required = false) AnalysisStatus status,
            @RequestParam(required = false) Integer minOverallScore,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(analysisResultService.search(status, minOverallScore, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<AnalysisResultResponse>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) Long productVersionId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(analysisResultService.findByUserId(userId, productVersionId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResultResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisResultService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AnalysisResultResponse> create(@Valid @RequestBody AnalysisResultRequest request) {
        AnalysisResultResponse created = analysisResultService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisResultResponse> update(@PathVariable Long id, @Valid @RequestBody AnalysisResultRequest request) {
        return ResponseEntity.ok(analysisResultService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnalysisResultResponse> patch(@PathVariable Long id, @Valid @RequestBody AnalysisResultPatchRequest request) {
        return ResponseEntity.ok(analysisResultService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        analysisResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
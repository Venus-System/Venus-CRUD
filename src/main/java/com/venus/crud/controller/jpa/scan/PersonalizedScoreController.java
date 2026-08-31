package com.venus.crud.controller.jpa.scan;

import com.venus.crud.dto.jpa.patch.scan.PersonalizedScorePatchRequest;
import com.venus.crud.dto.jpa.request.scan.PersonalizedScoreRequest;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.service.jpa.scan.PersonalizedScoreService;
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
@RequestMapping("/api/personalized-scores")
public class PersonalizedScoreController {

    private final PersonalizedScoreService personalizedScoreService;

    public PersonalizedScoreController(PersonalizedScoreService personalizedScoreService) {
        this.personalizedScoreService = personalizedScoreService;
    }

    @GetMapping
    public ResponseEntity<List<PersonalizedScoreResponse>> findAll() {
        return ResponseEntity.ok(personalizedScoreService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<PersonalizedScoreResponse>> search(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(personalizedScoreService.search(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<PersonalizedScoreResponse>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) Long productVersionId,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) RecommendationLevel recommendationLevel,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(personalizedScoreService.findByUserId(userId, productVersionId, riskLevel, recommendationLevel, pageable));
    }

    @GetMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<PersonalizedScoreResponse> findByAnalysisResultId(@PathVariable Long analysisResultId) {
        return ResponseEntity.ok(personalizedScoreService.findByAnalysisResultId(analysisResultId));
    }

    @PostMapping
    public ResponseEntity<PersonalizedScoreResponse> create(@Valid @RequestBody PersonalizedScoreRequest request) {
        PersonalizedScoreResponse created = personalizedScoreService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/analysis-result/{analysisResultId}")
                .buildAndExpand(created.analysisResultId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<PersonalizedScoreResponse> update(
            @PathVariable Long analysisResultId, @Valid @RequestBody PersonalizedScoreRequest request) {
        return ResponseEntity.ok(personalizedScoreService.update(analysisResultId, request));
    }

    @PatchMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<PersonalizedScoreResponse> patch(
            @PathVariable Long analysisResultId, @Valid @RequestBody PersonalizedScorePatchRequest request) {
        return ResponseEntity.ok(personalizedScoreService.patch(analysisResultId, request));
    }

    @DeleteMapping("/analysis-result/{analysisResultId}")
    public ResponseEntity<Void> delete(@PathVariable Long analysisResultId) {
        personalizedScoreService.delete(analysisResultId);
        return ResponseEntity.noContent().build();
    }
}
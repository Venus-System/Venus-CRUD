package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoreCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoreCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoreCategoryResponse;
import com.venus.crud.service.jpa.scoring.ScoreCategoryService;
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
@RequestMapping("/api/score-categories")
public class ScoreCategoryController {

    private final ScoreCategoryService scoreCategoryService;

    public ScoreCategoryController(ScoreCategoryService scoreCategoryService) {
        this.scoreCategoryService = scoreCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<ScoreCategoryResponse>> findAll() {
        return ResponseEntity.ok(scoreCategoryService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ScoreCategoryResponse>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scoreCategoryService.search(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(scoreCategoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ScoreCategoryResponse> create(@Valid @RequestBody ScoreCategoryRequest request) {
        ScoreCategoryResponse created = scoreCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody ScoreCategoryRequest request) {
        return ResponseEntity.ok(scoreCategoryService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> patch(@PathVariable Long id, @Valid @RequestBody ScoreCategoryPatchRequest request) {
        return ResponseEntity.ok(scoreCategoryService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scoreCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
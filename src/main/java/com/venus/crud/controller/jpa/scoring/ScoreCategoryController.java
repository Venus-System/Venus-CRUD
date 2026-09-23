package com.venus.crud.controller.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoreCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoreCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoreCategoryResponse;
import com.venus.crud.service.jpa.scoring.ScoreCategoryService;
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
@RequestMapping("/api/score-categories")
@Tag(name = "Categorias de Score", description = "Dimensões avaliadas do produto, como segurança e eficácia.")
public class ScoreCategoryController {

    private final ScoreCategoryService scoreCategoryService;

    public ScoreCategoryController(ScoreCategoryService scoreCategoryService) {
        this.scoreCategoryService = scoreCategoryService;
    }

    @Operation(operationId = "scoreCategoryFindAll", summary = "Lista as categorias de score")
    @GetMapping
    public ResponseEntity<List<ScoreCategoryResponse>> findAll() {
        return ResponseEntity.ok(scoreCategoryService.findAll());
    }

    @Operation(operationId = "scoreCategorySearch", summary = "Busca as categorias de score com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ScoreCategoryResponse>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scoreCategoryService.search(name, pageable));
    }

    @Operation(operationId = "scoreCategoryFindById", summary = "Busca a categoria de score por id")
    @GetMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(scoreCategoryService.findById(id));
    }

    @Operation(operationId = "scoreCategoryCreate", summary = "Cadastra uma categoria de score")
    @PostMapping
    public ResponseEntity<ScoreCategoryResponse> create(@Valid @RequestBody ScoreCategoryRequest request) {
        ScoreCategoryResponse created = scoreCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "scoreCategoryUpdate", summary = "Substitui os dados da categoria de score")
    @PutMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody ScoreCategoryRequest request) {
        return ResponseEntity.ok(scoreCategoryService.update(id, request));
    }

    @Operation(operationId = "scoreCategoryPatch", summary = "Atualiza parcialmente a categoria de score")
    @PatchMapping("/{id}")
    public ResponseEntity<ScoreCategoryResponse> patch(@PathVariable Long id, @Valid @RequestBody ScoreCategoryPatchRequest request) {
        return ResponseEntity.ok(scoreCategoryService.patch(id, request));
    }

    @Operation(operationId = "scoreCategoryDelete", summary = "Remove a categoria de score")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scoreCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
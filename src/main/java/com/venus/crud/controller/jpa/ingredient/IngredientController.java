package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import com.venus.crud.service.jpa.ingredient.IngredientService;
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
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientResponse>> search(
            @RequestParam(required = false) String commonName,
            @RequestParam(required = false) Long ingredientCategoryId,
            @RequestParam(required = false) Short minIrritationRiskLevel,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientService.search(commonName, ingredientCategoryId, minIrritationRiskLevel, pageable));
    }

    @GetMapping("/inci-name/{inciName}")
    public ResponseEntity<IngredientResponse> findByInciName(@PathVariable String inciName) {
        return ResponseEntity.ok(ingredientService.findByInciName(inciName));
    }

    @GetMapping("/source-reference/{sourceReference}")
    public ResponseEntity<IngredientResponse> findBySourceReference(@PathVariable String sourceReference) {
        return ResponseEntity.ok(ingredientService.findBySourceReference(sourceReference));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse created = ingredientService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredientResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientPatchRequest request) {
        return ResponseEntity.ok(ingredientService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
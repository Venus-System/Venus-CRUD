package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientEffectPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientEffectRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientEffectResponse;
import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.ingredient.IngredientEffectService;
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
@RequestMapping("/api/ingredient-effects")
public class IngredientEffectController {

    private final IngredientEffectService ingredientEffectService;

    public IngredientEffectController(IngredientEffectService ingredientEffectService) {
        this.ingredientEffectService = ingredientEffectService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientEffectResponse>> findAll() {
        return ResponseEntity.ok(ingredientEffectService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientEffectResponse>> search(
            @RequestParam(required = false) Long profileTagId,
            @RequestParam(required = false) EffectCategory effectCategory,
            @RequestParam(required = false) ReviewStatus reviewStatus,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientEffectService.search(profileTagId, effectCategory, reviewStatus, sourceType, pageable));
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<IngredientEffectResponse>> findByIngredientId(
            @PathVariable Long ingredientId, @RequestParam(required = false) Long profileTagId) {
        return ResponseEntity.ok(ingredientEffectService.findByIngredientId(ingredientId, profileTagId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientEffectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientEffectService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientEffectResponse> create(@Valid @RequestBody IngredientEffectRequest request) {
        IngredientEffectResponse created = ingredientEffectService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientEffectResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientEffectRequest request) {
        return ResponseEntity.ok(ingredientEffectService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredientEffectResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientEffectPatchRequest request) {
        return ResponseEntity.ok(ingredientEffectService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientEffectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
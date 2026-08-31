package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.CompatibilityRulePatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.CompatibilityRuleRequest;
import com.venus.crud.dto.jpa.response.ingredient.CompatibilityRuleResponse;
import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.ingredient.CompatibilityRuleService;
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
@RequestMapping("/api/compatibility-rules")
public class CompatibilityRuleController {

    private final CompatibilityRuleService compatibilityRuleService;

    public CompatibilityRuleController(CompatibilityRuleService compatibilityRuleService) {
        this.compatibilityRuleService = compatibilityRuleService;
    }

    @GetMapping
    public ResponseEntity<List<CompatibilityRuleResponse>> findAll() {
        return ResponseEntity.ok(compatibilityRuleService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<CompatibilityRuleResponse>> search(
            @RequestParam(required = false) EffectType effectType,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(compatibilityRuleService.search(effectType, sourceType, pageable));
    }

    @GetMapping("/ingredient-effect/{ingredientEffectId}")
    public ResponseEntity<List<CompatibilityRuleResponse>> findByIngredientEffectId(@PathVariable Long ingredientEffectId) {
        return ResponseEntity.ok(compatibilityRuleService.findByIngredientEffectId(ingredientEffectId));
    }

    @GetMapping("/scoring-model/{scoringModelId}/enabled")
    public ResponseEntity<List<CompatibilityRuleResponse>> findEnabledByScoringModelId(@PathVariable Long scoringModelId) {
        return ResponseEntity.ok(compatibilityRuleService.findEnabledByScoringModelId(scoringModelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompatibilityRuleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(compatibilityRuleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CompatibilityRuleResponse> create(@Valid @RequestBody CompatibilityRuleRequest request) {
        CompatibilityRuleResponse created = compatibilityRuleService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompatibilityRuleResponse> update(@PathVariable Long id, @Valid @RequestBody CompatibilityRuleRequest request) {
        return ResponseEntity.ok(compatibilityRuleService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompatibilityRuleResponse> patch(@PathVariable Long id, @Valid @RequestBody CompatibilityRulePatchRequest request) {
        return ResponseEntity.ok(compatibilityRuleService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compatibilityRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
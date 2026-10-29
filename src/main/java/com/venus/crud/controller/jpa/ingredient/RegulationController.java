package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.RegulationPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.RegulationRequest;
import com.venus.crud.dto.jpa.response.ingredient.RegulationResponse;
import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.service.jpa.ingredient.RegulationService;
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
@RequestMapping("/api/regulations")
@Tag(name = "Regulamentações", description = "Restrições regulatórias aplicadas a ingredientes.")
public class RegulationController {

    private final RegulationService regulationService;

    public RegulationController(RegulationService regulationService) {
        this.regulationService = regulationService;
    }

    @Operation(operationId = "regulationFindAll", summary = "Lista as regulamentações")
    @GetMapping
    public ResponseEntity<List<RegulationResponse>> findAll() {
        return ResponseEntity.ok(regulationService.findAll());
    }

    @Operation(operationId = "regulationSearch", summary = "Busca as regulamentações com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<RegulationResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String agency,
            @RequestParam(required = false) RegulationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(regulationService.search(title, country, agency, status, pageable));
    }

    @Operation(operationId = "regulationFindById", summary = "Busca a regulamentação por id")
    @GetMapping("/{id}")
    public ResponseEntity<RegulationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(regulationService.findById(id));
    }

    @Operation(operationId = "regulationCreate", summary = "Cadastra uma regulamentação")
    @PostMapping
    public ResponseEntity<RegulationResponse> create(@Valid @RequestBody RegulationRequest request) {
        RegulationResponse created = regulationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "regulationUpdate", summary = "Substitui os dados da regulamentação")
    @PutMapping("/{id}")
    public ResponseEntity<RegulationResponse> update(@PathVariable Long id, @Valid @RequestBody RegulationRequest request) {
        return ResponseEntity.ok(regulationService.update(id, request));
    }

    @Operation(operationId = "regulationPatch", summary = "Atualiza parcialmente a regulamentação")
    @PatchMapping("/{id}")
    public ResponseEntity<RegulationResponse> patch(@PathVariable Long id, @Valid @RequestBody RegulationPatchRequest request) {
        return ResponseEntity.ok(regulationService.patch(id, request));
    }

    @Operation(operationId = "regulationDelete", summary = "Remove a regulamentação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regulationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
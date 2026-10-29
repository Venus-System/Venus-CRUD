package com.venus.crud.controller.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReportPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReportRequest;
import com.venus.crud.dto.jpa.response.review.ReportResponse;
import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.service.jpa.review.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/reports")
@Tag(name = "Denúncias", description = "Denúncias de conteúdo impróprio em avaliações.")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(operationId = "reportFindAll", summary = "Lista as denúncias")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> findAll() {
        return ResponseEntity.ok(reportService.findAll());
    }

    @Operation(operationId = "reportSearch", summary = "Busca as denúncias com filtros e paginação")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/search")
    public ResponseEntity<Slice<ReportResponse>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportTargetType targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) Long adminUserId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reportService.search(userId, status, targetType, targetId, adminUserId, pageable));
    }

    @Operation(operationId = "reportFindById", summary = "Busca a denúncia por id")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.findById(id));
    }

    @Operation(operationId = "reportCreate", summary = "Cadastra uma denúncia")
    @PreAuthorize("hasRole('MODERATOR') or (@ownership.canAccessUser(#request.userId()) and #request.adminUserId() == null"
            + " and #request.status() == T(com.venus.crud.entity.enums.ReportStatus).OPEN)")
    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request) {
        ReportResponse created = reportService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "reportUpdate", summary = "Substitui os dados da denúncia")
    @PreAuthorize("hasRole('MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> update(@PathVariable Long id, @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.update(id, request));
    }

    @Operation(operationId = "reportPatch", summary = "Atualiza parcialmente a denúncia")
    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ReportResponse> patch(@PathVariable Long id, @Valid @RequestBody ReportPatchRequest request) {
        return ResponseEntity.ok(reportService.patch(id, request));
    }

    @Operation(operationId = "reportDelete", summary = "Remove a denúncia")
    @PreAuthorize("hasRole('MODERATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
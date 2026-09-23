package com.venus.crud.controller.mongo;

import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.entity.enums.AnalysisStatus;
import com.venus.crud.service.mongo.ScanSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/scan-sessions")
@Tag(name = "Sessões de Scan", description = "Sessões de leitura de rótulo guardadas no MongoDB, com o agregado do resultado.")
public class ScanSessionController {

    private final ScanSessionService scanSessionService;

    public ScanSessionController(ScanSessionService scanSessionService) {
        this.scanSessionService = scanSessionService;
    }

    @Operation(operationId = "scanSessionFindAll", summary = "Lista as sessões de scan")
    @GetMapping
    public ResponseEntity<Slice<ScanSessionResponse>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findAll(pageable));
    }

    @Operation(operationId = "scanSessionFindById", summary = "Busca a sessão de scan por id")
    @GetMapping("/{id}")
    public ResponseEntity<ScanSessionResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(scanSessionService.findById(id));
    }

    @Operation(operationId = "scanSessionFindByStatus", summary = "Lista as sessões de scan por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Slice<ScanSessionResponse>> findByStatus(
            @PathVariable AnalysisStatus status, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findByStatus(status, pageable));
    }

    @Operation(operationId = "scanSessionFindByDeviceId", summary = "Lista as sessões de scan de um dispositivo")
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Slice<ScanSessionResponse>> findByDeviceId(
            @Parameter(description = "Identificador do aparelho que originou o scan.")
            @PathVariable String deviceId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findByDeviceId(deviceId, pageable));
    }

    @Operation(operationId = "scanSessionCreate", summary = "Cadastra uma sessão de scan")
    @PostMapping
    public ResponseEntity<ScanSessionResponse> create(@Valid @RequestBody ScanSessionRequest request) {
        ScanSessionResponse created = scanSessionService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}

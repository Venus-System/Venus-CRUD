package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ClaimPatchRequest;
import com.venus.crud.dto.jpa.request.product.ClaimRequest;
import com.venus.crud.dto.jpa.response.product.ClaimResponse;
import com.venus.crud.entity.enums.ClaimType;
import com.venus.crud.service.jpa.product.ClaimService;
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
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> findAll() {
        return ResponseEntity.ok(claimService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ClaimResponse>> search(
            @RequestParam(required = false) ClaimType claimType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(claimService.search(claimType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> create(@Valid @RequestBody ClaimRequest request) {
        ClaimResponse created = claimService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimResponse> update(@PathVariable Long id, @Valid @RequestBody ClaimRequest request) {
        return ResponseEntity.ok(claimService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClaimResponse> patch(@PathVariable Long id, @Valid @RequestBody ClaimPatchRequest request) {
        return ResponseEntity.ok(claimService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        claimService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

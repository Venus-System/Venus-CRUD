package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.AllergyPatchRequest;
import com.venus.crud.dto.jpa.request.user.AllergyRequest;
import com.venus.crud.dto.jpa.response.user.AllergyResponse;
import com.venus.crud.entity.enums.AllergyType;
import com.venus.crud.service.jpa.user.AllergyService;
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
@RequestMapping("/api/allergies")
public class AllergyController {

    private final AllergyService allergyService;

    public AllergyController(AllergyService allergyService) {
        this.allergyService = allergyService;
    }

    @GetMapping
    public ResponseEntity<List<AllergyResponse>> findAll() {
        return ResponseEntity.ok(allergyService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<AllergyResponse>> search(
            @RequestParam(required = false) AllergyType allergyType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(allergyService.search(allergyType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllergyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(allergyService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AllergyResponse> create(@Valid @RequestBody AllergyRequest request) {
        AllergyResponse created = allergyService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllergyResponse> update(@PathVariable Long id, @Valid @RequestBody AllergyRequest request) {
        return ResponseEntity.ok(allergyService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AllergyResponse> patch(@PathVariable Long id, @Valid @RequestBody AllergyPatchRequest request) {
        return ResponseEntity.ok(allergyService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        allergyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.venus.crud.controller.jpa.shared;

import com.venus.crud.dto.jpa.patch.shared.ProfileTagPatchRequest;
import com.venus.crud.dto.jpa.request.shared.ProfileTagRequest;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.entity.enums.ProfileTagCategory;
import com.venus.crud.service.jpa.shared.ProfileTagService;
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
@RequestMapping("/api/profile-tags")
public class ProfileTagController {

    private final ProfileTagService profileTagService;

    public ProfileTagController(ProfileTagService profileTagService) {
        this.profileTagService = profileTagService;
    }

    @GetMapping
    public ResponseEntity<List<ProfileTagResponse>> findAll() {
        return ResponseEntity.ok(profileTagService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ProfileTagResponse>> search(
            @RequestParam(required = false) ProfileTagCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(profileTagService.search(category, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(profileTagService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProfileTagResponse> create(@Valid @RequestBody ProfileTagRequest request) {
        ProfileTagResponse created = profileTagService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> update(@PathVariable Long id, @Valid @RequestBody ProfileTagRequest request) {
        return ResponseEntity.ok(profileTagService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> patch(@PathVariable Long id, @Valid @RequestBody ProfileTagPatchRequest request) {
        return ResponseEntity.ok(profileTagService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profileTagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

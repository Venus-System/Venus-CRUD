package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserPreferencePatchRequest;
import com.venus.crud.dto.jpa.request.user.UserPreferenceRequest;
import com.venus.crud.dto.jpa.response.user.UserPreferenceResponse;
import com.venus.crud.service.jpa.user.UserPreferenceService;
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
@RequestMapping("/api/user-preferences")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @GetMapping
    public ResponseEntity<List<UserPreferenceResponse>> findAll() {
        return ResponseEntity.ok(userPreferenceService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<UserPreferenceResponse>> search(
            @RequestParam(required = false) Boolean preferCrueltyFree,
            @RequestParam(required = false) Boolean preferVegan,
            @RequestParam(required = false) Boolean preferSustainable,
            @RequestParam(required = false) Boolean preferFragranceFree,
            @RequestParam(required = false) Boolean preferParabenFree,
            @RequestParam(required = false) Boolean preferSulfateFree,
            @RequestParam(required = false) Boolean preferSiliconeFree,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userPreferenceService.search(preferCrueltyFree, preferVegan, preferSustainable,
                preferFragranceFree, preferParabenFree, preferSulfateFree, preferSiliconeFree, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userPreferenceService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<UserPreferenceResponse> create(@Valid @RequestBody UserPreferenceRequest request) {
        UserPreferenceResponse created = userPreferenceService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> update(@PathVariable Long userId, @Valid @RequestBody UserPreferenceRequest request) {
        return ResponseEntity.ok(userPreferenceService.update(userId, request));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> patch(@PathVariable Long userId, @Valid @RequestBody UserPreferencePatchRequest request) {
        return ResponseEntity.ok(userPreferenceService.patch(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userPreferenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
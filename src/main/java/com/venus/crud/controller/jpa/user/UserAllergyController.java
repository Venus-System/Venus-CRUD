package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserAllergyPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserAllergyRequest;
import com.venus.crud.dto.jpa.response.user.UserAllergyResponse;
import com.venus.crud.service.jpa.user.UserAllergyService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/user-allergies")
public class UserAllergyController {

    private final UserAllergyService userAllergyService;

    public UserAllergyController(UserAllergyService userAllergyService) {
        this.userAllergyService = userAllergyService;
    }

    @GetMapping
    public ResponseEntity<List<UserAllergyResponse>> findAll() {
        return ResponseEntity.ok(userAllergyService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<UserAllergyResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userAllergyService.findByUserId(userId, pageable));
    }

    @GetMapping("/allergy/{allergyId}")
    public ResponseEntity<Slice<UserAllergyResponse>> findByAllergyId(
            @PathVariable Long allergyId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userAllergyService.findByAllergyId(allergyId, pageable));
    }

    @PostMapping
    public ResponseEntity<UserAllergyResponse> create(@Valid @RequestBody UserAllergyRequest request) {
        UserAllergyResponse created = userAllergyService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/user/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/user/{userId}/allergy/{allergyId}")
    public ResponseEntity<UserAllergyResponse> patch(
            @PathVariable Long userId, @PathVariable Long allergyId, @Valid @RequestBody UserAllergyPatchRequest request) {
        return ResponseEntity.ok(userAllergyService.patch(userId, allergyId, request));
    }

    @DeleteMapping("/user/{userId}/allergy/{allergyId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long allergyId) {
        userAllergyService.delete(userId, allergyId);
        return ResponseEntity.noContent().build();
    }
}
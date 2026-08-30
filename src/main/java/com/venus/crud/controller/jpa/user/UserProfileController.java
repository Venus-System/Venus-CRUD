package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserProfilePatchRequest;
import com.venus.crud.dto.jpa.request.user.UserProfileRequest;
import com.venus.crud.dto.jpa.response.user.UserProfileResponse;
import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.service.jpa.user.UserProfileService;
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
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> findAll() {
        return ResponseEntity.ok(userProfileService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<UserProfileResponse>> search(
            @RequestParam(required = false) SkinType skinType,
            @RequestParam(required = false) HairType hairType,
            @RequestParam(required = false) SensitivityLevel skinSensitivity,
            @RequestParam(required = false) Boolean acneProne,
            @RequestParam(required = false) Boolean isPregnant,
            @RequestParam(required = false) AgeRange ageRange,
            @RequestParam(required = false) Gender gender,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userProfileService.search(skinType, hairType, skinSensitivity, acneProne, isPregnant, ageRange, gender, pageable));
    }

    @GetMapping("/count/skin-type")
    public ResponseEntity<Long> countBySkinType(@RequestParam SkinType skinType) {
        return ResponseEntity.ok(userProfileService.countBySkinType(skinType));
    }

    @GetMapping("/count/acne-prone")
    public ResponseEntity<Long> countByAcneProne() {
        return ResponseEntity.ok(userProfileService.countByAcneProne());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> create(@Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse created = userProfileService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> update(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.update(userId, request));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> patch(@PathVariable Long userId, @Valid @RequestBody UserProfilePatchRequest request) {
        return ResponseEntity.ok(userProfileService.patch(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userProfileService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
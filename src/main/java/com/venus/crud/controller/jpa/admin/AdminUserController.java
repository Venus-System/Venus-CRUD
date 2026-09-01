package com.venus.crud.controller.jpa.admin;

import com.venus.crud.dto.jpa.patch.admin.AdminUserPatchRequest;
import com.venus.crud.dto.jpa.request.admin.AdminUserRequest;
import com.venus.crud.dto.jpa.response.admin.AdminUserResponse;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.service.jpa.admin.AdminUserService;
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
@RequestMapping("/api/admin-users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> findAll() {
        return ResponseEntity.ok(adminUserService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<AdminUserResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AdminRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.search(name, role, isActive, pageable));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AdminUserResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminUserService.findByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> create(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse created = adminUserService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> update(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminUserService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminUserResponse> patch(@PathVariable Long id, @Valid @RequestBody AdminUserPatchRequest request) {
        return ResponseEntity.ok(adminUserService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
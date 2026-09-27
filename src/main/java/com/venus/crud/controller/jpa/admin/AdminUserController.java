package com.venus.crud.controller.jpa.admin;

import com.venus.crud.dto.jpa.patch.admin.AdminUserPatchRequest;
import com.venus.crud.dto.jpa.request.admin.AdminUserPasswordChangeRequest;
import com.venus.crud.dto.jpa.request.admin.AdminUserRequest;
import com.venus.crud.dto.jpa.response.admin.AdminUserResponse;
import com.venus.crud.entity.enums.AdminRole;
import com.venus.crud.service.jpa.admin.AdminUserService;
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
@RequestMapping("/api/admin-users")
@Tag(name = "Administradores", description = "Contas de administrador: cadastro, busca e senha local.")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Operation(operationId = "adminUserFindAll", summary = "Lista os administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> findAll() {
        return ResponseEntity.ok(adminUserService.findAll());
    }

    @Operation(operationId = "adminUserSearch", summary = "Busca os administradores com filtros e paginação")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Slice<AdminUserResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AdminRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.search(name, role, isActive, pageable));
    }

    @Operation(operationId = "adminUserFindByEmail", summary = "Busca o administrador pelo e-mail")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<AdminUserResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminUserService.findByEmail(email));
    }

    @Operation(operationId = "adminUserFindById", summary = "Busca o administrador por id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.findById(id));
    }

    @Operation(operationId = "adminUserCreate", summary = "Cadastra um administrador")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdminUserResponse> create(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse created = adminUserService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "adminUserUpdate", summary = "Substitui os dados do administrador")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> update(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminUserService.update(id, request));
    }

    @Operation(operationId = "adminUserPatch", summary = "Atualiza parcialmente o administrador")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<AdminUserResponse> patch(@PathVariable Long id, @Valid @RequestBody AdminUserPatchRequest request) {
        return ResponseEntity.ok(adminUserService.patch(id, request));
    }

    @Operation(operationId = "adminUserDelete", summary = "Remove o administrador")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            operationId = "adminUserChangePassword",
            summary = "Troca a senha local do administrador",
            description = "Só o próprio administrador troca a senha: o id da rota tem que ser o do token (**403** para "
                    + "outro id). Exige a senha atual correta no corpo e devolve **400** quando ela não confere ou quando "
                    + "o administrador ainda não tem senha local cadastrada. Em caso de sucesso não há corpo na resposta.")
    @PreAuthorize("@ownership.isCurrentAdmin(#id)")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody AdminUserPasswordChangeRequest request) {
        adminUserService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
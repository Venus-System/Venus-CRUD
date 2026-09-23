package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserRequest;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.service.jpa.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Cadastro, busca, ciclo de vida, avatar e perfil completo do usuário.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(operationId = "userFindAll", summary = "Lista os usuários")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(
            operationId = "userSearch",
            summary = "Busca os usuários com filtros e paginação",
            description = "`firebaseUid` tem precedência sobre os demais filtros e devolve no máximo um registro. `status` "
                    + "e `name` combinam entre si; `name` casa por trecho, sem diferenciar maiúsculas.")
    @GetMapping("/search")
    public ResponseEntity<Slice<UserResponse>> search(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String name,
            @Parameter(description = "UID do Firebase. Tem precedência sobre os outros filtros e devolve no máximo um registro.")
            @RequestParam(required = false) String firebaseUid,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.search(status, name, firebaseUid, pageable));
    }

    @Operation(operationId = "userFindById", summary = "Busca o usuário por id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(operationId = "userCreate", summary = "Cadastra um usuário")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "userUpdate", summary = "Substitui os dados do usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @Operation(operationId = "userPatch", summary = "Atualiza parcialmente o usuário")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patch(@PathVariable Long id, @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patch(id, request));
    }

    @Operation(operationId = "userDelete", summary = "Remove o usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
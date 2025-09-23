package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.web.dto.*;
import com.rafadev.teamops.security.PasswordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordService password;

    public UserController(UserRepository users, RoleRepository roles, PasswordService password) {
        this.users = users; this.roles = roles; this.password = password;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UserDto> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        var p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return users.findAll(p).map(this::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDto get(@PathVariable UUID id) {
        return users.findById(id).map(this::toDto).orElseThrow();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Transactional
    public UserDto create(@Valid @RequestBody CreateUserDto in) {
        var u = new User();
        u.setFullName(in.fullName());
        u.setCpf(in.cpf());
        u.setEmail(in.email());
        u.setCargo(in.cargo());
        u.setPasswordHash(password.hash(in.password()));
        u.setEnabled(true);
        u.setRoles(resolveRoles(in.roles()));
        users.save(u);
        return toDto(u);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public UserDto update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto in) {
        var u = users.findById(id).orElseThrow();
        u.setFullName(in.fullName());
        u.setCpf(in.cpf());
        u.setEmail(in.email());
        u.setCargo(in.cargo());
        if (in.enabled() != null) u.setEnabled(in.enabled());
        u.setRoles(resolveRoles(in.roles()));
        return toDto(u);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { users.deleteById(id); }

    private Set<Role> resolveRoles(Set<String> names) {
        return names.stream()
                .map(n -> roles.findByName(n).orElseThrow(() -> new IllegalArgumentException("Role not found: "+n)))
                .collect(Collectors.toSet());
    }

    private UserDto toDto(User u) {
        return new UserDto(
                u.getId(), u.getFullName(), u.getCpf(), u.getEmail(), u.getCargo(), u.isEnabled(),
                u.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
}

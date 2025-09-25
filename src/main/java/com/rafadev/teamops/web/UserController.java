package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.RoleRepository;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.security.PasswordService;
import com.rafadev.teamops.web.dto.CreateUserDto;
import com.rafadev.teamops.web.dto.UpdateUserDto;
import com.rafadev.teamops.web.dto.UserDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordService password;

    public UserController(UserRepository users, RoleRepository roles, PasswordService password) {
        this.users = users;
        this.roles = roles;
        this.password = password;
    }

    // ---------- READ ----------

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
        var u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return toDto(u);
    }

    // ---------- CREATE ----------

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Transactional
    public UserDto create(@Valid @RequestBody CreateUserDto in) {
        // unicidade (409)
        if (users.findByLogin(in.login()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já em uso.");
        }
        if (users.findByEmail(in.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já em uso.");
        }
        // requer UserRepository.existsByCpf(String)
        if (users.existsByCpf(in.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já em uso.");
        }

        var u = new User();
        u.setFullName(in.fullName());
        u.setCpf(in.cpf());
        u.setEmail(in.email());
        u.setCargo(in.cargo());
        u.setLogin(in.login());
        u.setPasswordHash(password.hash(in.password())); // BCrypt
        u.setRoles(resolveRoles(in.roles()));

        users.save(u);
        return toDto(u);
    }

    // ---------- UPDATE ----------

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public UserDto update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto in) {
        var u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // unicidade (exclui o próprio id)
        if (users.existsByLoginAndIdNot(in.login(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já em uso.");
        }
        if (users.existsByEmailAndIdNot(in.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já em uso.");
        }
        if (users.existsByCpfAndIdNot(in.cpf(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já em uso.");
        }

        // campos básicos
        u.setFullName(in.fullName());
        u.setCpf(in.cpf());
        u.setEmail(in.email());
        u.setCargo(in.cargo());
        u.setLogin(in.login());
        u.setRoles(resolveRoles(in.roles()));

        // senha (opcional): não aceitar vazia nem igual à atual
        if (in.password() != null) {
            var newPass = in.password().trim();
            if (newPass.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha não pode ser vazia.");
            }
            if (password.matches(newPass, u.getPasswordHash())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha não pode ser igual à atual.");
            }
            u.setPasswordHash(password.hash(newPass));
        }

        users.save(u); // explícito
        return toDto(u);
    }

    // ---------- DELETE ----------

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!users.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        users.deleteById(id);
    }

    // ---------- helpers ----------

    private Set<Role> resolveRoles(Set<String> names) {
        return names.stream()
                .map(n -> roles.findByName(n)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role não encontrada: " + n)))
                .collect(Collectors.toSet());
    }

    private UserDto toDto(User u) {
        return new UserDto(
                u.getId(),
                u.getFullName(),
                u.getLogin(),
                u.getCpf(),
                u.getEmail(),
                u.getCargo(),
                true, // enabled
                u.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
}

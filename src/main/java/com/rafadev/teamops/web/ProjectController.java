package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.domain.ProjectStatus;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.ProjectRepository;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.web.dto.ProjectDto;
import com.rafadev.teamops.web.dto.ProjectIn;
import com.rafadev.teamops.web.dto.UpdateProjectDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
@SecurityRequirement(name = "bearer-jwt")
public class ProjectController {

    private final ProjectRepository projects;
    private final UserRepository users;

    public ProjectController(ProjectRepository projects, UserRepository users) {
        this.projects = projects;
        this.users = users;
    }

    //                    CREATE
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    @Transactional
    public ProjectDto create(@Valid @RequestBody ProjectIn in,
                             @AuthenticationPrincipal Jwt jwt) {

        var caller = getCaller(jwt);
        ensureManagerOrAdmin(caller);

        String name = in.name().trim();
        if (projects.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um projeto com este nome.");
        }

        LocalDate start = in.startDate();
        LocalDate endPlanned = in.endDatePlanned();
        if (endPlanned.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "endDatePlanned não pode ser anterior a startDate.");
        }

        var manager = users.findByLogin(in.managerLogin().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gerente (login) não encontrado"));
        ensureHasRoleManager(manager);

        if (manager.getId().equals(caller.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Você não pode ser o gerente do próprio projeto.");
        }

        var p = new Project();
        p.setName(name);
        p.setDescription(in.description().trim());
        p.setStartDate(start);
        p.setEndDatePlanned(endPlanned);
        p.setStatus(ProjectStatus.valueOf(in.status()));
        p.setManager(manager);

        projects.save(p);
        return toDto(p);
    }

    //                      UPDATE
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    @Transactional
    public ProjectDto update(@PathVariable UUID id,
                             @Valid @RequestBody UpdateProjectDto in,
                             @AuthenticationPrincipal Jwt jwt) {

        var caller = getCaller(jwt);
        ensureManagerOrAdmin(caller);

        var p = projects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        String newName = in.name().trim();
        if (projects.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um projeto com este nome.");
        }

        LocalDate start = in.startDate();
        LocalDate endPlanned = in.endDatePlanned();
        if (endPlanned.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "endDatePlanned não pode ser anterior a startDate.");
        }

        p.setName(newName);
        p.setDescription(in.description().trim());
        p.setStartDate(start);
        p.setEndDatePlanned(endPlanned);
        p.setStatus(ProjectStatus.valueOf(in.status()));

        // Trocar gerente APENAS se ADMIN e se veio managerLogin
        if (in.managerLogin() != null && !in.managerLogin().isBlank()) {
            ensureIsAdmin(caller);

            var newManager = users.findByLogin(in.managerLogin().trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gerente (login) não encontrado"));

            // === Consistência 2: novo gerente precisa ser MANAGER ===
            ensureHasRoleManager(newManager);

            // === Consistência 3: ADMIN não pode se colocar como gerente do próprio projeto ===
            if (newManager.getId().equals(caller.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Você não pode ser o gerente do próprio projeto.");
            }

            p.setManager(newManager);
        }

        projects.save(p);
        return toDto(p);
    }

    //                    READ

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLLABORATOR')")
    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable UUID id) {
        var p = projects.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        return toDto(p);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLLABORATOR')")
    @GetMapping
    public Page<ProjectDto> list(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        var pr = PageRequest.of(page, size, Sort.by("startDate").descending());
        return projects.findAll(pr).map(this::toDto);
    }


    private User getCaller(Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário do token não encontrado"));
    }

    private void ensureManagerOrAdmin(User u) {
        boolean ok = u.getRoles().stream()
                .anyMatch(r -> "MANAGER".equals(r.getName()) || "ADMIN".equals(r.getName()));
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas MANAGER/ADMIN podem criar/editar projetos.");
        }
    }

    private void ensureHasRoleManager(User u) {
        boolean ok = u.getRoles().stream().anyMatch(r -> "MANAGER".equals(r.getName()));
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Usuário indicado não possui role MANAGER.");
        }
    }

    private void ensureIsAdmin(User u) {
        boolean ok = u.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas ADMIN pode trocar o gerente do projeto.");
        }
    }

    private ProjectDto toDto(Project p) {
        var m = p.getManager();
        return new ProjectDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStartDate(),
                p.getEndDatePlanned(),
                p.getStatus().name(),
                m.getFullName(),
                m.getLogin(),
                m.getEmail()
        );
    }
}

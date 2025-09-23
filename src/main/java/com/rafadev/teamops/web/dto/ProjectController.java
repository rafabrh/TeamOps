package com.rafadev.teamops.dto;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.repository.ProjectRepository;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.web.dto.ProjectDto;
import com.rafadev.teamops.web.dto.ProjectIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
@SecurityRequirement(name = "bearer-jwt")
public class ProjectController {
    private final ProjectRepository projects;
    private final UserRepository users;

    public ProjectController(ProjectRepository projects, UserRepository users) {
        this.projects = projects; this.users = users;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    @Transactional
    public ProjectDto create(@Valid @RequestBody ProjectIn in) {
        var manager = users.findById(in.managerId()).orElseThrow();
        var p = new Project();
        p.setName(in.name());
        p.setDescription(in.description());
        p.setStartDate(in.startDate());
        p.setEndDatePlanned(in.endDatePlanned());
        p.setStatus(in.status());
        p.setManager(manager);
        projects.save(p);
        return toDto(p);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    @Transactional
    public ProjectDto update(@PathVariable UUID id, @Valid @RequestBody ProjectIn in) {
        var p = projects.findById(id).orElseThrow();
        var manager = users.findById(in.managerId()).orElseThrow();
        p.setName(in.name());
        p.setDescription(in.description());
        p.setStartDate(in.startDate());
        p.setEndDatePlanned(in.endDatePlanned());
        p.setStatus(in.status());
        p.setManager(manager);
        return toDto(p);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLLAB')")
    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable UUID id) {
        var p = projects.findById(id).orElseThrow();
        return toDto(p);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLLAB')")
    @GetMapping
    public Page<ProjectDto> list(@RequestParam(defaultValue="0") int page,
                                 @RequestParam(defaultValue="20") int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return projects.findAll(pr).map(this::toDto);
    }

    private ProjectDto toDto(Project p) {
        return new ProjectDto(p.getId(), p.getName(), p.getDescription(), p.getStartDate(),
                p.getEndDatePlanned(), p.getStatus(), p.getManager().getId(), p.getManager().getFullName());
    }
}

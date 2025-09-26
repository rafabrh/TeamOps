package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Team;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.repository.ProjectRepository;
import com.rafadev.teamops.shared.IdOrCodeResolver;
import com.rafadev.teamops.web.dto.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/teams")
@SecurityRequirement(name = "bearer-jwt")
public class TeamController {

    private final TeamRepository teams;
    private final UserRepository users;
    private final ProjectRepository projects;
    private final IdOrCodeResolver resolver;

    public TeamController(TeamRepository teams,
                          UserRepository users,
                          ProjectRepository projects,
                          IdOrCodeResolver resolver) {
        this.teams = teams;
        this.users = users;
        this.projects = projects;
        this.resolver = resolver;
    }

    // ---------- CRUD básico ----------

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    @Transactional
    public TeamDto create(@Valid @RequestBody TeamIn in) {
        var t = new Team();
        t.setName(in.name());
        t.setDescription(in.description());

        // Mantém compat: TeamIn.memberIds() é Set<UUID>
        if (in.memberIds() != null && !in.memberIds().isEmpty()) {
            var members = new HashSet<User>(users.findAllById(in.memberIds()));
            t.setMembers(members);
        }

        teams.save(t);
        return toDto(t);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{teamIdOrCode}")
    @Transactional
    public TeamDto update(@PathVariable String teamIdOrCode, @Valid @RequestBody TeamIn in) {
        var t = resolver.resolveTeam(teamIdOrCode);
        t.setName(in.name());
        t.setDescription(in.description());

        var newMembers = new HashSet<User>();
        if (in.memberIds() != null && !in.memberIds().isEmpty()) {
            newMembers.addAll(users.findAllById(in.memberIds()));
        }
        t.setMembers(newMembers);
        return toDto(t);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLABORADOR')")
    @GetMapping("/{teamIdOrCode}")
    public TeamDto get(@PathVariable String teamIdOrCode) {
        var t = resolver.resolveTeam(teamIdOrCode);
        return toDto(t);
    }

    // ---------- 4.2 Adicionar membros (UUID ou code) ----------
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{teamIdOrCode}/members")
    @Transactional
    public ResponseEntity<Void> addMembers(@PathVariable String teamIdOrCode,
                                           @Valid @RequestBody AddMembersRequest in) {
        var team = resolver.resolveTeam(teamIdOrCode);
        var addUsers = new HashSet<>(resolver.resolveUsers(in.getUserIds())); // <-- getter
        team.getMembers().addAll(addUsers);
        teams.save(team);
        return ResponseEntity.ok().build();
    }

    // ---------- 4.3 Remover membro (UUID ou code) ----------
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{teamIdOrCode}/members/{userIdOrCode}")
    @Transactional
    public ResponseEntity<Void> removeMember(@PathVariable String teamIdOrCode,
                                             @PathVariable String userIdOrCode) {
        var team = resolver.resolveTeam(teamIdOrCode);
        var user = resolver.resolveUser(userIdOrCode);
        team.getMembers().remove(user);
        teams.save(team);
        return ResponseEntity.noContent().build();
    }

    // ---------- 4.4 Vincular equipe a projeto (UUID ou code) ----------
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{teamIdOrCode}/projects")
    @Transactional
    public ResponseEntity<Void> linkProjects(@PathVariable String teamIdOrCode,
                                             @Valid @RequestBody LinkProjectsRequest in) {
        var team = resolver.resolveTeam(teamIdOrCode);
        var toLink = new HashSet<Project>(resolver.resolveProjects(in.getProjectIds())); // <-- getter
        team.getProjects().addAll(toLink);
        teams.save(team);
        return ResponseEntity.ok().build();
    }

    // ---------- util ----------
    private TeamDto toDto(Team t) {
        var memberIds = t.getMembers().stream().map(User::getId).collect(Collectors.toSet());
        return new TeamDto(t.getId(), t.getName(), t.getDescription(), memberIds);
    }
}

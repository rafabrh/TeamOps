package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.domain.Team;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.ProjectRepository;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.shared.IdOrCodeResolver;
import com.rafadev.teamops.web.dto.AddMembersRequest;
import com.rafadev.teamops.web.dto.TeamDto;
import com.rafadev.teamops.web.dto.TeamIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/teams")
@SecurityRequirement(name = "bearer-jwt")
public class TeamController {

    private final TeamRepository teams;
    private final UserRepository users;
    private final IdOrCodeResolver resolver;

    public TeamController(TeamRepository teams,
                          UserRepository users,
                          ProjectRepository projects,
                          IdOrCodeResolver resolver) {
        this.teams = teams;
        this.users = users;
        this.resolver = resolver;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    @Transactional
    public TeamDto create(@Valid @RequestBody TeamIn in) {
        var t = new Team();
        t.setName(in.name());
        t.setDescription(in.description());

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

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{teamIdOrCode}/members")
    @Transactional
    public ResponseEntity<Void> addMembers(@PathVariable String teamIdOrCode,
                                           @Valid @RequestBody AddMembersRequest in) {
        var team = resolver.resolveTeam(teamIdOrCode);

        var requested = in.getLogins().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (requested.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Lista de logins vazia.");
        }

        var found = users.findByLoginIn(requested);
        var foundLogins = found.stream().map(User::getLogin).collect(Collectors.toSet());

        var missing = requested.stream().filter(l -> !foundLogins.contains(l)).toList();
        if (!missing.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Logins inexistentes: " + String.join(", ", missing));
        }

        var already = found.stream()
                .filter(u -> team.getMembers().contains(u))
                .map(User::getLogin)
                .toList();
        if (!already.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "Já são membros deste time: " + String.join(", ", already));
        }

        team.getMembers().addAll(found);
        teams.save(team);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{teamIdOrCode}/members/{login}")
    @Transactional
    public ResponseEntity<Void> removeMember(@PathVariable String teamIdOrCode,
                                             @PathVariable String login) {
        var team = resolver.resolveTeam(teamIdOrCode);
        var user = users.findByLogin(login.trim())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Login não encontrado: " + login));

        if (!team.getMembers().remove(user)) {
            throw new ResponseStatusException(NOT_FOUND, "Usuário não é membro do time: " + login);
        }
        teams.save(team);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{teamIdOrCode}/projects")
    @Transactional
    public ResponseEntity<Void> linkProjects(@PathVariable String teamIdOrCode,
                                             @Valid @RequestBody com.rafadev.teamops.web.dto.LinkProjectsRequest in) {
        var team = resolver.resolveTeam(teamIdOrCode);
        var toLink = new HashSet<Project>(resolver.resolveProjects(in.getProjectIds())); // mantém por id/code
        team.getProjects().addAll(toLink);
        teams.save(team);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<TeamDto> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        var pr = PageRequest.of(page, size, Sort.by("name").ascending());
        return teams.findAll(pr).map(this::toDto);
    }

    private TeamDto toDto(Team t) {
        var memberIds = t.getMembers().stream().map(User::getId).collect(Collectors.toSet());
        return new TeamDto(t.getId(), t.getName(), t.getDescription(), memberIds);
    }
}

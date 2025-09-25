package com.rafadev.teamops.web;

import com.rafadev.teamops.domain.Team;
import com.rafadev.teamops.repository.UserRepository;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.web.dto.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

    public TeamController(TeamRepository teams, UserRepository users) {
        this.teams = teams; this.users = users;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    @Transactional
    public TeamDto create(@Valid @RequestBody TeamIn in) {
        var t = new Team();
        t.setName(in.name());
        t.setDescription(in.description());
        if (in.memberIds() != null && !in.memberIds().isEmpty()) {
            var members = new HashSet<>(users.findAllById(in.memberIds()));
            t.setMembers(members);
        }
        teams.save(t);
        return toDto(t);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    @Transactional
    public TeamDto update(@PathVariable UUID id, @Valid @RequestBody TeamIn in) {
        var t = teams.findById(id).orElseThrow();
        t.setName(in.name());
        t.setDescription(in.description());
        var members = new HashSet<>(users.findAllById(in.memberIds()==null? Set.<UUID>of() : in.memberIds()));
        t.setMembers(members);
        return toDto(t);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','COLLAB')")
    @GetMapping("/{id}")
    public TeamDto get(@PathVariable UUID id) {
        return teams.findById(id).map(this::toDto).orElseThrow();
    }

    private TeamDto toDto(Team t) {
        var memberIds = t.getMembers().stream().map(u -> u.getId()).collect(Collectors.toSet());
        return new TeamDto(t.getId(), t.getName(), t.getDescription(), memberIds);
    }
}

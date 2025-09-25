package com.rafadev.teamops.shared;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.repository.ProjectRepository;
import com.rafadev.teamops.domain.Team;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class IdOrCodeResolver {

    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final TeamRepository teamRepo;

    public IdOrCodeResolver(UserRepository u, ProjectRepository p, TeamRepository t) {
        this.userRepo = u; this.projectRepo = p; this.teamRepo = t;
    }

    private boolean isUUID(String s) {
        try { UUID.fromString(s); return true; } catch (Exception e) { return false; }
    }

    public User resolveUser(String idOrCode) {
        return isUUID(idOrCode) ? userRepo.findById(UUID.fromString(idOrCode)).orElseThrow()
                : userRepo.findByCode(idOrCode).orElseThrow();
    }

    public Project resolveProject(String idOrCode) {
        return isUUID(idOrCode) ? projectRepo.findById(UUID.fromString(idOrCode)).orElseThrow()
                : projectRepo.findByCode(idOrCode).orElseThrow();
    }

    public Team resolveTeam(String idOrCode) {
        return isUUID(idOrCode) ? teamRepo.findById(UUID.fromString(idOrCode)).orElseThrow()
                : teamRepo.findByCode(idOrCode).orElseThrow();
    }

    public List<User> resolveUsers(Collection<String> idsOrCodes) {
        return idsOrCodes.stream().map(this::resolveUser).collect(Collectors.toList());
    }

    public List<Project> resolveProjects(Collection<String> idsOrCodes) {
        return idsOrCodes.stream().map(this::resolveProject).collect(Collectors.toList());
    }
}

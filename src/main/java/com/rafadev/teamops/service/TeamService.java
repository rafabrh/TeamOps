package com.rafadev.teamops.service;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.domain.Team;
import com.rafadev.teamops.repository.TeamRepository;
import com.rafadev.teamops.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepo;

    public TeamService(TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
    }

    @Transactional
    public void addMembers(Team team, List<User> users) {
        for (User u : users) {
            team.addMember(u); // implemente addMember na entidade Team (Set<User> members)
        }
        teamRepo.save(team);
    }

    @Transactional
    public void removeMember(Team team, User user) {
        team.removeMember(user); // implemente removeMember na entidade Team
        teamRepo.save(team);
    }

    @Transactional
    public void linkProjects(Team team, List<Project> projects) {
        for (Project p : projects) {
            team.addProject(p); // implemente addProject na entidade Team (Set<Project> projects)
        }
        teamRepo.save(team);
    }
}
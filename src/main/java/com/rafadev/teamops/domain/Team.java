package com.rafadev.teamops.domain;

import com.rafadev.teamops.shared.ShortCodeGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(name = "uk_team_name", columnNames = "name"))
public class Team {
    @Setter
    @Getter
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, updatable = false, length = 16)
    private String code;

    @Setter
    @Getter
    @Column(nullable = false, length = 120)
    private String name;

    @PrePersist
    public void prePersist() {
        if (this.code == null) this.code = ShortCodeGenerator.generate("TEAM", 6);
    }

    public void addMember(User u)   {
        this.members.add(u); }

    public void removeMember(User u){
        this.members.remove(u); }

    public void addProject(Project p){
        this.projects.add(p); }

    @Setter
    @Getter
    @Column(nullable = false, length = 500)
    private String description;

    @Setter
    @Getter
    @ManyToMany
    @JoinTable(name = "team_members",
            joinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_tm_team")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tm_user")))
    private Set<User> members = new HashSet<>();

    @Setter
    @Getter
    @ManyToMany
    @JoinTable(name = "project_teams",
            joinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_pt_team")),
            inverseJoinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_pt_project")))
    private Set<Project> projects = new HashSet<>();

    @Getter
    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Getter
    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

}

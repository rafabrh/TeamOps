package com.rafadev.teamops.domain;

import com.rafadev.teamops.domain.Project;
import com.rafadev.teamops.domain.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(name = "uk_team_name", columnNames = "name"))
public class Team {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToMany
    @JoinTable(name = "team_members",
            joinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_tm_team")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tm_user")))
    private Set<User> members = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "project_teams",
            joinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_pt_team")),
            inverseJoinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_pt_project")))
    private Set<Project> projects = new HashSet<>();

    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

    // getters/setters
    public UUID getId() {
        return id; }

    public void setId(UUID id) {
        this.id = id; }

    public String getName() {
        return name; }

    public void setName(String name) {
        this.name = name; }

    public String getDescription() {
        return description; }

    public void setDescription(String description) {
        this.description = description; }

    public Set<User> getMembers() {
        return members; }

    public void setMembers(Set<User> members) {
        this.members = members; }

    public Set<Project> getProjects() {
        return projects; }

    public void setProjects(Set<Project> projects) {
        this.projects = projects; }

    public OffsetDateTime getCreatedAt() {
        return createdAt; }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt; }
}

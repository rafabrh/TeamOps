package com.rafadev.teamops.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDatePlanned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status; // PLANNED, IN_PROGRESS, DONE, CANCELED

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_project_manager"))
    private User manager;

    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

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

    public LocalDate getStartDate() {
        return startDate; }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate; }

    public LocalDate getEndDatePlanned() {
        return endDatePlanned; }

    public void setEndDatePlanned(LocalDate endDatePlanned) {
        this.endDatePlanned = endDatePlanned; }

    public ProjectStatus getStatus() {
        return status; }

    public void setStatus(ProjectStatus status) {
        this.status = status; }

    public User getManager() {
        return manager; }

    public void setManager(User manager) {
        this.manager = manager; }

    public OffsetDateTime getCreatedAt() {
        return createdAt; }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt; }
}

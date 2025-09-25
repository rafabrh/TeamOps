package com.rafadev.teamops.domain;

import com.rafadev.teamops.shared.ShortCodeGenerator;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
public class Project {
    @Setter
    @Getter
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, updatable = false, length = 16)
    private String code;

    @Setter
    @Getter
    @Column(nullable = false, length = 120)
    private String name;

    @PrePersist
    public void prePersist() {
        if (this.code == null) this.code = ShortCodeGenerator.generate("PRJ", 6);
    }

    @Setter
    @Getter
    @Column(nullable = false, length = 1000)
    private String description;

    @Setter
    @Getter
    @Column(nullable = false)
    private LocalDate startDate;

    @Setter
    @Getter
    @Column
    private LocalDate endDatePlanned;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status; // PLANNED, IN_PROGRESS, DONE, CANCELED

    @Setter
    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_project_manager"))
    private User manager;

    @Getter
    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Getter
    @Column(nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

}

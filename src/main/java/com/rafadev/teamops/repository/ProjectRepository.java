package com.rafadev.teamops.repository;

import com.rafadev.teamops.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = "manager")
    Page<Project> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "manager")
    Optional<Project> findById(UUID id);

    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    Optional<Project> findByCode(String code);
}

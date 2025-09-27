package com.rafadev.teamops.repository;

import com.rafadev.teamops.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    @EntityGraph(attributePaths = "members")
    Page<Team> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "members")
    Optional<Team> findById(UUID id);

    @EntityGraph(attributePaths = "members")
    Optional<Team> findByCode(String code);

}
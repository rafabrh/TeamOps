package com.rafadev.teamops.repository;

import com.rafadev.teamops.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByCode(String code);

}
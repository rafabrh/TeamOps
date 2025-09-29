package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record TeamDto(UUID id, @NotBlank String name, @NotBlank String description, Set<UUID> memberIds) {}
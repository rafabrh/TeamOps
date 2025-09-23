package com.rafadev.teamops.web.dto;

import java.util.Set;
import java.util.UUID;

public record TeamDto(UUID id, String name, String description, Set<UUID> memberIds) {}
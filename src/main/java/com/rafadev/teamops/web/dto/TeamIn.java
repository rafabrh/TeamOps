package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

public record TeamIn(@NotBlank String name, @NotBlank String description, Set<UUID> memberIds) {}
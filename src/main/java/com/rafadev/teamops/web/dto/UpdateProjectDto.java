package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateProjectDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDatePlanned,
        @Pattern(regexp="PLANNED|IN_PROGRESS|DONE|CANCELED") String status,
        String managerLogin
) {}

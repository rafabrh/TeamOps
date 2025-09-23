package com.rafadev.teamops.web.dto;


import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectIn(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull LocalDate startDate,
        LocalDate endDatePlanned,
        @Pattern(regexp="PLANNED|IN_PROGRESS|DONE|CANCELED") String status,
        @NotNull UUID managerId
) {}
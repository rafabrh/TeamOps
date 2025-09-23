package com.rafadev.teamops.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectDto(
        UUID id, String name, String description, LocalDate startDate, LocalDate endDatePlanned,
        String status, UUID managerId, String managerName
) {}
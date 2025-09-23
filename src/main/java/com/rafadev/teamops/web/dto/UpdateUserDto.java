package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public record UpdateUserDto(
        @NotBlank String fullName,
        @Pattern(regexp="^[0-9\\.\\-]{11,14}$", message="CPF inválido") String cpf,
        @NotBlank @Email String email,
        @NotBlank String cargo,
        Boolean enabled,
        @NotEmpty Set<@Pattern(regexp="ADMIN|MANAGER|COLLAB") String> roles
) {}

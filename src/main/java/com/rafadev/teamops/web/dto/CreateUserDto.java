package com.rafadev.teamops.web.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public record CreateUserDto(
        @NotBlank String fullName,
        @Pattern(regexp = "^[0-9\\.\\-]{11,14}$", message = "CPF inválido") String cpf,
        @NotBlank @Email String email,
        @NotBlank String cargo,
        @NotBlank String login,
        @Size(min = 6) String password,
        @NotEmpty Set<@Pattern(regexp = "ADMIN|MANAGER|COLABORADOR") String> roles
) {}
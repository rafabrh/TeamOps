package com.rafadev.teamops.web.dto;

import java.util.Set;
import java.util.UUID;

public record UserDto(UUID id, String fullName, String cpf, String email, String cargo, boolean enabled, Set<String> roles) {}

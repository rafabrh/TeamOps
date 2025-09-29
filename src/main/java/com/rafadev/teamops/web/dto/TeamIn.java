package com.rafadev.teamops.web.dto;

import java.util.Set;
import java.util.UUID;

public record TeamIn( String name, String description, Set<UUID> memberIds) {}
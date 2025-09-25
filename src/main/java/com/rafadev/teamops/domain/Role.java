package com.rafadev.teamops.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String name;

}

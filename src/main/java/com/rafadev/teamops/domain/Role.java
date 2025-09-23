package com.rafadev.teamops.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    public UUID getId() {
        return id; }

    public void setId(UUID id) {
        this.id = id; }

    public String getName() {
        return name; }

    public void setName(String name) {
        this.name = name; }
}

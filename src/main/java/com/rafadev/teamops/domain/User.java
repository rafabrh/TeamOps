package com.rafadev.teamops.domain;

import com.rafadev.teamops.shared.ShortCodeGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Setter
    @Getter
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, updatable = false, length = 16)
    private String code;

    @Getter
    @Setter
    @Column(nullable = false, length = 120)
    private String nome;

    @PrePersist
    public void prePersist() {
        if (this.code == null) this.code = ShortCodeGenerator.generate("USR", 6);
    }

    @Column(nullable = false, length = 160)
    private String fullName;

    @Setter
    @Getter
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Setter
    @Getter
    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Setter
    @Getter
    @Column(length = 80)
    private String cargo;

    @Setter
    @Getter
    @Column(nullable = false, unique = true, length = 80)
    private String login;

    @Setter
    @Getter
    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Setter
    @Getter
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public String getFullName() {
        return this.fullName != null ? this.fullName : this.nome;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.nome = fullName;
    }

}

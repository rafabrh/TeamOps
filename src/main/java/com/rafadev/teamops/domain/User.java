package com.rafadev.teamops.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 80)
    private String cargo;

    @Column(nullable = false, unique = true, length = 80)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public UUID getId() {
        return id; }

    public void setId(UUID id) {
        this.id = id; }

    public String getNome() {
        return nome; }

    public void setNome(String nome) {
        this.nome = nome; }

    public String getCpf() {
        return cpf; }

    public void setCpf(String cpf) {
        this.cpf = cpf; }

    public String getEmail() {
        return email; }

    public void setEmail(String email) {
        this.email = email; }

    public String getCargo() {
        return cargo; }

    public void setCargo(String cargo) {
        this.cargo = cargo; }

    public String getLogin() {
        return login; }

    public void setLogin(String login) {
        this.login = login; }

    public String getPasswordHash() {
        return passwordHash; }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash; }

    public OffsetDateTime getCreatedAt() {
        return createdAt; }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt; }

    public Set<Role> getRoles() {
        return roles; }

    public void setRoles(Set<Role> roles) {
        this.roles = roles; }
}

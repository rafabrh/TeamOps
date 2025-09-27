package com.rafadev.teamops.security;

import com.rafadev.teamops.domain.Role;
import com.rafadev.teamops.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.user = Objects.requireNonNull(user, "user");
        this.authorities = mapAuthorities(user.getRoles());
    }

    private Set<GrantedAuthority> mapAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> {
                    String name = role.getName(); // supondo getName()
                    if (name == null || name.isBlank()) return null;
                    String granted = name.startsWith("ROLE_") ? name : "ROLE_" + name;
                    return new SimpleGrantedAuthority(granted);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public User getDomainUser() {
        return user;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; }

    @Override public String getPassword() {
        return user.getPasswordHash(); }

    @Override public String getUsername() {
        return user.getLogin(); }

    @Override public boolean isAccountNonExpired() {
        return true; }

    @Override public boolean isAccountNonLocked() {
        return true; }

    @Override public boolean isCredentialsNonExpired() {
        return true; }

    @Override public boolean isEnabled() {
        return true; }
}

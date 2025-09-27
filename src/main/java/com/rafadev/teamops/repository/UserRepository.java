package com.rafadev.teamops.repository;

import com.rafadev.teamops.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);

    Optional<User> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByLoginAndIdNot(String login, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    List<User> findByLoginIn(Collection<String> logins);



}

